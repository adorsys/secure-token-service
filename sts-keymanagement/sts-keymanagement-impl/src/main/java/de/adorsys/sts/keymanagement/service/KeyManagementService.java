package de.adorsys.sts.keymanagement.service;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import de.adorsys.sts.common.model.KeyAndJwk;
import de.adorsys.sts.common.util.ImmutableLists;
import de.adorsys.sts.keymanagement.model.KeyUsage;
import de.adorsys.sts.keymanagement.model.ServerKeysHolder;
import de.adorsys.sts.keymanagement.model.StsKeyEntry;
import de.adorsys.sts.keymanagement.model.StsKeyStore;
import de.adorsys.sts.keymanagement.persistence.KeyStoreRepository;
import de.adorsys.sts.keymanagement.util.StsServerKeyMap;

import java.security.Key;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class KeyManagementService implements ServerKeyMapProvider {

    private static final JWKSet EMPTY_JWK_SET = new JWKSet(ImmutableLists.emptyList());
    private static final ServerKeysHolder EMPTY_KEYS = new ServerKeysHolder(EMPTY_JWK_SET, EMPTY_JWK_SET);

    private final KeyStoreRepository repository;
    private final KeyConversionService keyConversionService;

    public KeyManagementService(
            KeyStoreRepository repository,
            KeyConversionService keyConversionService
    ) {
        this.repository = repository;
        this.keyConversionService = keyConversionService;
    }

    @Override
    public KeyAndJwk randomSecretKey() {
        //FIXME-cleanup
        //return getSecretKeys().randomSecretKey();
        return null;
    }

    @Override
    public KeyAndJwk randomSignKey() {
        //FIXME-cleanup
        //return getPrivateKeys().randomSignKey();
        return null;
    }

    @Override
    public Key getKey(String keyId) {
        //FIXME-cleanup
        /*StsServerKeyMap serverKeyMap = new StsServerKeyMap(loadKeys().getPrivateKeySet());

        return serverKeyMap.getKey(keyId);*/
        return null;
    }

    private ServerKeysHolder loadKeys() {
        ServerKeysHolder exportedKeys;

        if(repository.exists()) {
            exportedKeys = keyConversionService.export(repository.load());
        } else {
            exportedKeys = EMPTY_KEYS;
        }

        return exportedKeys;
    }

    @Override
    public JWKSet getPublicKeys() {
        if(repository.exists()) {
            StsKeyStore keyStore = repository.load();

            ServerKeysHolder exportedKeys = keyConversionService.export(keyStore);
            Map<String, StsKeyEntry> keyEntries = keyStore.getEntries();

            List<String> filteredKeyAliases = keyEntries.values().stream()
                    .filter(this::hasUsablePublicKey)
                    .map(StsKeyEntry::getAlias)
                    .collect(Collectors.toList());

            List<JWK> filteredKeys = exportedKeys.getPublicKeySet().getKeys()
                    .stream()
                    .filter(k -> filteredKeyAliases.contains(k.getKeyID()))
                    .collect(Collectors.toList());

            return new JWKSet(filteredKeys);
        } else {
            return EMPTY_JWK_SET;
        }
    }

    private StsServerKeyMap getPrivateKeys() {
        //FIXME-cleanup
        //return new StsServerKeyMap(getFilteredPrivateKeys(this::hasUsablePrivateKey));
        return null;
    }

    private StsServerKeyMap getSecretKeys() {
        //FIXME-cleanup
        //return new StsServerKeyMap(getFilteredPrivateKeys(this::isUsableSecretKey));
        return null;
    }

    private JWKSet getFilteredPrivateKeys(Predicate<StsKeyEntry> predicate) {
        if(repository.exists()) {
            StsKeyStore keyStore = repository.load();

            ServerKeysHolder exportedKeys = keyConversionService.export(keyStore);
            Map<String, StsKeyEntry> keyEntries = keyStore.getEntries();

            List<String> filteredKeyAliases = keyEntries.values().stream()
                    .filter(predicate)
                    .map(StsKeyEntry::getAlias)
                    .collect(Collectors.toList());

            List<JWK> filteredKeys = exportedKeys.getPrivateKeySet().getKeys()
                    .stream()
                    .filter(k -> filteredKeyAliases.contains(k.getKeyID()))
                    .collect(Collectors.toList());

            return new JWKSet(filteredKeys);
        } else {
            return EMPTY_JWK_SET;
        }
    }

    private boolean hasUsablePublicKey(StsKeyEntry stsKeyEntry) {
        return stsKeyEntry.getKeyUsage() == KeyUsage.Encryption && stsKeyEntry.getState() == StsKeyEntry.State.VALID
                || stsKeyEntry.getKeyUsage() == KeyUsage.Signature && (stsKeyEntry.getState() == StsKeyEntry.State.VALID || stsKeyEntry.getState() == StsKeyEntry.State.LEGACY);
    }

    private boolean hasUsablePrivateKey(StsKeyEntry stsKeyEntry) {
        return stsKeyEntry.getKeyUsage() == KeyUsage.Signature && stsKeyEntry.getState() == StsKeyEntry.State.VALID
                || stsKeyEntry.getKeyUsage() == KeyUsage.Encryption && (stsKeyEntry.getState() == StsKeyEntry.State.VALID ||stsKeyEntry.getState() == StsKeyEntry.State.LEGACY);
    }

    private boolean isUsableSecretKey(StsKeyEntry stsKeyEntry) {
        return stsKeyEntry.getKeyUsage() == KeyUsage.SecretKey && stsKeyEntry.getState() == StsKeyEntry.State.VALID;
    }
}
