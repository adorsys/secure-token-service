package de.adorsys.sts.persistence;

import org.adorsys.docusafe.business.DocumentSafeService;
import org.adorsys.docusafe.business.types.UserID;
import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.adorsys.encobject.domain.ReadKeyPassword;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.adorsys.sts.resourceserver.model.UserCredentials;
import de.adorsys.sts.resourceserver.service.UserDataRepository;

public class FsUserDataRepository extends FsBasedService implements UserDataRepository {

	private static final String RESOURCE_SERVERS_FILE_NAME = "user_data";
	private static final TypeReference<UserCredentials> VALUE_TYPE = new TypeReference<UserCredentials>() {}; 
	private final DocumentFQN dataFileFQN = new DocumentFQN(RESOURCE_SERVERS_FILE_NAME);

	public FsUserDataRepository(DocumentSafeService documentSafeService, ObjectMapper objectMapper) {
		super(documentSafeService, objectMapper);
	}

	@Override
	public void addAccount(String userId, String readKeyPassword) {
		if(hasAccount(userId)) return;
		super.createUser(userIDAuth(userId, readKeyPassword));
	}

	@Override
	public boolean hasAccount(String userId) {
		return super.userExists(new UserID(userId));
	}

	@Override
	public UserCredentials loadUserCredentials(String userId, String readKeyPassword) {
		UserIDAuth userIDAuth = userIDAuth(userId, readKeyPassword);
		return super.load(userIDAuth, dataFileFQN, VALUE_TYPE).orElse(null);
	}
	
	@Override
	public void storeUserCredentials(String userId, String readKeyPassword, UserCredentials userCredentials) {
		UserIDAuth userIDAuth = userIDAuth(userId, readKeyPassword);
		super.storeDocument(userIDAuth, dataFileFQN, userCredentials);
	}

	private UserIDAuth userIDAuth(String user, String readKeyPassword) {
		return new UserIDAuth(new UserID(user), new ReadKeyPassword(readKeyPassword));
	}

}
