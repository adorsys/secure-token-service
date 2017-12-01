package de.adorsys.sts.common.config;

public interface KeyManagementProperties {
    KeyManagementProperties.PersistenceProperties getPersistence();
    KeyManagementProperties.KeyStoreProperties getKeystore();

    interface PersistenceProperties {
        String getContainerName();
        String getPassword();
    }

    interface KeyStoreProperties {
        String getPassword();
        String getType();
        String getName();
        String getAliasPrefix();
        KeyManagementProperties.KeyStoreProperties.KeysProperties getKeys();

        interface KeysProperties {
            KeyManagementProperties.KeyStoreProperties.KeysProperties.KeyPairProperties getEncKeyPairs();
            KeyManagementProperties.KeyStoreProperties.KeysProperties.KeyPairProperties getSignKeyPairs();
            KeyManagementProperties.KeyStoreProperties.KeysProperties.SecretKeyProperties getSecretKeys();

            interface KeyPairProperties {
                Integer getInitialCount();
                String getAlgo();
                String getSigAlgo();
                Integer getSize();
                String getName();
            }

            interface SecretKeyProperties {
                Integer getInitialCount();
                String getAlgo();
                Integer getSize();
            }
        }
    }
}
