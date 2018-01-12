package de.adorsys.sts.keymanagement;

import de.adorsys.sts.keymanagement.model.StsKeyStore;
import de.adorsys.sts.keymanagement.persistence.KeyStoreRepository;
import de.adorsys.sts.keymanagement.service.KeyRotationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class KeyRotationSchedule {
    private final static Logger LOG = LoggerFactory.getLogger(KeyRotationSchedule.class);

    private final KeyRotationService keyRotationService;
    private final KeyStoreRepository keyStoreRepository;

    @Autowired
    public KeyRotationSchedule(
            KeyRotationService keyRotationService,
            KeyStoreRepository keyStoreRepository
    ) {
        this.keyRotationService = keyRotationService;
        this.keyStoreRepository = keyStoreRepository;
    }

    @Scheduled(
            initialDelayString = "${sts.keymanagement.keystore.rotationCheckInterval}",
            fixedDelayString = "${sts.keymanagement.keystore.rotationCheckInterval}"
    )
    public void performEncryptionKeyPairRotation() {
        LOG.debug("Perform key rotation...");

        StsKeyStore keyStore = keyStoreRepository.load();
        KeyRotationService.KeyRotationResult keyRotationResult = keyRotationService.rotate(keyStore);

        if(LOG.isDebugEnabled()) {

            List<String> removedKeys = keyRotationResult.getRemovedKeys();
            LOG.debug(removedKeys.size() + " keys removed: [" + removedKeys.stream().collect(Collectors.joining(",")) + "]");

            List<String> generatedKeys = keyRotationResult.getGeneratedKeys();
            LOG.debug(generatedKeys.size() + " keys generated: [" + generatedKeys.stream().collect(Collectors.joining(",")) + "]");
        }

        keyStoreRepository.save(keyStore);

        LOG.debug("Key rotation finished.");
    }
}
