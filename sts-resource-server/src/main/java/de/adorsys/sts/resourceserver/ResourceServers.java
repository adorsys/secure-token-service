package de.adorsys.sts.resourceserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.adorsys.sts.resourceserver.model.ResourceServer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description="Holds a list of resource servers", value="ResourceServers" )
public class ResourceServers {
	
	public static final String AUNDIENCE = "aundience";
	public static final String ENDPOINT = "endpoint";
	@ApiModelProperty(value = "Holds the list of resource servers", required=true)
	private List<ResourceServer> servers = new ArrayList<>();

	public List<ResourceServer> getServers() {
		return servers;
	}

	public void setServers(List<ResourceServer> servers) {
		this.servers = servers;
	}
	
	Map<String, Map<String, ResourceServer>> toMultiMap(){
		Map<String, Map<String, ResourceServer>> result = new HashMap<>();
		result.put(ENDPOINT, new HashMap<>());
		result.put(AUNDIENCE, new HashMap<>());
		for (ResourceServer resourceServer : servers) {
			result.get(ENDPOINT).put(resourceServer.getEndpointUrl(), resourceServer);
			result.get(AUNDIENCE).put(resourceServer.getAudience(), resourceServer);
		}
		return result;
	}
}
