package de.adorsys.sts.keymanagement.service;

import com.nitorcreations.junit.runners.NestedRunner;
import de.adorsys.sts.CollectionHelpers;
import de.adorsys.sts.keymanagement.model.KeyUsage;
import de.adorsys.sts.keymanagement.model.StsKeyEntry;
import de.adorsys.sts.keymanagement.model.StsKeyStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.KeyStore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;

@RunWith(NestedRunner.class)
public class KeyRotationServiceTest {

    KeyRotationService keyRotationService;

    @Mock
    StsKeyStore stsKeyStore;

    @Mock
    KeyStore keyStore;

    Map<String, StsKeyEntry> keyEntries;

    static final String SIGNATURE_KEY_ALIAS = "signature key";
    static final String ENCRYPTION_KEY_ALIAS = "encryption key";
    static final String SECRET_KEY_ALIAS = "secret key";

    @Mock
    StsKeyEntry signatureKeyEntry;

    @Mock
    StsKeyEntry encryptionKeyEntry;

    @Mock
    StsKeyEntry secretKeyEntry;

    @Mock
    KeyStoreFilter keyStoreFilter;

    @Mock
    KeyStoreGenerator keyStoreGenerator;

    @Mock
    StsKeyEntry generatedEncryptionKeyPair;

    @Mock
    StsKeyEntry generatedSignatureKeyPair;

    @Mock
    StsKeyEntry generatedSecretKey;

    @Mock
    KeyManagementProperties.KeyStoreProperties.KeysProperties.KeyRotationProperties encryptionKeyPairRotationProperties;

    @Mock
    KeyManagementProperties.KeyStoreProperties.KeysProperties.KeyRotationProperties signatureKeyPairRotationProperties;

    @Mock
    KeyManagementProperties.KeyStoreProperties.KeysProperties.KeyRotationProperties secretKeyRotationProperties;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        keyEntries = new HashMap<>();

        when(signatureKeyEntry.getAlias()).thenReturn(SIGNATURE_KEY_ALIAS);
        when(signatureKeyEntry.getKeyUsage()).thenReturn(KeyUsage.Signature);

        when(encryptionKeyEntry.getAlias()).thenReturn(ENCRYPTION_KEY_ALIAS);
        when(encryptionKeyEntry.getKeyUsage()).thenReturn(KeyUsage.Encryption);

        when(secretKeyEntry.getAlias()).thenReturn(SECRET_KEY_ALIAS);
        when(secretKeyEntry.getKeyUsage()).thenReturn(KeyUsage.SecretKey);

        keyEntries.put(SIGNATURE_KEY_ALIAS, signatureKeyEntry);
        keyEntries.put(ENCRYPTION_KEY_ALIAS, encryptionKeyEntry);
        keyEntries.put(SECRET_KEY_ALIAS, secretKeyEntry);

        when(stsKeyStore.getKeyEntries()).thenReturn(keyEntries);
        when(stsKeyStore.getKeyStore()).thenReturn(keyStore);

        when(keyStoreGenerator.generateEncryptionKeyPair()).thenReturn(generatedEncryptionKeyPair);
        when(keyStoreGenerator.generateSignKeyPair()).thenReturn(generatedSignatureKeyPair);
        when(keyStoreGenerator.generateSecretKey()).thenReturn(generatedSecretKey);

        when(encryptionKeyPairRotationProperties.getMinKeys()).thenReturn(1);
        when(signatureKeyPairRotationProperties.getMinKeys()).thenReturn(1);
        when(secretKeyRotationProperties.getMinKeys()).thenReturn(1);

        when(encryptionKeyPairRotationProperties.isEnabled()).thenReturn(true);
        when(signatureKeyPairRotationProperties.isEnabled()).thenReturn(true);
        when(secretKeyRotationProperties.isEnabled()).thenReturn(true);

        keyRotationService = new KeyRotationService(
                keyStoreFilter,
                keyStoreGenerator,
                encryptionKeyPairRotationProperties,
                signatureKeyPairRotationProperties,
                secretKeyRotationProperties
        );
    }

    public class KeyStoreWithExpiredSignatureKeyPair {

        private KeyRotationService.KeyRotationResult keyRotationResult;

        @Before
        public void setup() throws Exception {
            when(keyStoreFilter.filterValid(keyEntries.values())).thenReturn(CollectionHelpers.asList(ENCRYPTION_KEY_ALIAS));
            when(keyStoreFilter.filterLegacy(keyEntries.values())).thenReturn(CollectionHelpers.asList(SECRET_KEY_ALIAS));

            when(keyStoreFilter.isInvalid(signatureKeyEntry)).thenReturn(true);
            when(keyStoreFilter.isInvalid(encryptionKeyEntry)).thenReturn(false);
            when(keyStoreFilter.isInvalid(secretKeyEntry)).thenReturn(false);

            when(keyStoreFilter.isValid(signatureKeyEntry)).thenReturn(false);
            when(keyStoreFilter.isValid(encryptionKeyEntry)).thenReturn(true);
            when(keyStoreFilter.isValid(secretKeyEntry)).thenReturn(true);

            keyRotationResult = keyRotationService.rotate(stsKeyStore);
        }

        @Test
        public void shouldGenerateNewKey() throws Exception {
            verify(stsKeyStore, times(1)).addKey(generatedSignatureKeyPair);
        }

        @Test
        public void shouldRemoveInvalidKey() throws Exception {
            verify(stsKeyStore, times(1)).removeKey(SIGNATURE_KEY_ALIAS);
        }

        @Test
        public void shouldReturnRemovedKeyAliases() {
            List<String> removedKeys = keyRotationResult.getRemovedKeys();

            assertThat(removedKeys, hasSize(1));
            assertThat(removedKeys.get(0), is(equalTo(SIGNATURE_KEY_ALIAS)));
        }

        @Test
        public void shouldReturnGeneratedKeyAliases() {
            List<String> generatedKeys = keyRotationResult.getGeneratedKeys();

            assertThat(generatedKeys, hasSize(1));
        }
    }

    public class KeyStoreWithExpiredEncryptionKeyPair {

        private KeyRotationService.KeyRotationResult keyRotationResult;

        @Before
        public void setup() throws Exception {
            when(keyStoreFilter.filterValid(keyEntries.values())).thenReturn(CollectionHelpers.asList(SECRET_KEY_ALIAS));
            when(keyStoreFilter.filterLegacy(keyEntries.values())).thenReturn(CollectionHelpers.asList(SIGNATURE_KEY_ALIAS));

            when(keyStoreFilter.isInvalid(signatureKeyEntry)).thenReturn(false);
            when(keyStoreFilter.isInvalid(encryptionKeyEntry)).thenReturn(true);
            when(keyStoreFilter.isInvalid(secretKeyEntry)).thenReturn(false);

            when(keyStoreFilter.isValid(signatureKeyEntry)).thenReturn(true);
            when(keyStoreFilter.isValid(encryptionKeyEntry)).thenReturn(false);
            when(keyStoreFilter.isValid(secretKeyEntry)).thenReturn(true);

            keyRotationResult = keyRotationService.rotate(stsKeyStore);
        }

        @Test
        public void shouldGenerateNewKey() throws Exception {
            verify(stsKeyStore, times(1)).addKey(generatedEncryptionKeyPair);
        }


        @Test
        public void shouldRemoveInvalidKey() throws Exception {
            verify(stsKeyStore, times(1)).removeKey(ENCRYPTION_KEY_ALIAS);
        }

        @Test
        public void shouldReturnRemovedKeyAliases() {
            List<String> removedKeys = keyRotationResult.getRemovedKeys();

            assertThat(removedKeys, hasSize(1));
            assertThat(removedKeys.get(0), is(equalTo(ENCRYPTION_KEY_ALIAS)));
        }

        @Test
        public void shouldReturnGeneratedKeyAliases() {
            List<String> generatedKeys = keyRotationResult.getGeneratedKeys();

            assertThat(generatedKeys, hasSize(1));
        }
    }

    public class KeyStoreWithExpiredSecretKey {

        private KeyRotationService.KeyRotationResult keyRotationResult;

        @Before
        public void setup() throws Exception {
            when(keyStoreFilter.filterValid(keyEntries.values())).thenReturn(CollectionHelpers.asList(SIGNATURE_KEY_ALIAS));
            when(keyStoreFilter.filterLegacy(keyEntries.values())).thenReturn(CollectionHelpers.asList(ENCRYPTION_KEY_ALIAS));

            when(keyStoreFilter.isInvalid(signatureKeyEntry)).thenReturn(false);
            when(keyStoreFilter.isInvalid(encryptionKeyEntry)).thenReturn(false);
            when(keyStoreFilter.isInvalid(secretKeyEntry)).thenReturn(true);

            when(keyStoreFilter.isValid(signatureKeyEntry)).thenReturn(true);
            when(keyStoreFilter.isValid(encryptionKeyEntry)).thenReturn(true);
            when(keyStoreFilter.isValid(secretKeyEntry)).thenReturn(false);

            keyRotationResult = keyRotationService.rotate(stsKeyStore);
        }

        @Test
        public void shouldGenerateNewKey() throws Exception {
            verify(stsKeyStore, times(1)).addKey(generatedSecretKey);
        }


        @Test
        public void shouldRemoveInvalidKey() throws Exception {
            verify(stsKeyStore, times(1)).removeKey(SECRET_KEY_ALIAS);
        }

        @Test
        public void shouldReturnRemovedKeyAliases() {
            List<String> removedKeys = keyRotationResult.getRemovedKeys();

            assertThat(removedKeys, hasSize(1));
            assertThat(removedKeys.get(0), is(equalTo(SECRET_KEY_ALIAS)));
        }

        @Test
        public void shouldReturnGeneratedKeyAliases() {
            List<String> generatedKeys = keyRotationResult.getGeneratedKeys();

            assertThat(generatedKeys, hasSize(1));
        }
    }
}
