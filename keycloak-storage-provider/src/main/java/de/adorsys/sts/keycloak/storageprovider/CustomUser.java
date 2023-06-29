package de.adorsys.sts.keycloak.storageprovider;

import de.adorsys.sts.keycloak.util.ImmutableList;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

import java.util.List;
import java.util.Map;

public class CustomUser extends AbstractUserAdapter {
    private static final List<String> EMPTY = new ImmutableList<>();

    private final String username;

    private List<String> roles;

    private Map<String, String> secrets;

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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Map<String, String> getSecrets() {
        return secrets;
    }

    public void setSecrets(Map<String, String> secrets) {
        this.secrets = secrets;
    }

    @Override
    public List<String> getAttribute(String name) {
        return EMPTY;
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
