package de.adorsys.sts.keymanagement.config;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Component
@ConfigurationProperties(prefix = "sts.keymanagement")
@Validated
public class KeyManagementProperties {

    @Valid
    private PersistenceProperties persistence;

    @Valid
    @NotNull
    private KeyStoreProperties keystore;

    public PersistenceProperties getPersistence() {
        return persistence;
    }

    public void setPersistence(PersistenceProperties persistence) {
        this.persistence = persistence;
    }

    public KeyStoreProperties getKeystore() {
        return keystore;
    }

    public void setKeystore(KeyStoreProperties keystore) {
        this.keystore = keystore;
    }

    @Validated
    public static class PersistenceProperties {

        @NotNull
        @NotEmpty
        private String containerName;

        @NotNull
        @NotEmpty
        private String password;

        public String getContainerName() {
            return containerName;
        }

        public void setContainerName(String containerName) {
            this.containerName = containerName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    @Validated
    public static class KeyStoreProperties {

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
        private KeysProperties keys;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAliasPrefix() {
            return aliasPrefix;
        }

        public void setAliasPrefix(String aliasPrefix) {
            this.aliasPrefix = aliasPrefix;
        }

        public KeysProperties getKeys() {
            return keys;
        }

        public void setKeys(KeysProperties keys) {
            this.keys = keys;
        }

        @Validated
        public static class KeysProperties {

            @Valid
            @NotNull
            private KeyPairProperties encKeyPairs;

            @Valid
            @NotNull
            private KeyPairProperties signKeyPairs;

            @Valid
            @NotNull
            private SecretKeyProperties secretKeys;

            public KeyPairProperties getEncKeyPairs() {
                return encKeyPairs;
            }

            public void setEncKeyPairs(KeyPairProperties encKeyPairs) {
                this.encKeyPairs = encKeyPairs;
            }

            public KeyPairProperties getSignKeyPairs() {
                return signKeyPairs;
            }

            public void setSignKeyPairs(KeyPairProperties signKeyPairs) {
                this.signKeyPairs = signKeyPairs;
            }

            public SecretKeyProperties getSecretKeys() {
                return secretKeys;
            }

            public void setSecretKeys(SecretKeyProperties secretKeys) {
                this.secretKeys = secretKeys;
            }

            @Validated
            public static class KeyPairProperties {

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

                public Integer getInitialCount() {
                    return initialCount;
                }

                public void setInitialCount(Integer initialCount) {
                    this.initialCount = initialCount;
                }

                public String getAlgo() {
                    return algo;
                }

                public void setAlgo(String algo) {
                    this.algo = algo;
                }

                public String getSigAlgo() {
                    return sigAlgo;
                }

                public void setSigAlgo(String sigAlgo) {
                    this.sigAlgo = sigAlgo;
                }

                public Integer getSize() {
                    return size;
                }

                public void setSize(Integer size) {
                    this.size = size;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }
            }

            @Validated
            public static class SecretKeyProperties {
                private Integer initialCount = 1;

                @NotNull
                @NotEmpty
                private String algo;

                @NotNull
                private Integer size;

                public Integer getInitialCount() {
                    return initialCount;
                }

                public void setInitialCount(Integer initialCount) {
                    this.initialCount = initialCount;
                }

                public String getAlgo() {
                    return algo;
                }

                public void setAlgo(String algo) {
                    this.algo = algo;
                }

                public Integer getSize() {
                    return size;
                }

                public void setSize(Integer size) {
                    this.size = size;
                }
            }
        }
    }
}
