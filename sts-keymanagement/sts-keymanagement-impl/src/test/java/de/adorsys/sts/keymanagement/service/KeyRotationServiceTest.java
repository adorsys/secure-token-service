package de.adorsys.sts.keymanagement.service;


import com.nitorcreations.junit.runners.NestedRunner;
import de.adorsys.keymanagement.api.Juggler;
import de.adorsys.keymanagement.api.types.KeySetTemplate;
import de.adorsys.keymanagement.api.types.entity.KeyEntry;
import de.adorsys.keymanagement.api.types.template.ProvidedKeyTemplate;
import de.adorsys.keymanagement.api.types.template.generated.Encrypting;
import de.adorsys.keymanagement.api.types.template.generated.Secret;
import de.adorsys.keymanagement.api.types.template.generated.Signing;
import de.adorsys.keymanagement.api.view.EntryView;
import de.adorsys.keymanagement.config.keystore.KeyStoreConfig;
import de.adorsys.keymanagement.core.metadata.MetadataPersistenceConfig;
import de.adorsys.keymanagement.core.metadata.WithPersister;
import de.adorsys.keymanagement.juggler.services.DaggerBCJuggler;
import de.adorsys.sts.keymanagement.config.KeyManagementRotationProperties;
import de.adorsys.sts.keymanagement.model.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.KeyStore;
import java.security.Security;
import java.time.*;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;

@RunWith(NestedRunner.class)
public class KeyRotationServiceTest {

    private static final String PASSWRD = "password!";

    static final LocalDateTime FIXED_DATE_TIME = LocalDateTime.of(2018, 1, 22, 14, 27, 34);
    static final ZoneId FIXED_ZONE_ID = ZoneOffset.UTC;

    KeyRotationService keyRotationService;

    static final String SIGNATURE_KEY_ALIAS = "signature key";
    static final String ENCRYPTION_KEY_ALIAS = "encryption key";
    static final String SECRET_KEY_ALIAS = "secret key";

    private static final Map<String, StsKeyEntry> MOCKS_BY_ID = new HashMap<>();
    private StsKeyStore stsKeyStore;
    private EntryView<?> view;

    @Mock
    StsKeyEntry signatureKeyEntry;

    @Mock
    StsKeyEntry encryptionKeyEntry;

    @Mock
    StsKeyEntry secretKeyEntry;

    @Mock
    KeyStoreGenerator keyStoreGenerator;

    @Mock
    GeneratedStsEntry generatedEncryptionKeyPair;

    @Mock
    GeneratedStsEntry generatedSignatureKeyPair;

    @Mock
    GeneratedStsEntry generatedSecretKey;

    @Mock
    KeyManagementProperties.KeyStoreProperties keyStoreProperties;

    @Mock
    KeyManagementRotationProperties rotationProperties;

    @Mock
    KeyManagementRotationProperties.KeyRotationProperties encryptionKeyPairRotationProperties;

    @Mock
    KeyManagementRotationProperties.KeyRotationProperties signatureKeyPairRotationProperties;

    @Mock
    KeyManagementRotationProperties.KeyRotationProperties secretKeyRotationProperties;

    @Captor
    ArgumentCaptor<Collection<KeyEntry>> removed;

    ProvidedKeyTemplate genSignatureKeyPair;
    ProvidedKeyTemplate genEncryptionKeyPair;
    ProvidedKeyTemplate genSecretKey;

    final Clock clock = Clock.fixed(FIXED_DATE_TIME.atZone(FIXED_ZONE_ID).toInstant(), FIXED_ZONE_ID);

    @Before
    public void setup() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        MockitoAnnotations.initMocks(this);

        Juggler juggler = DaggerBCJuggler.builder()
                .keyStoreConfig(KeyStoreConfig.builder().type("UBER").build())
                .metadataConfig(
                        MetadataPersistenceConfig.builder()
                                .metadataClass(StsKeyEntryTestable.class)
                                .build()
                )
                .metadataPersister(new WithPersister())
                .build();

        when(signatureKeyEntry.getAlias()).thenReturn(SIGNATURE_KEY_ALIAS);
        when(signatureKeyEntry.getKeyUsage()).thenReturn(KeyUsage.Signature);
        when(signatureKeyEntry.getState()).thenReturn(KeyState.VALID);
        when(signatureKeyEntry.getNotBefore()).thenReturn(FIXED_DATE_TIME.minusSeconds(1).atZone(ZoneOffset.UTC));
        when(signatureKeyEntry.getNotAfter()).thenReturn(FIXED_DATE_TIME.plusSeconds(1).atZone(ZoneOffset.UTC));
        when(signatureKeyEntry.getExpireAt()).thenReturn(FIXED_DATE_TIME.plusSeconds(2).atZone(ZoneOffset.UTC));

        when(encryptionKeyEntry.getAlias()).thenReturn(ENCRYPTION_KEY_ALIAS);
        when(encryptionKeyEntry.getKeyUsage()).thenReturn(KeyUsage.Encryption);
        when(encryptionKeyEntry.getState()).thenReturn(KeyState.VALID);
        when(encryptionKeyEntry.getNotBefore()).thenReturn(FIXED_DATE_TIME.minusSeconds(1).atZone(ZoneOffset.UTC));
        when(encryptionKeyEntry.getNotAfter()).thenReturn(FIXED_DATE_TIME.plusSeconds(1).atZone(ZoneOffset.UTC));
        when(encryptionKeyEntry.getExpireAt()).thenReturn(FIXED_DATE_TIME.plusSeconds(2).atZone(ZoneOffset.UTC));

        when(secretKeyEntry.getAlias()).thenReturn(SECRET_KEY_ALIAS);
        when(secretKeyEntry.getKeyUsage()).thenReturn(KeyUsage.SecretKey);
        when(secretKeyEntry.getState()).thenReturn(KeyState.VALID);
        when(secretKeyEntry.getNotBefore()).thenReturn(FIXED_DATE_TIME.minusSeconds(1).atZone(ZoneOffset.UTC));
        when(secretKeyEntry.getNotAfter()).thenReturn(FIXED_DATE_TIME.plusSeconds(1).atZone(ZoneOffset.UTC));
        when(secretKeyEntry.getExpireAt()).thenReturn(FIXED_DATE_TIME.plusSeconds(2).atZone(ZoneOffset.UTC));

        MOCKS_BY_ID.put(SIGNATURE_KEY_ALIAS, signatureKeyEntry);
        MOCKS_BY_ID.put(ENCRYPTION_KEY_ALIAS, encryptionKeyEntry);
        MOCKS_BY_ID.put(SECRET_KEY_ALIAS, secretKeyEntry);

        KeySetTemplate template = KeySetTemplate.builder()
                .generatedSigningKey(Signing.with()
                        .alias(SIGNATURE_KEY_ALIAS)
                        .metadata(new StsKeyEntryTestable(SIGNATURE_KEY_ALIAS))
                        .build()
                )
                .generatedEncryptionKey(Encrypting.with()
                        .alias(ENCRYPTION_KEY_ALIAS)
                        .metadata(new StsKeyEntryTestable(ENCRYPTION_KEY_ALIAS))
                        .build()
                )
                .generatedSecretKey(Secret.with()
                        .alias(SECRET_KEY_ALIAS)
                        .metadata(new StsKeyEntryTestable(SECRET_KEY_ALIAS))
                        .build()
                )
                .build();
        KeyStore ks = juggler
                .toKeystore()
                .generate(juggler.generateKeys().fromTemplate(template), PASSWRD::toCharArray);

        view = spy(juggler.readKeys().fromKeyStore(ks, id -> PASSWRD.toCharArray()).entries());
        stsKeyStore = StsKeyStore.builder()
                .keyStore(ks)
                .view(view)
                .build();

        genSignatureKeyPair = juggler.generateKeys().signing(Signing.with().password(PASSWRD::toCharArray).build());
        genEncryptionKeyPair = juggler.generateKeys().encrypting(Encrypting.with().password(PASSWRD::toCharArray).build());
        genSecretKey = juggler.generateKeys().secret(Secret.with().password(PASSWRD::toCharArray).build());

        when(generatedSignatureKeyPair.getKey()).thenReturn(genSignatureKeyPair);
        when(generatedSignatureKeyPair.getEntry()).thenReturn(signatureKeyEntry);

        when(generatedEncryptionKeyPair.getKey()).thenReturn(genEncryptionKeyPair);
        when(generatedEncryptionKeyPair.getEntry()).thenReturn(encryptionKeyEntry);

        when(generatedSecretKey.getKey()).thenReturn(genSecretKey);
        when(generatedSecretKey.getEntry()).thenReturn(secretKeyEntry);

        when(keyStoreGenerator.generateEncryptionKeyEntryForInstantUsage()).thenReturn(generatedEncryptionKeyPair);
        when(keyStoreGenerator.generateSignatureKeyEntryForInstantUsage()).thenReturn(generatedSignatureKeyPair);
        when(keyStoreGenerator.generateSecretKeyEntryForInstantUsage()).thenReturn(generatedSecretKey);

        when(encryptionKeyPairRotationProperties.getMinKeys()).thenReturn(1);
        when(signatureKeyPairRotationProperties.getMinKeys()).thenReturn(1);
        when(secretKeyRotationProperties.getMinKeys()).thenReturn(1);

        when(encryptionKeyPairRotationProperties.isEnabled()).thenReturn(true);
        when(signatureKeyPairRotationProperties.isEnabled()).thenReturn(true);
        when(secretKeyRotationProperties.isEnabled()).thenReturn(true);

        when(rotationProperties.getEncKeyPairs()).thenReturn(encryptionKeyPairRotationProperties);
        when(rotationProperties.getSignKeyPairs()).thenReturn(signatureKeyPairRotationProperties);
        when(rotationProperties.getSecretKeys()).thenReturn(secretKeyRotationProperties);

        keyRotationService = new KeyRotationServiceImpl(
                keyStoreGenerator,
                clock,
                keyStoreProperties,
                rotationProperties
        );
    }

    public class KeyStoreWithExpiredSignatureKeyPair {

        private KeyRotationResult keyRotationResult;

        @Before
        public void setup() throws Exception {
            when(signatureKeyEntry.getState()).thenReturn(KeyState.EXPIRED);

            keyRotationResult = keyRotationService.rotate(stsKeyStore);
        }

        @Test
        public void shouldReturnRemovedKeyAliases() {
            List<String> removedKeys = keyRotationResult.getRemovedKeys();

            assertThat(removedKeys, hasSize(1));
            assertThat(removedKeys.get(0), is(equalTo(SIGNATURE_KEY_ALIAS)));
        }

        @Test
        public void shouldReturnGeneratedKeyAliases() {
            List<String> generatedKeys = keyRotationResult.getGeneratedKeys();

            assertThat(generatedKeys, hasSize(1));
        }
    }

    public class KeyStoreWithExpiredEncryptionKeyPair {

        private KeyRotationResult keyRotationResult;

        @Before
        public void setup() throws Exception {
            when(encryptionKeyEntry.getState()).thenReturn(KeyState.EXPIRED);

            keyRotationResult = keyRotationService.rotate(stsKeyStore);
        }

        @Test
        public void shouldReturnRemovedKeyAliases() {
            List<String> removedKeys = keyRotationResult.getRemovedKeys();

            assertThat(removedKeys, hasSize(1));
            assertThat(removedKeys.get(0), is(equalTo(ENCRYPTION_KEY_ALIAS)));
        }

        @Test
        public void shouldReturnGeneratedKeyAliases() {
            List<String> generatedKeys = keyRotationResult.getGeneratedKeys();

            assertThat(generatedKeys, hasSize(1));
        }
    }

    public class KeyStoreWithExpiredSecretKey {

        private KeyRotationResult keyRotationResult;

        @Before
        public void setup() throws Exception {
            when(secretKeyEntry.getState()).thenReturn(KeyState.EXPIRED);

            keyRotationResult = keyRotationService.rotate(stsKeyStore);
        }

        @Test
        public void shouldReturnRemovedKeyAliases() {
            List<String> removedKeys = keyRotationResult.getRemovedKeys();

            assertThat(removedKeys, hasSize(1));
            assertThat(removedKeys.get(0), is(equalTo(SECRET_KEY_ALIAS)));
        }

        @Test
        public void shouldReturnGeneratedKeyAliases() {
            List<String> generatedKeys = keyRotationResult.getGeneratedKeys();

            assertThat(generatedKeys, hasSize(1));
        }
    }

    @Getter
    @RequiredArgsConstructor
    private static class StsKeyEntryTestable implements StsKeyEntry {

        private final String id;

        @Override
        public void setState(KeyState state) {
            getMock().setState(state);
        }

        @Override
        public void setNotAfter(ZonedDateTime notAfter) {
            getMock().setNotAfter(notAfter);
        }

        @Override
        public void setExpireAt(ZonedDateTime expireAt) {
            getMock().setExpireAt(expireAt);
        }

        @Override
        public String getAlias() {
            return getMock().getAlias();
        }

        @Override
        public ZonedDateTime getCreatedAt() {
            return getMock().getCreatedAt();
        }

        @Override
        public ZonedDateTime getNotBefore() {
            return getMock().getNotBefore();
        }

        @Override
        public ZonedDateTime getNotAfter() {
            return getMock().getNotAfter();
        }

        @Override
        public ZonedDateTime getExpireAt() {
            return getMock().getExpireAt();
        }

        @Override
        public Long getValidityInterval() {
            return getMock().getValidityInterval();
        }

        @Override
        public Long getLegacyInterval() {
            return getMock().getLegacyInterval();
        }

        @Override
        public KeyState getState() {
            return getMock().getState();
        }

        @Override
        public KeyUsage getKeyUsage() {
            return getMock().getKeyUsage();
        }

        private StsKeyEntry getMock() {
            return MOCKS_BY_ID.get(id);
        }
    }
}