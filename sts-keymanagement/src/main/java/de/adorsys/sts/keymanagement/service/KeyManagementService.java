package de.adorsys.sts.keymanagement.service;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import de.adorsys.sts.keymanagement.model.KeyUsage;
import de.adorsys.sts.keymanagement.model.StsKeyEntry;
import de.adorsys.sts.keymanagement.model.StsKeyStore;
import de.adorsys.sts.keymanagement.persistence.KeyStoreRepository;
import org.adorsys.jjwk.serverkey.KeyAndJwk;
import org.adorsys.jjwk.serverkey.ServerKeyMap;
import org.adorsys.jjwk.serverkey.ServerKeyMapProvider;
import org.adorsys.jjwk.serverkey.ServerKeysHolder;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class KeyManagementService implements ServerKeyMapProvider {

    private final KeyStoreRepository repository;
    private final KeyStoreGenerator keyStoreGenerator;
    private final KeyConversionService keyConversionService;

    private StsKeyStore keyStore;

    public KeyManagementService(
            KeyStoreRepository repository,
            KeyStoreGenerator keyStoreGenerator,
            KeyConversionService keyConversionService
    ) {
        this.repository = repository;
        this.keyStoreGenerator = keyStoreGenerator;
        this.keyConversionService = keyConversionService;
    }

    @PostConstruct
    public void postConstruct() {
        if (repository.exists()) {
            keyStore = repository.load();
        } else {
            keyStore = keyStoreGenerator.generate();
            repository.save(keyStore);
        }
    }

    @Override
    public ServerKeyMap getKeyMap() {
        throw new IllegalStateException("Method not supported");
    }

    @Override
    public ServerKeysHolder getServerKeysHolder() {
        throw new IllegalStateException("Method not supported");
    }

    @Override
    public KeyAndJwk randomSecretKey() {
        return getSecretKeys().randomSecretKey();
    }

    @Override
    public KeyAndJwk randomSignKey() {
        return getPrivateKeys().randomSignKey();
    }

    @Override
    public Key getKey(String keyId) {
        ServerKeysHolder exportedKeys = keyConversionService.export(keyStore.getKeyStore());
        ServerKeyMap serverKeyMap = new ServerKeyMap(exportedKeys.getPrivateKeySet());

        return serverKeyMap.getKey(keyId);
    }

    @Override
    public JWKSet getPublicKeys() {
        ServerKeysHolder exportedKeys = keyConversionService.export(keyStore.getKeyStore());
        Map<String, StsKeyEntry> keyEntries = keyStore.getKeyEntries();

        List<String> filteredKeyAliases = keyEntries.values().stream()
                .filter(this::hasUsablePublicKey)
                .map(StsKeyEntry::getAlias)
                .collect(Collectors.toList());

        List<JWK> filteredKeys = exportedKeys.getPublicKeySet().getKeys()
                .stream()
                .filter(k -> filteredKeyAliases.contains(k.getKeyID()))
                .collect(Collectors.toList());

        return new JWKSet(filteredKeys);
    }

    private ServerKeyMap getPrivateKeys() {
        return new ServerKeyMap(getFilteredPrivateKeys(this::hasUsablePrivateKey));
    }

    private ServerKeyMap getSecretKeys() {
        return new ServerKeyMap(getFilteredPrivateKeys(this::isUsableSecretKey));
    }

    private JWKSet getFilteredPrivateKeys(Predicate<StsKeyEntry> predicate) {
        ServerKeysHolder exportedKeys = keyConversionService.export(keyStore.getKeyStore());
        Map<String, StsKeyEntry> keyEntries = keyStore.getKeyEntries();

        List<String> filteredKeyAliases = keyEntries.values().stream()
                .filter(predicate)
                .map(StsKeyEntry::getAlias)
                .collect(Collectors.toList());

        List<JWK> filteredKeys = exportedKeys.getPrivateKeySet().getKeys()
                .stream()
                .filter(k -> filteredKeyAliases.contains(k.getKeyID()))
                .collect(Collectors.toList());

        return new JWKSet(filteredKeys);
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
