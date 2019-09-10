package de.adorsys.sts.keymanagement.service;

public interface KeyManagementProperties {
    KeyManagementProperties.KeyStoreProperties getKeystore();

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

                Long getValidityInterval();

                Long getLegacyInterval();
            }

            interface SecretKeyProperties {
                Integer getInitialCount();

                String getAlgo();

                Integer getSize();

                Long getValidityInterval();

                Long getLegacyInterval();
            }
        }
    }
}
