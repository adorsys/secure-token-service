package de.adorsys.sts.keymanagement;

import de.adorsys.sts.keymanagement.service.KeyManagementProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "sts.keymanagement")
@Validated
public class KeyManagementConfigurationProperties implements KeyManagementProperties {

    @Valid
    @NotNull
    private KeyStoreConfigurationProperties keystore;

    @Override
    @NotNull
    @Valid
    public KeyStoreProperties getKeystore() {
        return keystore;
    }

    public void setKeystore(KeyStoreConfigurationProperties keystore) {
        this.keystore = keystore;
    }

    @Validated
    public static class KeyStoreConfigurationProperties implements KeyStoreProperties {

        @NotNull
        @Size(min = 1)
        private String password;

        @NotNull
        @Size(min = 1)
        private String type;

        @NotNull
        @Size(min = 1)
        private String name;

        @NotNull
        @Size(min = 1)
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

                @Min(1)
                private Integer initialCount = 1;

                @NotNull
                @Size(min = 1)
                private String algo;

                @NotNull
                @Size(min = 1)
                private String sigAlgo;

                @NotNull
                @Min(1024)
                private Integer size;

                @NotNull
                @Size(min = 1)
                private String name;

                @NotNull
                @Min(1)
                private Long validityInterval;

                @NotNull
                @Min(1)
                private Long legacyInterval;

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

                @Override
                public Long getValidityInterval() {
                    return validityInterval;
                }

                @Override
                public Long getLegacyInterval() {
                    return legacyInterval;
                }

                public void setValidityInterval(Long validityInterval) {
                    this.validityInterval = validityInterval;
                }

                public void setLegacyInterval(Long legacyInterval) {
                    this.legacyInterval = legacyInterval;
                }
            }

            @Validated
            public static class SecretKeyConfigurationProperties implements SecretKeyProperties {

                @Min(1)
                private Integer initialCount = 1;

                @NotNull
                @Size(min = 1)
                private String algo;

                @NotNull
                private Integer size;

                @NotNull
                @Min(1)
                private Long validityInterval;

                @NotNull
                @Min(1)
                private Long legacyInterval;

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

                @Override
                public Long getValidityInterval() {
                    return validityInterval;
                }

                @Override
                public Long getLegacyInterval() {
                    return legacyInterval;
                }

                public void setValidityInterval(Long validityInterval) {
                    this.validityInterval = validityInterval;
                }

                public void setLegacyInterval(Long legacyInterval) {
                    this.legacyInterval = legacyInterval;
                }
            }
        }
    }
}
