package de.adorsys.sts.resourceserver.service;

import de.adorsys.sts.resourceserver.model.UserCredentials;
import de.adorsys.sts.resourceserver.model.ResourceServerAndSecret;
import org.adorsys.encobject.domain.KeyCredentials;
import org.adorsys.encobject.filesystem.FsPersistenceFactory;
import org.adorsys.encobject.service.KeystoreNotFoundException;
import org.adorsys.encobject.userdata.ObjectMapperSPI;
import org.adorsys.encobject.userdata.ObjectPersistenceAdapter;
import org.adorsys.encobject.userdata.UserDataNamingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceServerProcessorService {

    private final ResourceServerProcessor resourceServerProcessor;

    private final UserDataNamingPolicy namingPolicy;

    private final FsPersistenceFactory persFactory;

    private final ObjectMapperSPI objectMapper;

    @Autowired
    public ResourceServerProcessorService(
            ResourceServerProcessor resourceServerProcessor,
            UserDataNamingPolicy namingPolicy,
            FsPersistenceFactory persFactory,
            ObjectMapperSPI objectMapper
    ) {
        this.resourceServerProcessor = resourceServerProcessor;
        this.namingPolicy = namingPolicy;
        this.persFactory = persFactory;
        this.objectMapper = objectMapper;
    }

    public List<ResourceServerAndSecret> processResources(String[] audiences, String[] resources, String username, String password) {
        KeyCredentials keyCredentials = namingPolicy.newKeyCredntials(username, password);

        ObjectPersistenceAdapter persistenceAdapter = new ObjectPersistenceAdapter(persFactory.getEncObjectService(), keyCredentials, objectMapper);

        // Check if we have this user in the storage. If so user the record, if not create one.
        UserDataService userDataService = new UserDataService(namingPolicy, persistenceAdapter);
        if(!userDataService.hasAccount()){
            try {
                userDataService.addAccount();
            } catch (KeystoreNotFoundException e) {
                throw new IllegalStateException();
            }
        }

        // Check access
        UserCredentials loadUserCredentials = userDataService.loadUserCredentials();

        return resourceServerProcessor.processResources(audiences, resources, userDataService);
    }

    public void storeCredentials(String login, String password, String audience, String userEncKey) {
        KeyCredentials keyCredentials = namingPolicy.newKeyCredntials(login, password);

        ObjectPersistenceAdapter persistenceAdapter = new ObjectPersistenceAdapter(persFactory.getEncObjectService(), keyCredentials, objectMapper);

        // Check if we have this user in the storage. If so user the record, if not create one.
        UserDataService userDataService = new UserDataService(namingPolicy, persistenceAdapter);
        if(!userDataService.hasAccount()){
            try {
                userDataService.addAccount();
            } catch (KeystoreNotFoundException e) {
                throw new IllegalStateException();
            }
        }

        resourceServerProcessor.storeUserCredentials(userDataService, userEncKey, audience);
    }
}
