package de.adorsys.sts.keymanagement.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sts.keymanagement")
public class KeyManagementProperties {

    private KeyStoreProperties keystore = new KeyStoreProperties();

    public KeyStoreProperties getKeystore() {
        return keystore;
    }

    public void setKeystore(KeyStoreProperties keystore) {
        this.keystore = keystore;
    }

    public static class KeyStoreProperties {

        private String password;
        private String type;
        private String name;
        private String aliasPrefix;

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

        public static class KeysProperties {

            private KeyPairProperties encKeyPairs;
            private KeyPairProperties signKeyPairs;
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

            public static class KeyPairProperties {
                 private Integer initialCount;
                 private String algo;
                 private String sigAlgo;
                 private Integer size;
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

            public static class SecretKeyProperties {
                private Integer initialCount;
                private String algo;
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
