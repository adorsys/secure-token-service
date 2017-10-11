package de.adorsys.sts.keymanagement.service;

import com.nimbusds.jose.jwk.JWKSet;
import de.adorsys.sts.keymanagement.persistence.KeyStoreRepository;
import org.adorsys.jjwk.serverkey.KeyConverter;
import org.adorsys.jjwk.serverkey.ServerKeysHolder;

import javax.annotation.PostConstruct;
import java.security.KeyStore;

public class KeyManagementService {

    private final KeyStoreRepository repository;
    private final KeyStoreGenerator keyStoreGenerator;

    private final String keyStorePassword;

    private KeyStore keyStore;

    public KeyManagementService(
            KeyStoreRepository repository,
            KeyStoreGenerator keyStoreGenerator,
            String keyStorePassword
    ) {
        this.repository = repository;
        this.keyStoreGenerator = keyStoreGenerator;
        this.keyStorePassword = keyStorePassword;
    }

    @PostConstruct
    public void postConstruct() {
        if(repository.exists()) {
            keyStore = repository.load();
        } else {
            keyStore = keyStoreGenerator.generate();
            repository.save(keyStore);
        }
    }

    public ServerKeysHolder get() {
        JWKSet privateKeys = KeyConverter.exportPrivateKeys(keyStore, keyStorePassword.toCharArray());
        JWKSet publicKeys = privateKeys.toPublicJWKSet();

        return new ServerKeysHolder(privateKeys, publicKeys);
    }

    public void addNewKey() {

    }
}
