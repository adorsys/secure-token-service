package de.adorsys.sts.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.adorsys.cryptoutils.exceptions.BaseException;
import org.adorsys.docusafe.business.types.UserID;
import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.adorsys.docusafe.cached.transactional.CachedTransactionalDocumentSafeService;
import org.adorsys.docusafe.service.types.DocumentContent;

import java.io.IOException;
import java.util.Optional;

/**
 * Base class for providing access to object thru cache.
 * 
 * Provides caching functionality when enabled.
 * 
 * @author fpo 2018-04-06 04:36
 *
 */
public abstract class FsBasedService {

	private CachedTransactionalDocumentSafeService cachedTransactionalDocumentSafeService;
	private ObjectMapper objectMapper;
	

	public FsBasedService(CachedTransactionalDocumentSafeService cachedTransactionalDocumentSafeService, ObjectMapper objectMapper) {
		super();
		this.cachedTransactionalDocumentSafeService = cachedTransactionalDocumentSafeService;
		this.objectMapper = objectMapper;
		if(this.objectMapper==null) this.objectMapper=new ObjectMapper();
	}
	
	protected void createUser(UserIDAuth userIDAuth){
		cachedTransactionalDocumentSafeService.createUser(userIDAuth);
	}

	public boolean userExists(UserID userID) {
		return cachedTransactionalDocumentSafeService.userExists(userID);
	}
	
	/**
	 * Load file from location documentFQN and parse using valueType. Check and
	 * return from cache is available. Caches result it not yet done.
	 *
	 * @param documentFQN
	 * @param valueType
	 * @return
	 */
	protected <T> Optional<T> load(UserIDAuth userIDAuth, DocumentFQN documentFQN, TypeReference<T> valueType) {
		// Return empty if base document does not exist.
		if (!cachedTransactionalDocumentSafeService.nonTxDocumentExists(userIDAuth,documentFQN)) return Optional.empty();

		try {
			Optional<T> ot = Optional.of(objectMapper.readValue(cachedTransactionalDocumentSafeService.nonTxReadDocument(userIDAuth, documentFQN).getDocumentContent().getValue(), valueType));
			return ot;
		} catch (IOException e) {
			throw new BaseException(e);
		}
	}

	protected <T> void storeDocument(UserIDAuth userIDAuth, DocumentFQN documentFQN, T entity) {
		try {
			storeDocument(userIDAuth, documentFQN, objectMapper.writeValueAsBytes(entity));
		} catch (JsonProcessingException e) {
			throw new BaseException(e);
		}
	}

	protected void storeDocument(UserIDAuth userIDAuth, DocumentFQN documentFQN, byte[] data) {
		DocumentContent documentContent = new DocumentContent(data);
		DSDocument dsDocument = new DSDocument(documentFQN, documentContent, null);
		cachedTransactionalDocumentSafeService.nonTxStoreDocument(userIDAuth, dsDocument);
	}

	public boolean documentExists(UserIDAuth userIDAuth, DocumentFQN documentFQN) {
		return cachedTransactionalDocumentSafeService.nonTxDocumentExists(userIDAuth, documentFQN);
	}

}
