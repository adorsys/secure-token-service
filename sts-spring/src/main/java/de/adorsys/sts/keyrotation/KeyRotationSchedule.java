package de.adorsys.sts.keyrotation;

import de.adorsys.lockpersistence.client.LockClient;
import de.adorsys.sts.keymanagement.model.StsKeyStore;
import de.adorsys.sts.keymanagement.persistence.KeyStoreRepository;
import de.adorsys.sts.keymanagement.service.KeyManagementProperties;
import de.adorsys.sts.keymanagement.service.KeyRotationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class KeyRotationSchedule {
    private final static Logger LOG = LoggerFactory.getLogger(KeyRotationSchedule.class);

    private final KeyRotationService keyRotationService;
    private final KeyStoreRepository keyStoreRepository;

    private final LockClient lockClient;

    private final String keyStoreName;

    @Autowired
    public KeyRotationSchedule(
            KeyRotationService keyRotationService,
            KeyStoreRepository keyStoreRepository,
            LockClient lockClient,
            KeyManagementProperties properties
    ) {
        this.keyRotationService = keyRotationService;
        this.keyStoreRepository = keyStoreRepository;
        this.lockClient = lockClient;
        this.keyStoreName = properties.getKeystore().getName();
    }

    @Scheduled(
            initialDelayString = "${sts.keymanagement.rotation.checkInterval:60000}",
            fixedDelayString = "${sts.keymanagement.rotation.checkInterval:60000}"
    )
    public void scheduledRotation() {

        lockClient.executeIfOwned(keyStoreName, () -> {
            if(keyStoreRepository.exists()) {
                LOG.debug("Perform key rotation...");

                performKeyRotation();

                LOG.debug("Key rotation finished.");
            } else {
                LOG.debug("No key rotation needed. Keystore repository is (still) empty.");
            }
        });
    }

    private void performKeyRotation() {
        StsKeyStore keyStore = keyStoreRepository.load();
        KeyRotationService.KeyRotationResult keyRotationResult = keyRotationService.rotate(keyStore);

        if(LOG.isDebugEnabled()) {

            List<String> removedKeys = keyRotationResult.getRemovedKeys();
            LOG.debug(removedKeys.size() + " keys removed: [" + removedKeys.stream().collect(Collectors.joining(",")) + "]");

            List<String> futureKeys = keyRotationResult.getFutureKeys();
            LOG.debug(futureKeys.size() + " future keys generated: [" + futureKeys.stream().collect(Collectors.joining(",")) + "]");

            List<String> generatedKeys = keyRotationResult.getGeneratedKeys();
            LOG.debug(generatedKeys.size() + " keys generated: [" + generatedKeys.stream().collect(Collectors.joining(",")) + "]");
        }

        keyStoreRepository.save(keyStore);
    }
}
