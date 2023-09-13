package de.adorsys.sts.keyrotation;

import de.adorsys.sts.keymanagement.config.KeyManagementRotationProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;


@Component
@ConfigurationProperties(prefix = "sts.keymanagement.rotation")
@Validated
public class KeyRotationManagementConfigurationProperties implements KeyManagementRotationProperties {

    @Valid
    @NotNull
    private KeyRotationConfigurationProperties encKeyPairs;

    @Valid
    @NotNull
    private KeyRotationConfigurationProperties signKeyPairs;

    @Valid
    @NotNull
    private KeyRotationConfigurationProperties secretKeys;

    @Override
    public KeyRotationProperties getEncKeyPairs() {
        return encKeyPairs;
    }

    @Override
    public KeyRotationProperties getSignKeyPairs() {
        return signKeyPairs;
    }

    @Override
    public KeyRotationProperties getSecretKeys() {
        return secretKeys;
    }

    public void setEncKeyPairs(KeyRotationConfigurationProperties encKeyPairs) {
        this.encKeyPairs = encKeyPairs;
    }

    public void setSignKeyPairs(KeyRotationConfigurationProperties signKeyPairs) {
        this.signKeyPairs = signKeyPairs;
    }

    public void setSecretKeys(KeyRotationConfigurationProperties secretKeys) {
        this.secretKeys = secretKeys;
    }

    @Validated
    public static class KeyRotationConfigurationProperties implements KeyRotationProperties {

        @NotNull
        @Min(1)
        private Integer minKeys = 5;

        private Boolean enabled = false;

        @Override
        public Integer getMinKeys() {
            return minKeys;
        }

        @Override
        public Boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public void setMinKeys(Integer minKeys) {
            this.minKeys = minKeys;
        }
    }
}