package de.adorsys.sts.keymanagement.config;

import de.adorsys.sts.keymanagement.service.KeyManagementProperties;

public interface KeyManagementRotationProperties {
    KeyRotationProperties getEncKeyPairs();
    KeyRotationProperties getSignKeyPairs();
    KeyRotationProperties getSecretKeys();

    interface KeyRotationProperties {
        Integer getMinKeys();
        Boolean isEnabled();
    }
}
