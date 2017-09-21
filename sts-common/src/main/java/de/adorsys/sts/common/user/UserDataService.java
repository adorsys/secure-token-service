package de.adorsys.sts.common.user;

import org.adorsys.encobject.domain.ObjectHandle;
import org.adorsys.encobject.service.KeystoreNotFoundException;
import org.adorsys.encobject.userdata.ObjectPersistenceAdapter;
import org.adorsys.encobject.userdata.UserDataNamingPolicy;

public class UserDataService {

	private UserDataNamingPolicy namingPolicy;

	private ObjectPersistenceAdapter objectPersistenceAdapter;

	public UserDataService(UserDataNamingPolicy namingPolicy,
			ObjectPersistenceAdapter objectPersistenceAdapter) {
		this.namingPolicy = namingPolicy;
		this.objectPersistenceAdapter = objectPersistenceAdapter;
	}
	
	/**
	 * Add a new user to this system.
	 * 
	 * @param user
	 * @param password
	 * @throws KeystoreNotFoundException 
	 */
	public void addAccount() throws KeystoreNotFoundException {
		if(objectPersistenceAdapter.hasStore()){
			throw new KeystoreNotFoundException("User " + objectPersistenceAdapter.getKeyCredentials().getKeyid() + " already have an account.");
		}
		objectPersistenceAdapter.initStore();
		UserCredentials userCredentials = new UserCredentials();
		userCredentials.setUsername(objectPersistenceAdapter.getKeyCredentials().getHandle().getContainer());
		storeUserCredentials(userCredentials);
	}

	/**
	 * Checks is user has an account with this system
	 * 
	 * @return
	 */
	public boolean hasAccount() {
		return objectPersistenceAdapter.hasStore();
	}
	
	public UserCredentials loadUserCredentials() {
		ObjectHandle handleForUserMainRecord = namingPolicy.handleForUserMainRecord(objectPersistenceAdapter.getKeyCredentials());
		return objectPersistenceAdapter.load(handleForUserMainRecord, UserCredentials.class);
	}

	public void storeUserCredentials(UserCredentials userCredentials) {
		ObjectHandle handleForUserMainRecord = namingPolicy.handleForUserMainRecord(objectPersistenceAdapter.getKeyCredentials());
		objectPersistenceAdapter.store(handleForUserMainRecord, userCredentials);
	}
}
