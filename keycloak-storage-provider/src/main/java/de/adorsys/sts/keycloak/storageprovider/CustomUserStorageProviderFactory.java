package de.adorsys.sts.keycloak.storageprovider;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.storage.UserStorageProviderFactory;

import java.util.Arrays;
import java.util.List;

public class CustomUserStorageProviderFactory implements UserStorageProviderFactory<CustomUserStorageProvider> {

    @Override
    public CustomUserStorageProvider create(KeycloakSession keycloakSession, ComponentModel componentModel) {
        return new CustomUserStorageProvider(keycloakSession, componentModel);
    }

    @Override
    public String getId() {
        return "custom-user-storage-provider";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return Arrays.asList(
                Properties.stsAuthEndpointProperty
        );
    }
}
