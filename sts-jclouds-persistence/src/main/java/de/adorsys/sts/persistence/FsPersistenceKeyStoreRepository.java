package de.adorsys.sts.persistence;

import de.adorsys.sts.keymanagement.model.StsKeyEntry;
import de.adorsys.sts.keymanagement.model.StsKeyStore;
import de.adorsys.sts.keymanagement.persistence.KeyStoreRepository;
import de.adorsys.sts.keymanagement.service.KeyManagementProperties;
import org.adorsys.encobject.domain.ObjectHandle;
import org.adorsys.encobject.domain.Tuple;
import org.adorsys.encobject.filesystem.FsPersistenceFactory;
import org.adorsys.encobject.service.*;
import org.adorsys.jkeygen.keystore.KeyEntry;
import org.adorsys.jkeygen.keystore.KeyStoreService;
import org.adorsys.jkeygen.pwd.PasswordCallbackHandler;

import javax.annotation.PostConstruct;
import javax.security.auth.callback.CallbackHandler;
import java.io.IOException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FsPersistenceKeyStoreRepository implements KeyStoreRepository {

    private final FsPersistenceFactory persFactory;
    private final String keystoreContainerName;
    private final String keystoreName;
    private final CallbackHandler keyPassHandler;
    private final KeyEntryMapper keyEntryMapper;

    public FsPersistenceKeyStoreRepository(
            FsPersistenceFactory persFactory,
            String keystoreContainerName,
            String keystoreName,
            String keyStorePassword,
            KeyEntryMapper keyEntryMapper
    ) {
        this.persFactory = persFactory;
        this.keystoreContainerName = keystoreContainerName;
        this.keystoreName = keystoreName;

        keyPassHandler = new PasswordCallbackHandler(keyStorePassword.toCharArray());
        this.keyEntryMapper = keyEntryMapper;
    }

    public FsPersistenceKeyStoreRepository(
            FsPersistenceFactory persFactory,
            KeyManagementProperties keyManagementProperties,
            KeyEntryMapper keyEntryMapper
    ) {
        this.persFactory = persFactory;
        this.keystoreContainerName = keyManagementProperties.getPersistence().getContainerName();
        this.keystoreName = keyManagementProperties.getKeystore().getName();
        this.keyEntryMapper = keyEntryMapper;

        String keyStorePassword = keyManagementProperties.getPersistence().getPassword();
        keyPassHandler = new PasswordCallbackHandler(keyStorePassword.toCharArray());
    }

    @PostConstruct
    public void postConstruct() {
        ContainerPersistence containerPersistence = persFactory.getContainerPersistence();
        if (!containerPersistence.containerExists(keystoreContainerName)) {
            try {
                containerPersistence.creteContainer(keystoreContainerName);
            } catch (ContainerExistsException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Override
    public StsKeyStore load() {
        ObjectHandle handle = new ObjectHandle(keystoreContainerName, keystoreName);

        try {
            Tuple<KeyStore, Map<String, String>> keyStoreAndAttributes = persFactory.getKeystorePersistence().loadKeystoreAndAttributes(handle, keyPassHandler);
            KeyStore keyStore = keyStoreAndAttributes.getX();
            Map<String, String> attributesMap = keyStoreAndAttributes.getY();

            Map<String, StsKeyEntry> loadedKeyEntries = loadKeyEntries(keyStore, attributesMap);

            return StsKeyStore.builder()
                    .keyStore(keyStore)
                    .keyEntries(loadedKeyEntries)
                    .build();
        } catch (KeystoreNotFoundException | CertificateException | WrongKeystoreCredentialException | MissingKeystoreAlgorithmException | MissingKeystoreProviderException | MissingKeyAlgorithmException | IOException | UnknownContainerException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, StsKeyEntry> loadKeyEntries(KeyStore keyStore, Map<String, String> attributesMap) {
        Map<String, StsKeyEntry> loadedKeyEntries = new HashMap<>();

        List<KeyEntry> keyEntries = KeyStoreService.loadEntries(keyStore, new KeyStoreService.SimplePasswordProvider(keyPassHandler));

        for(KeyEntry keyEntry : keyEntries) {
            String alias = keyEntry.getAlias();
            String attributes = attributesMap.get(alias);
            StsKeyEntry stsKeyEntry = keyEntryMapper.mapFromKeyEntryWithAttributes(keyEntry, attributes);

            loadedKeyEntries.put(alias, stsKeyEntry);
        }

        return loadedKeyEntries;
    }

    @Override
    public boolean exists() {
        ObjectHandle handle = new ObjectHandle(keystoreContainerName, keystoreName);

        return persFactory.getKeystorePersistence().hasKeystore(handle);
    }

    @Override
    public void save(StsKeyStore keyStore) {
        ObjectHandle handle = new ObjectHandle(keystoreContainerName, keystoreName);

        try {
            Map<String, String> attributes = buildAttributes(keyStore);
            persFactory.getKeystorePersistence().saveKeyStoreWithAttributes(keyStore.getKeyStore(), attributes, keyPassHandler, handle);
        } catch (NoSuchAlgorithmException | CertificateException | UnknownContainerException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> buildAttributes(StsKeyStore keyStore) {
        Map<String, String> attributes = new HashMap<>();

        for (Map.Entry<String, StsKeyEntry> entry : keyStore.getKeyEntries().entrySet()) {
            String alias = entry.getKey();
            StsKeyEntry keyEntry = entry.getValue();

            String valuesAsString = keyEntryMapper.extractEntryAttributesToString(keyEntry);

            attributes.put(alias, valuesAsString);
        }

        return attributes;
    }
}
