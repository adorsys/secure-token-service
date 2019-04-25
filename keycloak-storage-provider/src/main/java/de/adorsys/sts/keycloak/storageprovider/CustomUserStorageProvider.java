package de.adorsys.sts.keycloak.storageprovider;

import de.adorsys.sts.keycloak.AuthenticatorUtil;
import de.adorsys.sts.keycloak.Constants;
import de.adorsys.sts.keycloak.rest.CustomRestClient;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomUserStorageProvider implements UserStorageProvider, UserLookupProvider, CredentialInputValidator {

    private static final Pattern PATTERN = Pattern.compile("f:[a-z0-9\\-]+:(.+)");
    private final KeycloakSession session;
    private final ComponentModel model;


    CustomUserStorageProvider(KeycloakSession session, ComponentModel model) {
        this.session = session;
        this.model = model;
    }

    @Override
    public boolean supportsCredentialType(String s) {
        return CredentialModel.PASSWORD.equals(s);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realmModel, UserModel userModel, String s) {
        return CredentialModel.PASSWORD.equals(s);
    }

    @Override
    public boolean isValid(RealmModel realmModel, UserModel userModel, CredentialInput credentialInput) {
        boolean isValid = false;

        String url = model.getConfig().getFirst(Constants.STS_LOGIN_URL);

        if (url == null) {
            throw new IllegalStateException("STS login endpoint address is not set but mandatory");
        }

        if (credentialInput instanceof UserCredentialModel) {
            UserCredentialModel credentialInputModel = (UserCredentialModel) credentialInput;
            String password = credentialInputModel.getValue();
            List<String> audiences = AuthenticatorUtil.extractAudiences(credentialInputModel);

            String secrets = CustomRestClient.loadUserSecrets(url, userModel.getUsername(), password, audiences);

            isValid = secrets != null;

            // Francis 2017.10.31. We can put all backend information in the credential input
            // object and delegate the work to a higher layer.
            credentialInputModel.setNote(Constants.CUSTOM_USER_SECRET_NOTE_KEY, secrets);
        }

        return isValid;
    }

    @Override
    public void close() {

    }

    @Override
    public UserModel getUserById(String s, RealmModel realmModel) {
        String username = extractUsernameFromId(s);
        return getUserByUsername(username, realmModel);
    }

    @Override
    public UserModel getUserByUsername(String s, final RealmModel realmModel) {
        return CustomUser.builder()
                .session(session)
                .storageProviderModel(model)
                .realm(realmModel)
                .username(s)
                .build();
    }

    @Override
    public UserModel getUserByEmail(String s, RealmModel realmModel) {
        return null;
    }

    private String extractUsernameFromId(String id) {
        Matcher matcher = PATTERN.matcher(id);

        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            return id;
        }
    }
}
