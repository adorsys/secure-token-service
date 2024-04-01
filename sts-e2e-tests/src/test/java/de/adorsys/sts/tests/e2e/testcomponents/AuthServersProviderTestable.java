package de.adorsys.sts.tests.e2e.testcomponents;

import de.adorsys.sts.token.authentication.AuthServerConfigurationProperties;
import de.adorsys.sts.token.authentication.ConfigurationPropertiesAuthServerProvider;
import de.adorsys.sts.tokenauth.AuthServer;
import de.adorsys.sts.tokenauth.AuthServersProvider;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AuthServersProviderTestable extends ConfigurationPropertiesAuthServerProvider implements AuthServersProvider {

    private Map<String, AuthServer> authServers;
    private final AuthServerConfigurationProperties authServerConfigurationProperties;

    public AuthServersProviderTestable(AuthServerConfigurationProperties authServerConfigurationProperties) {
        super(authServerConfigurationProperties);
        this.authServerConfigurationProperties = authServerConfigurationProperties;
    }

    @Override
    public Map<String, AuthServer> getAll() {
        return getOrReadAuthServers();
    }

    @Override
    public AuthServer get(String issuer) {
        return getOrReadAuthServers().get(issuer);
    }

    private Map<String, AuthServer> getOrReadAuthServers() {
        if (authServers == null) {
            List<AuthServerConfigurationProperties.AuthServerProperties> authServersProperties = authServerConfigurationProperties.getAuthservers();
            authServers = authServersProperties.stream()
                    .map(this::mapFromProperties)
                    .collect(Collectors.toMap(AuthServer::getIssUrl, Function.identity()));
        }

        return authServers;
    }

    private AuthServer mapFromProperties(AuthServerConfigurationProperties.AuthServerProperties properties) {
        return new AuthServerTestable(
                properties.getName(),
                properties.getIssUrl(),
                properties.getJwksUrl(),
                properties.getRefreshIntervalSeconds()
        );
    }
}
