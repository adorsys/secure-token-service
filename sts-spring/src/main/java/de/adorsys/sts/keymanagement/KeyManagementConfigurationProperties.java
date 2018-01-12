package de.adorsys.sts.keymanagement;

import de.adorsys.sts.keymanagement.service.KeyManagementProperties;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Component
@ConfigurationProperties(prefix = "sts.keymanagement")
@Validated
public class KeyManagementConfigurationProperties implements KeyManagementProperties {

    @Valid
    private PersistenceConfigurationProperties persistence;

    @Valid
    @NotNull
    private KeyStoreConfigurationProperties keystore;

    @Override
    public PersistenceProperties getPersistence() {
        return persistence;
    }

    public void setPersistence(PersistenceConfigurationProperties persistence) {
        this.persistence = persistence;
    }

    @Override
    public KeyStoreProperties getKeystore() {
        return keystore;
    }

    public void setKeystore(KeyStoreConfigurationProperties keystore) {
        this.keystore = keystore;
    }

    @Validated
    public static class PersistenceConfigurationProperties implements PersistenceProperties {

        @NotNull
        @NotEmpty
        private String containerName;

        @NotNull
        @NotEmpty
        private String password;

        @Override
        public String getContainerName() {
            return containerName;
        }

        public void setContainerName(String containerName) {
            this.containerName = containerName;
        }

        @Override
        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    @Validated
    public static class KeyStoreConfigurationProperties implements KeyStoreProperties {

        @NotNull
        @NotEmpty
        private String password;

        @NotNull
        @NotEmpty
        private String type;

        @NotNull
        @NotEmpty
        private String name;

        @NotNull
        @NotEmpty
        private String aliasPrefix;

        @Valid
        @NotNull
        private KeysConfigurationProperties keys;

        @Override
        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String getAliasPrefix() {
            return aliasPrefix;
        }

        public void setAliasPrefix(String aliasPrefix) {
            this.aliasPrefix = aliasPrefix;
        }

        @Override
        public KeysProperties getKeys() {
            return keys;
        }

        public void setKeys(KeysConfigurationProperties keys) {
            this.keys = keys;
        }

        @Validated
        public static class KeysConfigurationProperties implements KeysProperties {

            @Valid
            @NotNull
            private KeyPairConfigurationProperties encKeyPairs;

            @Valid
            @NotNull
            private KeyPairConfigurationProperties signKeyPairs;

            @Valid
            @NotNull
            private SecretKeyConfigurationProperties secretKeys;

            @Override
            public KeyPairProperties getEncKeyPairs() {
                return encKeyPairs;
            }

            public void setEncKeyPairs(KeyPairConfigurationProperties encKeyPairs) {
                this.encKeyPairs = encKeyPairs;
            }

            @Override
            public KeyPairProperties getSignKeyPairs() {
                return signKeyPairs;
            }

            public void setSignKeyPairs(KeyPairConfigurationProperties signKeyPairs) {
                this.signKeyPairs = signKeyPairs;
            }

            @Override
            public SecretKeyProperties getSecretKeys() {
                return secretKeys;
            }

            public void setSecretKeys(SecretKeyConfigurationProperties secretKeys) {
                this.secretKeys = secretKeys;
            }

            @Validated
            public static class KeyPairConfigurationProperties implements KeyPairProperties {

                @NotNull
                @Valid
                private KeyRotationConfigurationProperties rotation;

                private Integer initialCount = 1;

                @NotNull
                @NotEmpty
                private String algo;

                @NotNull
                @NotEmpty
                private String sigAlgo;

                @NotNull
                private Integer size;

                @NotNull
                @NotEmpty
                private String name;

                @Override
                public KeyRotationProperties getRotation() {
                    return rotation;
                }

                public void setRotation(KeyRotationConfigurationProperties rotation) {
                    this.rotation = rotation;
                }

                @Override
                public Integer getInitialCount() {
                    return initialCount;
                }

                public void setInitialCount(Integer initialCount) {
                    this.initialCount = initialCount;
                }

                @Override
                public String getAlgo() {
                    return algo;
                }

                public void setAlgo(String algo) {
                    this.algo = algo;
                }

                @Override
                public String getSigAlgo() {
                    return sigAlgo;
                }

                public void setSigAlgo(String sigAlgo) {
                    this.sigAlgo = sigAlgo;
                }

                @Override
                public Integer getSize() {
                    return size;
                }

                public void setSize(Integer size) {
                    this.size = size;
                }

                @Override
                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }
            }

            @Validated
            public static class SecretKeyConfigurationProperties implements SecretKeyProperties {

                @NotNull
                @Valid
                private KeyRotationConfigurationProperties rotation;

                private Integer initialCount = 1;

                @NotNull
                @NotEmpty
                private String algo;

                @NotNull
                private Integer size;

                @Override
                public KeyRotationProperties getRotation() {
                    return rotation;
                }

                public void setRotation(KeyRotationConfigurationProperties rotation) {
                    this.rotation = rotation;
                }

                @Override
                public Integer getInitialCount() {
                    return initialCount;
                }

                public void setInitialCount(Integer initialCount) {
                    this.initialCount = initialCount;
                }

                @Override
                public String getAlgo() {
                    return algo;
                }

                public void setAlgo(String algo) {
                    this.algo = algo;
                }

                @Override
                public Integer getSize() {
                    return size;
                }

                public void setSize(Integer size) {
                    this.size = size;
                }
            }

            @Validated
            public static class KeyRotationConfigurationProperties implements KeyRotationProperties {

                @NotNull
                @Min(1)
                private Long validityInterval;

                @NotNull
                @Min(1)
                private Long legacyInterval;

                @NotNull
                @Min(1)
                private Integer minKeys;

                private Boolean enabled = false;

                @Override
                public Long getValidityInterval() {
                    return validityInterval;
                }

                @Override
                public Long getLegacyInterval() {
                    return legacyInterval;
                }

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

                public void setValidityInterval(Long validityInterval) {
                    this.validityInterval = validityInterval;
                }

                public void setLegacyInterval(Long legacyInterval) {
                    this.legacyInterval = legacyInterval;
                }

                public void setMinKeys(Integer minKeys) {
                    this.minKeys = minKeys;
                }
            }
        }
    }
}
