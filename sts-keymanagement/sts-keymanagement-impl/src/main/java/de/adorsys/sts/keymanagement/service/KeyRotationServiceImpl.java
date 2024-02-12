package de.adorsys.sts.keymanagement.service;

import com.google.common.collect.Streams;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.attribute.support.SimpleFunction;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.option.QueryOptions;
import de.adorsys.keymanagement.api.types.ResultCollection;
import de.adorsys.keymanagement.api.types.entity.KeyAlias;
import de.adorsys.keymanagement.api.types.entity.KeyEntry;
import de.adorsys.keymanagement.api.view.EntryView;
import de.adorsys.sts.keymanagement.config.KeyManagementRotationProperties;
import de.adorsys.sts.keymanagement.model.*;
import de.adorsys.sts.keymanagement.util.DateTimeUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.googlecode.cqengine.query.QueryFactory.*;

public class KeyRotationServiceImpl implements KeyRotationService {

    private final SimpleFunction<KeyEntry, StsKeyEntry> STS = it -> ((StsKeyEntry) it.getMeta());

    private final Attribute<KeyEntry, KeyState> STATE = new SimpleAttribute<>() {
        @Override
        public KeyState getValue(KeyEntry o, QueryOptions queryOptions) {
            return STS.apply(o).getState();
        }
    };

    private final Attribute<KeyEntry, Instant> NOT_BEFORE = new SimpleAttribute<>() {
        @Override
        public Instant getValue(KeyEntry o, QueryOptions queryOptions) {
            return STS.apply(o).getNotBefore().toInstant();
        }
    };

    private final Attribute<KeyEntry, Instant> NOT_AFTER = new SimpleAttribute<>() {
        @Override
        public Instant getValue(KeyEntry o, QueryOptions queryOptions) {
            return STS.apply(o).getNotAfter().toInstant();
        }
    };

    private final Attribute<KeyEntry, Instant> EXPIRE_AT = new SimpleAttribute<>() {
        @Override
        public Instant getValue(KeyEntry o, QueryOptions queryOptions) {
            return STS.apply(o).getExpireAt().toInstant();
        }
    };

    private final Attribute<KeyEntry, KeyUsage> USAGE = new SimpleAttribute<>() {
        @Override
        public KeyUsage getValue(KeyEntry o, QueryOptions queryOptions) {
            return STS.apply(o).getKeyUsage();
        }
    };


    private final KeyStoreGenerator keyStoreGenerator;
    private final Clock clock;
    private final KeyManagementRotationProperties.KeyRotationProperties encryptionKeyPairRotationProperties;
    private final KeyManagementRotationProperties.KeyRotationProperties signatureKeyPairRotationProperties;
    private final KeyManagementRotationProperties.KeyRotationProperties secretKeyRotationProperties;

    public KeyRotationServiceImpl(
            KeyStoreGenerator keyStoreGenerator,
            Clock clock,
            KeyManagementRotationProperties rotationProperties
    ) {
        this.keyStoreGenerator = keyStoreGenerator;
        this.clock = clock;
        this.encryptionKeyPairRotationProperties = rotationProperties.getEncKeyPairs();
        this.signatureKeyPairRotationProperties = rotationProperties.getSignKeyPairs();
        this.secretKeyRotationProperties = rotationProperties.getSecretKeys();
    }

    @Override
    public KeyRotationResult rotate(StsKeyStore stsKeyStore) {
        ZonedDateTime now = now();
        EntryView<Query<KeyEntry>> view = (EntryView<Query<KeyEntry>>) stsKeyStore.getView();
        Map<KeyUsage, Integer> rotationEnabledForWithCount = rotationEnabledForWithCount();
        Set<KeyUsage> rotationEnabledFor = rotationEnabledForWithCount.keySet();

        List<String> createdFutureKeys = moveCreatedToValidAndReplenish(now, view, rotationEnabledFor);
        moveValidToLegacy(now.toInstant(), view, rotationEnabledFor);
        List<String> dropped = moveLegacyToExpiredAndDrop(now.toInstant(), view, rotationEnabledFor);
        List<String> generatedKeyAliases = generateMissingValid(rotationEnabledForWithCount, view);

        return KeyRotationResult.builder()
                .generatedKeys(generatedKeyAliases)
                .removedKeys(dropped)
                .futureKeys(createdFutureKeys)
                .build();
    }

    private List<String> moveCreatedToValidAndReplenish(ZonedDateTime now, EntryView<Query<KeyEntry>> view,
                                                        Collection<KeyUsage> rotationEnabledForUsages) {
        ResultCollection<KeyEntry> createdToValid = view.retrieve(
                and(
                        equal(STATE, KeyState.CREATED),
                        lessThan(NOT_BEFORE, now.toInstant()),
                        in(USAGE, rotationEnabledForUsages)
                )
        ).toCollection();

        view.update(
                createdToValid.stream()
                        .map(it -> it.aliasWithMeta(StsKeyEntry.class))
                        .map(it -> it.toBuilder().metadata(toValid(now, it.getMetadata())).build())
                        .collect(Collectors.toList())
        );

        List<GeneratedStsEntry> createdKeys = new ArrayList<>();
        for (KeyEntry keyEntry : createdToValid) {
            StsKeyEntry original = (StsKeyEntry) keyEntry.getMeta();
            GeneratedStsEntry generatedKeyEntry = keyStoreGenerator.generateKeyEntryForFutureUsage(
                    original.getKeyUsage(), original.getNotAfter()
            );

            createdKeys.add(generatedKeyEntry);
        }
        view.add(createdKeys.stream().map(it -> it.getKey()).collect(Collectors.toList()));

        return createdKeys.stream().map(it -> it.getEntry().getAlias()).collect(Collectors.toList());
    }

    private void moveValidToLegacy(Instant now, EntryView<Query<KeyEntry>> view,
                                   Collection<KeyUsage> rotationEnabledForUsages) {
        ResultCollection<KeyEntry> expiredValid = view.retrieve(
                and(
                        equal(STATE, KeyState.VALID),
                        lessThan(NOT_AFTER, now),
                        in(USAGE, rotationEnabledForUsages)
                )
        ).toCollection();

        view.update(
                expiredValid.stream()
                        .map(it -> it.aliasWithMeta(StsKeyEntry.class))
                        .map(it -> it.toBuilder().metadata(toLegacy(it.getMetadata())).build())
                        .collect(Collectors.toList())
        );
    }

    private List<String> moveLegacyToExpiredAndDrop(Instant now, EntryView<Query<KeyEntry>> view,
                                                    Collection<KeyUsage> rotationEnabledForUsages) {
        ResultCollection<KeyEntry> legacyExpiredEntries = view.retrieve(
                and(
                        equal(STATE, KeyState.LEGACY),
                        lessThan(EXPIRE_AT, now),
                        in(USAGE, rotationEnabledForUsages)
                )
        ).toCollection();
        view.remove(legacyExpiredEntries);

        ResultCollection<KeyEntry> expired = view.retrieve(
                and(
                        equal(STATE, KeyState.EXPIRED),
                        in(USAGE, rotationEnabledForUsages)
                )
        ).toCollection();
        view.remove(expired);

        return Streams.concat(legacyExpiredEntries.stream(), expired.stream())
                .map(KeyAlias::getAlias)
                .collect(Collectors.toList());
    }

    private List<String> generateMissingValid(Map<KeyUsage, Integer> rotationEnabledForWithCount,
                                              EntryView<Query<KeyEntry>> view) {
        List<GeneratedStsEntry> generatedMissing = new ArrayList<>();
        for (Map.Entry<KeyUsage, Integer> toCheck : rotationEnabledForWithCount.entrySet()) {
            int countValidForUsage = view.retrieve(and(equal(STATE, KeyState.VALID), equal(USAGE, toCheck.getKey())))
                    .toCollection()
                    .size();

            for (int i = 0; i < toCheck.getValue() - countValidForUsage; ++i) {
                generatedMissing.add(generateKey(toCheck.getKey()));
            }
        }

        view.add(generatedMissing.stream().map(it -> it.getKey()).collect(Collectors.toList()));
        return generatedMissing.stream().map(it -> it.getEntry().getAlias()).collect(Collectors.toList());
    }

    private Map<KeyUsage, Integer> rotationEnabledForWithCount() {
        Map<KeyUsage, Integer> result = new HashMap<>();
        if (encryptionKeyPairRotationProperties.isEnabled()) {
            result.put(KeyUsage.Encryption, encryptionKeyPairRotationProperties.getMinKeys());
        }

        if (signatureKeyPairRotationProperties.isEnabled()) {
            result.put(KeyUsage.Signature, signatureKeyPairRotationProperties.getMinKeys());
        }

        if (secretKeyRotationProperties.isEnabled()) {
            result.put(KeyUsage.SecretKey, secretKeyRotationProperties.getMinKeys());
        }

        return result;
    }

    private StsKeyEntry toLegacy(StsKeyEntry entry) {
        entry.setState(KeyState.LEGACY);
        return entry;
    }

    private StsKeyEntry toValid(ZonedDateTime now, StsKeyEntry entry) {
        entry.setNotAfter(DateTimeUtils.addMillis(now, entry.getValidityInterval()));
        entry.setExpireAt(DateTimeUtils.addMillis(now, entry.getLegacyInterval()));
        entry.setState(KeyState.VALID);
        return entry;
    }

    private GeneratedStsEntry generateKey(KeyUsage keyUsage) {
        if (keyUsage == KeyUsage.Signature) {
            return keyStoreGenerator.generateSignatureKeyEntryForInstantUsage();
        } else if(keyUsage == KeyUsage.Encryption) {
            return keyStoreGenerator.generateEncryptionKeyEntryForInstantUsage();
        } else if(keyUsage == KeyUsage.SecretKey) {
            return keyStoreGenerator.generateSecretKeyEntryForInstantUsage();
        }

        throw new IllegalArgumentException("Unknown KeyUsage: " + keyUsage);
    }

    private ZonedDateTime now() {
        return clock.instant().atZone(ZoneOffset.UTC);
    }
}
