package de.adorsys.sts.resourceserver.processing;

import de.adorsys.sts.resourceserver.model.ResourceServerAndSecret;
import de.adorsys.sts.resourceserver.service.UserDataRepository;

import java.util.List;

public class ResourceServerProcessorService {

    private final ResourceServerProcessor resourceServerProcessor;
    private final UserDataRepository userDataRepository;

    public ResourceServerProcessorService(ResourceServerProcessor resourceServerProcessor,
			UserDataRepository userDataRepository) {
		this.resourceServerProcessor = resourceServerProcessor;
		this.userDataRepository = userDataRepository;
	}

	public List<ResourceServerAndSecret> processResources(String[] audiences, String[] resources, String username, String password) {
		createOrCheckAccess(username, password);
		return resourceServerProcessor.processResources(audiences, resources, userDataRepository, username, password);
    }

    public void storeCredentials(String username, String password, String audience, String userEncKey) {
    	createOrCheckAccess(username, password);
        resourceServerProcessor.storeUserCredentials(userDataRepository, userEncKey, audience, username, password);
    }
    
    /*
     * Shall create the user if user does not exists. Store user record using given password.
     * 
     * Shall load the user record using given password and throw exception if password is wrong.
     */
    private void createOrCheckAccess(String username, String password){
		// Add account if not existent. Return is existent without checking password.
    	userDataRepository.addAccount(username, password);
        // Check access
        userDataRepository.loadUserCredentials(username, password);
    }
}
