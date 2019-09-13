package de.adorsys.sts.keyrotation;

import com.google.common.annotations.VisibleForTesting;
import de.adorsys.sts.common.lock.LockClient;
import de.adorsys.sts.keymanagement.model.KeyRotationResult;
import de.adorsys.sts.keymanagement.model.StsKeyStore;
import de.adorsys.sts.keymanagement.persistence.KeyStoreRepository;
import de.adorsys.sts.keymanagement.service.KeyManagementProperties;
import de.adorsys.sts.keymanagement.service.KeyRotationService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Component
public class KeyRotationSchedule {
    private static final Logger LOG = LoggerFactory.getLogger(KeyRotationSchedule.class);

    private final KeyRotationService keyRotationService;
    private final KeyStoreRepository keyStoreRepository;

    private final LockClient lockClient;

    private final String rotationLockName;

    private final Clock clock;

    @Getter
    @Value("test.value")
    private String testValue;

    @Autowired
    public KeyRotationSchedule(
            KeyRotationService keyRotationService,
            @Qualifier("cached") KeyStoreRepository keyStoreRepository,
            LockClient lockClient,
            KeyManagementProperties properties,
            Clock clock
    ) {
        this.keyRotationService = keyRotationService;
        this.keyStoreRepository = keyStoreRepository;
        this.lockClient = lockClient;
        this.clock = clock;

        String keyStoreName = properties.getKeystore().getName();
        this.rotationLockName = "key-rotation -- " + keyStoreName;
    }

    @Scheduled(
            initialDelayString = "${sts.keymanagement.rotation.check-interval:60000}",
            fixedDelayString = "${sts.keymanagement.rotation.check-interval:60000}"
    )
    public void scheduledRotation() {
        lockClient.executeIfOwned(rotationLockName, this::doRotate);
    }

    @VisibleForTesting
    protected void doRotate() {
        if (keyStoreRepository.exists()) {
            LOG.info("Will perform");
            LOG.debug("Perform key rotation...");

            performKeyRotation();

            LOG.debug("Key rotation finished.");
        } else {
            LOG.debug("No key rotation needed. Keystore repository is (still) empty.");
        }
    }

    private void performKeyRotation() {
        StsKeyStore keyStore = keyStoreRepository.load();
        KeyRotationResult keyRotationResult = keyRotationService.rotate(keyStore);

        List<String> removedKeys = keyRotationResult.getRemovedKeys();
        List<String> futureKeys = keyRotationResult.getFutureKeys();
        List<String> generatedKeys = keyRotationResult.getGeneratedKeys();

        LOG.debug("{} keys removed: {}", removedKeys.size(), removedKeys);
        LOG.debug("{} future keys generated: {}", futureKeys.size(), futureKeys);
        LOG.debug("{} keys generated: {}", generatedKeys.size(), generatedKeys);

        if(removedKeys.size() + futureKeys.size() + generatedKeys.size() > 0) {
            keyStore.setLastUpdate(now());
            keyStoreRepository.save(keyStore);
        }
    }

    private ZonedDateTime now() {
        return clock.instant().atZone(ZoneOffset.UTC);
    }
}
