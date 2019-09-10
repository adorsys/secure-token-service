package de.adorsys.sts.token.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.sts.tokenauth.AuthServer;
import de.adorsys.sts.tokenauth.AuthServersProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ConfigurationPropertiesAuthServerProvider implements AuthServersProvider {

    private final AuthServerConfigurationProperties authServerConfigurationProperties;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    private Map<String, AuthServer> authServers;

    @Autowired
    public ConfigurationPropertiesAuthServerProvider(
            AuthServerConfigurationProperties authServerConfigurationProperties,
            ObjectMapper objectMapper,
            Clock clock
    ) {
        this.authServerConfigurationProperties = authServerConfigurationProperties;
        this.objectMapper = objectMapper;
        this.clock = clock;
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
                    .map(properties -> mapFromProperties(properties, clock))
                    .collect(Collectors.toMap(AuthServer::getIssUrl, Function.identity()));
        }

        return authServers;
    }

    private AuthServer mapFromProperties(AuthServerConfigurationProperties.AuthServerProperties properties, Clock clock) {
        return new LoggingAuthServer(
                properties.getName(),
                properties.getIssUrl(),
                properties.getJwksUrl(),
                properties.getRefreshIntervalSeconds(),
                objectMapper,
                clock
        );
    }
}
