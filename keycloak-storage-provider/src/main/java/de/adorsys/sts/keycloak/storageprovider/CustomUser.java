package de.adorsys.sts.keycloak.storageprovider;

import de.adorsys.sts.keycloak.util.ImmutableList;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.UserCredentialManager;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.SubjectCredentialManager;
import org.keycloak.storage.adapter.AbstractUserAdapter;

import java.util.List;
import java.util.stream.Stream;

public class CustomUser extends AbstractUserAdapter {
    private static final List<String> EMPTY = new ImmutableList<>();

    private final String username;

    private CustomUser(
            KeycloakSession session,
            RealmModel realm,
            ComponentModel storageProviderModel,
            String username
    ) {
        super(session, realm, storageProviderModel);
        this.username = username;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public SubjectCredentialManager credentialManager() {
        return new UserCredentialManager(session, realm, this);
    }

    @Override
    public Stream<String> getAttributeStream(String name) {
        return EMPTY.stream();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private KeycloakSession session;
        private RealmModel realm;
        private ComponentModel storageProviderModel;
        private String username;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder session(KeycloakSession session) {
            this.session = session;
            return this;
        }

        public Builder realm(RealmModel realm) {
            this.realm = realm;
            return this;
        }

        public Builder storageProviderModel(ComponentModel storageProviderModel) {
            this.storageProviderModel = storageProviderModel;
            return this;
        }

        public CustomUser build() {
            return new CustomUser(session, realm, storageProviderModel, username);
        }
    }
}
