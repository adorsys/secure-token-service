package de.adorsys.sts.token.authentication;

import de.adorsys.sts.tokenauth.AuthServer;
import de.adorsys.sts.tokenauth.AuthServersProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ConfigurationPropertiesAuthServerProvider implements AuthServersProvider {

    private final AuthServerConfigurationProperties authServerConfigurationProperties;

    private Map<String, AuthServer> authServers;

    @Autowired
    public ConfigurationPropertiesAuthServerProvider(AuthServerConfigurationProperties authServerConfigurationProperties) {
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
        if(authServers == null) {
            List<AuthServerConfigurationProperties.AuthServerProperties> authServersProperties = authServerConfigurationProperties.getAuthservers();
            authServers = authServersProperties.stream()
                    .map(this::mapFromProperties)
                    .collect(Collectors.toMap(AuthServer::getIssUrl, Function.identity()));
        }

        return authServers;
    }

    private AuthServer mapFromProperties(AuthServerConfigurationProperties.AuthServerProperties properties) {
        return new AuthServer(
                properties.getName(),
                properties.getIssUrl(),
                properties.getJwksUrl(),
                properties.getRefreshIntervalSeconds()
        );
    }
}
