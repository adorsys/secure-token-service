package de.adorsys.sts.persistence;

import java.security.KeyStore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.security.auth.callback.CallbackHandler;

import org.adorsys.encobject.complextypes.BucketDirectory;
import org.adorsys.encobject.domain.ObjectHandle;
import org.adorsys.encobject.domain.Tuple;
import org.adorsys.encobject.domain.UserMetaData;
import org.adorsys.encobject.service.api.ExtendedStoreConnection;
import org.adorsys.encobject.service.api.KeystorePersistence;
import org.adorsys.encobject.service.impl.BlobStoreKeystorePersistenceImpl;
import org.adorsys.jkeygen.keystore.KeyEntry;
import org.adorsys.jkeygen.keystore.KeyStoreService;
import org.adorsys.jkeygen.pwd.PasswordCallbackHandler;

import de.adorsys.sts.keymanagement.model.StsKeyEntry;
import de.adorsys.sts.keymanagement.model.StsKeyStore;
import de.adorsys.sts.keymanagement.persistence.KeyStoreRepository;
import de.adorsys.sts.keymanagement.service.KeyManagementProperties;

public class FsPersistenceKeyStoreRepository implements KeyStoreRepository {

    private final ExtendedStoreConnection storageConnection;
    private KeystorePersistence keystorePersistence;
    private final String keystoreContainerName;
    private final String keystoreName;
    private final CallbackHandler keyPassHandler;
    private final KeyEntryMapper keyEntryMapper;

    public FsPersistenceKeyStoreRepository(
    		ExtendedStoreConnection storageConnection,
            KeyManagementProperties keyManagementProperties,
            KeyEntryMapper keyEntryMapper
    ) {
        this.storageConnection = storageConnection;
        this.keystoreContainerName = keyManagementProperties.getPersistence().getContainerName();
        this.keystoreName = keyManagementProperties.getKeystore().getName();
        this.keyEntryMapper = keyEntryMapper;

        String keyStorePassword = keyManagementProperties.getKeystore().getPassword();
        keyPassHandler = new PasswordCallbackHandler(keyStorePassword.toCharArray());
        
        keystorePersistence = new BlobStoreKeystorePersistenceImpl(storageConnection);
    }

    @PostConstruct
    public void postConstruct() {
    	BucketDirectory containerDir = new BucketDirectory(keystoreContainerName);
        if (!storageConnection.containerExists(containerDir)) {
        	storageConnection.createContainer(containerDir);
        }
    }

    @Override
    public StsKeyStore load() {
    	ObjectHandle handle = new ObjectHandle(keystoreContainerName, keystoreName);

    	Tuple<KeyStore, Map<String, String>> keyStoreAndAttributes = keystorePersistence.loadKeystoreAndAttributes(handle, keyPassHandler);
        KeyStore keyStore = keyStoreAndAttributes.getX();
        Map<String, String> attributesMap = keyStoreAndAttributes.getY();

        Map<String, StsKeyEntry> loadedKeyEntries = loadKeyEntries(keyStore, attributesMap);

        return StsKeyStore.builder()
                .keyStore(keyStore)
                .keyEntries(loadedKeyEntries)
                .build();
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

        return keystorePersistence.hasKeystore(handle);
    }

    @Override
    public void save(StsKeyStore keyStore) {
        ObjectHandle handle = new ObjectHandle(keystoreContainerName, keystoreName);

        UserMetaData attributes = buildAttributes(keyStore);
        keystorePersistence.saveKeyStoreWithAttributes(keyStore.getKeyStore(), attributes, keyPassHandler, handle);
    }

    private UserMetaData buildAttributes(StsKeyStore keyStore) {
        UserMetaData attributes = new UserMetaData();
        for (Map.Entry<String, StsKeyEntry> entry : keyStore.getKeyEntries().entrySet()) {
            String alias = entry.getKey();
            StsKeyEntry keyEntry = entry.getValue();

            String valuesAsString = keyEntryMapper.extractEntryAttributesToString(keyEntry);

            attributes.put(alias, valuesAsString);
        }

        return attributes;
    }
}
