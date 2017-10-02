package de.adorsys.sts.resourceserver.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApiModel(description="Holds a list of resource servers", value="ResourceServers" )
@Builder
public class ResourceServers {
	
	public static final String AUDIENCE = "audience";
	public static final String ENDPOINT = "endpoint";
	public static final String CLIENT_ID = "clientId";

	@ApiModelProperty(value = "Holds the list of resource servers", required=true)
	@Builder.Default
	private List<ResourceServer> servers = new ArrayList<>();

	public List<ResourceServer> getServers() {
		return servers;
	}

	public Map<String, Map<String, ResourceServer>> toMultiMap(){
		Map<String, Map<String, ResourceServer>> result = new HashMap<>();

		result.put(ENDPOINT, new HashMap<>());
		result.put(AUDIENCE, new HashMap<>());
		result.put(CLIENT_ID, new HashMap<>());

		for (ResourceServer resourceServer : servers) {
			result.get(ENDPOINT).put(resourceServer.getEndpointUrl(), resourceServer);
			result.get(AUDIENCE).put(resourceServer.getAudience(), resourceServer);
			result.get(CLIENT_ID).put(resourceServer.getClientId(), resourceServer);
		}

		return result;
	}
}
