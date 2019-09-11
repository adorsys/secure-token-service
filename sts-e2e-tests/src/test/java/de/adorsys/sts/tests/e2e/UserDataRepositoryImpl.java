package de.adorsys.sts.tests.e2e;

import de.adorsys.sts.resourceserver.model.UserCredentials;
import de.adorsys.sts.resourceserver.service.UserDataRepository;

public class UserDataRepositoryImpl implements UserDataRepository {
    @Override
    public void addAccount(String user, String password) {

    }

    @Override
    public boolean hasAccount(String user) {
        return false;
    }

    @Override
    public UserCredentials loadUserCredentials(String user, String password) {
        return null;
    }

    @Override
    public void storeUserCredentials(String user, String password, UserCredentials userCredentials) {

    }
}
