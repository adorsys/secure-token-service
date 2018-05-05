package de.adorsys.sts.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.adorsys.cryptoutils.exceptions.BaseException;
import org.adorsys.docusafe.business.DocumentSafeService;
import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.adorsys.sts.resourceserver.model.ResourceServer;
import de.adorsys.sts.resourceserver.persistence.ResourceServerRepository;

/**
 * Stores the list of resource servers known to this application.
 * 
 * @author fpo 2018-04-18 11:39
 *
 */
public class FsResourceServerRepository extends FsBasedService implements ResourceServerRepository {

	private static final TypeReference<List<ResourceServer>> RESOURCE_SERVER_LIST_TYPE = new TypeReference<List<ResourceServer>>() {};
	private static final String RESOURCE_SERVERS_FILE_NAME = "resource_servers";
	private final DocumentFQN dataFileFQN = new DocumentFQN(RESOURCE_SERVERS_FILE_NAME);
	// The identity of this server instance.
	private final UserIDAuth userIDAuth;

	public FsResourceServerRepository(UserIDAuth userIDAuth, DocumentSafeService documentSafeService, ObjectMapper objectMapper) {
		super(documentSafeService, objectMapper);
    	this.userIDAuth = userIDAuth;
    }

    @Override
    public List<ResourceServer> getAll() {
        return loadAll();
    }

    @Override
    public void add(ResourceServer resourceServer) {
        if(!isValid(resourceServer)) 
            throw new BaseException("Resource server not valid");
        ArrayList<ResourceServer> resourceServers = new ArrayList<>(loadAll());
        add(resourceServer, resourceServers);
    }
    
    @Override
    public void addAll(Iterable<ResourceServer> serversIn) {
    	ArrayList<ResourceServer> existingServers = new ArrayList<>(loadAll());
        boolean persist = false;
        for (ResourceServer resourceServer : serversIn) {
            add(resourceServer, existingServers);
            persist = true;
        }
        if(persist)persist(existingServers);
    }

    private Map<String, ResourceServer> mapResourceServers(List<ResourceServer> resourceServers) {
        return resourceServers.stream().collect(Collectors.toMap(ResourceServer::getAudience, Function.identity()));
    }

    private void add(ResourceServer resourceServer, final ArrayList<ResourceServer> existingServers) {
        addInternal(resourceServer, existingServers);
        persist(existingServers);
    }
    
    // Add without persisting.
    private void addInternal(ResourceServer resourceServer, final ArrayList<ResourceServer> existingServers) {
        Map<String, ResourceServer> resourceServerMap = mapResourceServers(existingServers);

        String audience = resourceServer.getAudience();
        if(resourceServerMap.containsKey(audience)) {
            ResourceServer existingResourceServer = resourceServerMap.get(audience);

            if(!existingResourceServer.equals(resourceServer)) {
                int indexOf = existingServers.indexOf(existingResourceServer);
                existingServers.set(indexOf, resourceServer);
            }
        } else {
            existingServers.add(resourceServer);
        }
    }
    
    private List<ResourceServer> loadAll() {
    	if(!super.documentExists(userIDAuth, dataFileFQN))
    		return new ArrayList<>();
    	return load(userIDAuth, dataFileFQN, RESOURCE_SERVER_LIST_TYPE).orElse(new ArrayList<>());
    }

    private void persist(List<ResourceServer> existingServers) {
    	storeDocument(userIDAuth, dataFileFQN, existingServers);
    }
    
    private boolean isValid(ResourceServer resourceServer) {
        return !StringUtils.isBlank(resourceServer.getAudience());
    }
}
