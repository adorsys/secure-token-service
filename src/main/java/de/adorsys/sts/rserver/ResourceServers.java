package de.adorsys.sts.rserver;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description="Holds a list of resource servers", value="ResourceServers" )
public class ResourceServers {
	
	@ApiModelProperty(value = "Holds the list of resource servers", required=true)
	private List<ResourceServer> servers = new ArrayList<>();

	public List<ResourceServer> getServers() {
		return servers;
	}

	public void setServers(List<ResourceServer> servers) {
		this.servers = servers;
	}
}
