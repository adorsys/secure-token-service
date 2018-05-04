package de.adorsys.sts.persistence;

import de.adorsys.sts.keymanagement.model.StsKeyEntry;
import de.adorsys.sts.keymanagement.model.StsKeyStore;
import de.adorsys.sts.keymanagement.persistence.KeyStoreRepository;
import de.adorsys.sts.keymanagement.service.KeyManagementProperties;
import org.adorsys.docusafe.business.DocumentSafeService;
import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.adorsys.docusafe.business.types.complex.DSDocumentMetaInfo;
import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.adorsys.docusafe.service.types.DocumentContent;
import org.adorsys.encobject.domain.UserMetaData;
import org.adorsys.jkeygen.keystore.KeyEntry;
import org.adorsys.jkeygen.keystore.KeyStoreService;
import org.adorsys.jkeygen.keystore.KeyStoreType;
import org.adorsys.jkeygen.pwd.PasswordCallbackHandler;

import javax.security.auth.callback.CallbackHandler;
import java.security.KeyStore;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FsKeyStoreRepository implements KeyStoreRepository {
	private final static String KEYSTORE_TYPE_KEY = "INTERNAL_SERVER_KEYSTORE_PERSISTENCE_TYPE_KEY";
	private final static String KEYSTORE_LAST_UPDATE_KEY = "INTERNAL_SERVER_KEYSTORE_PERSISTENCE_LAST_UPDATE_KEY";
	private static final ZonedDateTime DEFAULT_LAST_UPDATE = ZonedDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC);

	private final DocumentFQN keystoreFileFQN;
	private final DocumentSafeService documentSafeService;
	// The identity of this server instance.
	private final UserIDAuth userIDAuth;
	private final String keystoreName;
	private final CallbackHandler keyPassHandler;
	private final KeyEntryMapper keyEntryMapper;

	public FsKeyStoreRepository(UserIDAuth userIDAuth, DocumentSafeService documentSafeService,
			KeyManagementProperties keyManagementProperties, KeyEntryMapper keyEntryMapper) {
		this.userIDAuth = userIDAuth;
		this.documentSafeService = documentSafeService;
		this.keystoreName = keyManagementProperties.getKeystore().getName();
		this.keystoreFileFQN = new DocumentFQN(this.keystoreName);
		this.keyEntryMapper = keyEntryMapper;
		this.keyPassHandler = new PasswordCallbackHandler(
				keyManagementProperties.getKeystore().getPassword().toCharArray());
	}

	@Override
	public StsKeyStore load() {

		if (!documentSafeService.documentExists(userIDAuth, keystoreFileFQN))
			return null;

		DSDocument dsDocument = documentSafeService.readDocument(userIDAuth, keystoreFileFQN);
		KeyStore keyStore = initKeystore(dsDocument, keystoreFileFQN.getValue(), keyPassHandler);
		DSDocumentMetaInfo metaInfo = dsDocument.getDsDocumentMetaInfo();

		Map<String, String> attributesMap = new HashMap<>();
		Set<String> keySet = metaInfo.keySet();
		for (String key : keySet) {
			attributesMap.put(key, metaInfo.get(key));
		}
		attributesMap.remove(KEYSTORE_TYPE_KEY);

		Map<String, StsKeyEntry> loadedKeyEntries = loadKeyEntries(keyStore, attributesMap);

		return StsKeyStore.builder()
				.lastUpdate(readLastUpdateFromMetaInfo(metaInfo))
				.keyStore(keyStore)
				.keyEntries(loadedKeyEntries)
				.build();
	}

	private ZonedDateTime readLastUpdateFromMetaInfo(UserMetaData metaInfo) {
		String lastUpdateAsText = metaInfo.get(KEYSTORE_LAST_UPDATE_KEY);

		if(lastUpdateAsText == null) {
			return DEFAULT_LAST_UPDATE;
		}

		return ZonedDateTime.parse(lastUpdateAsText);
	}

	private void writeLastUpdateIntoMetaInfo(ZonedDateTime lastUpdate, UserMetaData metaInfo) {
		String lastUpdateAsText;

		if (lastUpdate == null) {
			lastUpdateAsText = DEFAULT_LAST_UPDATE.toString();
		} else {
			lastUpdateAsText = lastUpdate.toString();
		}

		metaInfo.put(KEYSTORE_LAST_UPDATE_KEY, lastUpdateAsText);
	}

	private Map<String, StsKeyEntry> loadKeyEntries(KeyStore keyStore, Map<String, String> attributesMap) {
		Map<String, StsKeyEntry> loadedKeyEntries = new HashMap<>();

		List<KeyEntry> keyEntries = KeyStoreService.loadEntries(keyStore,
				new KeyStoreService.SimplePasswordProvider(keyPassHandler));

		for (KeyEntry keyEntry : keyEntries) {
			String alias = keyEntry.getAlias();
			String attributes = attributesMap.get(alias);
			StsKeyEntry stsKeyEntry = keyEntryMapper.mapFromKeyEntryWithAttributes(keyEntry, attributes);

			loadedKeyEntries.put(alias, stsKeyEntry);
		}

		return loadedKeyEntries;
	}

	@Override
	public boolean exists() {
		return documentSafeService.documentExists(userIDAuth, keystoreFileFQN);
	}

	@Override
	public void save(StsKeyStore keyStore) {
		UserMetaData attributes = buildAttributes(keyStore);
		String storeType = keyStore.getKeyStore().getType();
		byte[] bs = KeyStoreService.toByteArray(keyStore.getKeyStore(), keystoreFileFQN.getValue(), keyPassHandler);
		DSDocumentMetaInfo dsDocumentMetaInfo = new DSDocumentMetaInfo(attributes);
		dsDocumentMetaInfo.put(KEYSTORE_TYPE_KEY, storeType);
		DSDocument dsDocument = new DSDocument(keystoreFileFQN, new DocumentContent(bs), dsDocumentMetaInfo);
		documentSafeService.storeDocument(userIDAuth, dsDocument);
	}

	@Override
	public ZonedDateTime lastUpdate() {
		if(documentSafeService.documentExists(userIDAuth, keystoreFileFQN)) {
			DSDocument dsDocument = documentSafeService.readDocument(userIDAuth, keystoreFileFQN);
			DSDocumentMetaInfo metaInfo = dsDocument.getDsDocumentMetaInfo();

			return readLastUpdateFromMetaInfo(metaInfo);
		} else {
			return DEFAULT_LAST_UPDATE;
		}
	}

	private UserMetaData buildAttributes(StsKeyStore keyStore) {
		UserMetaData attributes = new UserMetaData();
		for (Map.Entry<String, StsKeyEntry> entry : keyStore.getKeyEntries().entrySet()) {
			String alias = entry.getKey();
			StsKeyEntry keyEntry = entry.getValue();

			String valuesAsString = keyEntryMapper.extractEntryAttributesToString(keyEntry);

			attributes.put(alias, valuesAsString);
		}

		writeLastUpdateIntoMetaInfo(keyStore.getLastUpdate(), attributes);

		return attributes;
	}

	private KeyStore initKeystore(DSDocument dsDocument, String storeid, CallbackHandler handler) {
		KeyStoreType keyStoreType = new KeyStoreType(dsDocument.getDsDocumentMetaInfo().get(KEYSTORE_TYPE_KEY));
		return KeyStoreService.loadKeyStore(dsDocument.getDocumentContent().getValue(), storeid, keyStoreType, handler);
	}
}
