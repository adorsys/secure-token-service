package de.adorsys.sts.resourceserver.service;

import de.adorsys.sts.resourceserver.model.UserCredentials;

public interface UserDataRepository {

	/**
	 * Add a new user to this system.
	 * 
	 * @param user
	 * @param password
	 * @throws KeystoreNotFoundException 
	 */
	public void addAccount(String user, String password);

	/**
	 * Checks is user has an account with this system
	 * 
	 * @return
	 */
	public boolean hasAccount(String user);
	
	public UserCredentials loadUserCredentials(String user, String password);

	public void storeUserCredentials(String user, String password, UserCredentials userCredentials);
}
