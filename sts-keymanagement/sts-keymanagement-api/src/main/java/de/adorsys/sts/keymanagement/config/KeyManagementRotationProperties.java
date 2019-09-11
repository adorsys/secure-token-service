package de.adorsys.sts.keymanagement.config;

public interface KeyManagementRotationProperties {
    KeyRotationProperties getEncKeyPairs();
    KeyRotationProperties getSignKeyPairs();
    KeyRotationProperties getSecretKeys();

    interface KeyRotationProperties {
        Integer getMinKeys();
        Boolean isEnabled();
    }
}
