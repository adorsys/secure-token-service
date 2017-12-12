package de.adorsys.sts.tokenauth;

import org.adorsys.envutils.EnvProperties;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentVariablesAuthServersProvider implements AuthServersProvider {

    private Map<String, AuthServer> authServers;

    @Override
    public Map<String, AuthServer> getAll() {
        if(authServers == null) {
            authServers = read();
        }

        return authServers;
    }

    @Override
    public AuthServer get(String issuer) {
        return authServers.get(issuer);
    }

    private Map<String, AuthServer> read() {
        Map<String, AuthServer> authServers = new HashMap<>();

        String auth_servers_prop = EnvProperties.getEnvOrSysProp("AUTH_SERVER_NAMES", true);
        String[] auth_servers = StringUtils.split(auth_servers_prop);
        if(auth_servers!=null){
            for (String auth_server : auth_servers) {
                String auth_server_iss_url = EnvProperties.getEnvOrSysProp(auth_server + "_AUTH_SERVER_ISS_URL", false);
                String auth_server_jwks_url = EnvProperties.getEnvOrSysProp(auth_server + "_AUTH_SERVER_JWKS_URL", false);
                String auth_server_jwks_refresh_int = EnvProperties.getEnvOrSysProp(auth_server + "_AUTH_SERVER_JWKS_URL", "600");

                AuthServer authServer = new AuthServer(auth_server, auth_server_iss_url, auth_server_jwks_url);
                if(StringUtils.isNumeric(auth_server_jwks_refresh_int)){
                    int refreshIntervalSeconds = Integer.parseInt(auth_server_jwks_refresh_int);
                    authServer.setRefreshIntervalSeconds(refreshIntervalSeconds);
                }
                authServers.put(authServer.getIssUrl(), authServer);
            }
        }

        return authServers;
    }
}
