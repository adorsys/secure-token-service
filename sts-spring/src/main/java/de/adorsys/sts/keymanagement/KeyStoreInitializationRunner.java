package de.adorsys.sts.keymanagement;

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

    @Autowired
    public KeyStoreInitializationRunner(KeyStoreInitializer initializer) {
        this.initializer = initializer;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Check if keys for keystore are needed to be initialized...");

        boolean hasBeenInitialized = initializer.initialize();

        if(hasBeenInitialized) {
            logger.info("Key initialization completed.");
        } else {
            logger.info("Key initialization skipped.");
        }
    }
}