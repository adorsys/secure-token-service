package de.adorsys.sts.resourceserver.provider;

import de.adorsys.sts.common.props.STSPropertiesConstants;
import de.adorsys.sts.resourceserver.model.ResourceServers;
import de.adorsys.sts.resourceserver.model.ResourceServer;
import org.adorsys.envutils.EnvProperties;
import org.apache.commons.lang3.StringUtils;


public class EnvironmentVariableResourceServersProvider implements ResourceServersProvider {

    @Override
    public ResourceServers get() {
        ResourceServers resourceServers = new ResourceServers();

        String res_servers_prop = EnvProperties.getEnvOrSysProp(STSPropertiesConstants.RESOURCE_SERVER_NAMES, "");
        String[] res_servers = StringUtils.split(res_servers_prop,",");

        for (String res_server_name : res_servers) {
            ResourceServer resourceServer = mapToResourceServer(res_server_name);
            resourceServers.getServers().add(resourceServer);
        }

        return resourceServers;
    }

    private ResourceServer mapToResourceServer(String res_server_name) {
        String res_server_endpoint_url = EnvProperties.getEnvOrSysProp(res_server_name + STSPropertiesConstants.RESOURCE_SERVER_ENDPOINT_URL_SUFFIX, true);
        String res_server_jwks_url = EnvProperties.getEnvOrSysProp(res_server_name + STSPropertiesConstants.RESOURCE_SERVER_JWKS_URL_SUFFIX, true);
        String res_server_client_id = EnvProperties.getEnvOrSysProp(res_server_name + STSPropertiesConstants.RESOURCE_SERVER_CLIENT_ID_SUFFIX, true);
        String res_server_user_secret_claim = EnvProperties.getEnvOrSysProp(res_server_name + STSPropertiesConstants.RESOURCE_SERVER_USER_SECRET_CLAIM_SUFFIX, true);
        String res_server_audience = EnvProperties.getEnvOrSysProp(res_server_name + STSPropertiesConstants.RESOURCE_SERVER_AUDIENCE_NAME_SUFFIX, false);

        ResourceServer resourceServer = new ResourceServer();
        resourceServer.setAudience(res_server_audience);
        if(StringUtils.isNotBlank(res_server_endpoint_url))resourceServer.setEndpointUrl(res_server_endpoint_url);
        if(StringUtils.isNotBlank(res_server_jwks_url))resourceServer.setJwksUrl(res_server_jwks_url);
        if(StringUtils.isNotBlank(res_server_client_id))resourceServer.setClientId(res_server_client_id);
        if(StringUtils.isNotBlank(res_server_user_secret_claim))resourceServer.setUserSecretClaimName(res_server_user_secret_claim);

        resourceServer.setIdpServer(false);
        return resourceServer;
    }
}
