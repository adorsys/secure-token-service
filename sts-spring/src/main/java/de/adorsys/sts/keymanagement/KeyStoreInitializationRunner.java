package de.adorsys.sts.keymanagement;

import de.adorsys.lockpersistence.client.LockClient;
import de.adorsys.sts.keymanagement.service.KeyManagementProperties;
import de.adorsys.sts.keymanagement.service.KeyStoreInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class KeyStoreInitializationRunner implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(KeyStoreInitializationRunner.class);

    private final KeyStoreInitializer initializer;
    private final LockClient lockClient;
    private final String initializationLockName;

    @Autowired
    public KeyStoreInitializationRunner(
            KeyStoreInitializer initializer,
            LockClient lockClient,
            KeyManagementProperties properties
    ) {
        this.initializer = initializer;
        this.lockClient = lockClient;

        String keyStoreName = properties.getKeystore().getName();
        this.initializationLockName = "keystore initialization -- " + keyStoreName;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        lockClient.executeIfOwned(initializationLockName, () -> {
            logger.info("Check if keys for keystore are needed to be initialized...");

            boolean hasBeenInitialized = initializer.initialize();

            if(hasBeenInitialized) {
                logger.info("Key initialization completed.");
            } else {
                logger.info("Key initialization skipped.");
            }
        });
    }
}
