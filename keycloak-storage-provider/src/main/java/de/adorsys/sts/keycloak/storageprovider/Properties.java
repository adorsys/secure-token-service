package de.adorsys.sts.keycloak.storageprovider;

import de.adorsys.sts.keycloak.Constants;
import org.keycloak.provider.ProviderConfigProperty;

public class Properties {

    private Properties() {
        throw new RuntimeException();
    }

    public static ProviderConfigProperty stsAuthEndpointProperty = new ProviderConfigProperty();

    static {
        stsAuthEndpointProperty.setName(Constants.STS_LOGIN_URL);
        stsAuthEndpointProperty.setLabel("STS login url");
        stsAuthEndpointProperty.setType(ProviderConfigProperty.STRING_TYPE);
        stsAuthEndpointProperty.setHelpText("STS auth endpoint url");
    }
}
