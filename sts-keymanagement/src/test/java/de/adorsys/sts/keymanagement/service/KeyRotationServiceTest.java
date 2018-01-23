package de.adorsys.sts.keymanagement.service;

import com.nitorcreations.junit.runners.NestedRunner;
import de.adorsys.sts.keymanagement.config.KeyManagementRotationProperties;
import de.adorsys.sts.keymanagement.model.KeyUsage;
import de.adorsys.sts.keymanagement.model.StsKeyEntry;
import de.adorsys.sts.keymanagement.model.StsKeyStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.KeyStore;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
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

    static final LocalDateTime FIXED_DATE_TIME = LocalDateTime.of(2018, 1, 22, 14, 27, 34);
    static final ZoneId FIXED_ZONE_ID = ZoneOffset.UTC;

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
    KeyStoreGenerator keyStoreGenerator;

    @Mock
    StsKeyEntry generatedEncryptionKeyPair;

    @Mock
    StsKeyEntry generatedSignatureKeyPair;

    @Mock
    StsKeyEntry generatedSecretKey;

    @Mock
    KeyManagementRotationProperties rotationProperties;

    @Mock
    KeyManagementRotationProperties.KeyRotationProperties encryptionKeyPairRotationProperties;

    @Mock
    KeyManagementRotationProperties.KeyRotationProperties signatureKeyPairRotationProperties;

    @Mock
    KeyManagementRotationProperties.KeyRotationProperties secretKeyRotationProperties;

    final Clock clock = Clock.fixed(FIXED_DATE_TIME.atZone(FIXED_ZONE_ID).toInstant(), FIXED_ZONE_ID);

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        keyEntries = new HashMap<>();

        when(signatureKeyEntry.getAlias()).thenReturn(SIGNATURE_KEY_ALIAS);
        when(signatureKeyEntry.getKeyUsage()).thenReturn(KeyUsage.Signature);
        when(signatureKeyEntry.getState()).thenReturn(StsKeyEntry.State.VALID);
        when(signatureKeyEntry.getNotBefore()).thenReturn(FIXED_DATE_TIME.minusSeconds(1).atZone(ZoneOffset.UTC));
        when(signatureKeyEntry.getNotAfter()).thenReturn(FIXED_DATE_TIME.plusSeconds(1).atZone(ZoneOffset.UTC));
        when(signatureKeyEntry.getExpireAt()).thenReturn(FIXED_DATE_TIME.plusSeconds(2).atZone(ZoneOffset.UTC));

        when(encryptionKeyEntry.getAlias()).thenReturn(ENCRYPTION_KEY_ALIAS);
        when(encryptionKeyEntry.getKeyUsage()).thenReturn(KeyUsage.Encryption);
        when(encryptionKeyEntry.getState()).thenReturn(StsKeyEntry.State.VALID);
        when(encryptionKeyEntry.getNotBefore()).thenReturn(FIXED_DATE_TIME.minusSeconds(1).atZone(ZoneOffset.UTC));
        when(encryptionKeyEntry.getNotAfter()).thenReturn(FIXED_DATE_TIME.plusSeconds(1).atZone(ZoneOffset.UTC));
        when(encryptionKeyEntry.getExpireAt()).thenReturn(FIXED_DATE_TIME.plusSeconds(2).atZone(ZoneOffset.UTC));

        when(secretKeyEntry.getAlias()).thenReturn(SECRET_KEY_ALIAS);
        when(secretKeyEntry.getKeyUsage()).thenReturn(KeyUsage.SecretKey);
        when(secretKeyEntry.getState()).thenReturn(StsKeyEntry.State.VALID);
        when(secretKeyEntry.getNotBefore()).thenReturn(FIXED_DATE_TIME.minusSeconds(1).atZone(ZoneOffset.UTC));
        when(secretKeyEntry.getNotAfter()).thenReturn(FIXED_DATE_TIME.plusSeconds(1).atZone(ZoneOffset.UTC));
        when(secretKeyEntry.getExpireAt()).thenReturn(FIXED_DATE_TIME.plusSeconds(2).atZone(ZoneOffset.UTC));

        keyEntries.put(SIGNATURE_KEY_ALIAS, signatureKeyEntry);
        keyEntries.put(ENCRYPTION_KEY_ALIAS, encryptionKeyEntry);
        keyEntries.put(SECRET_KEY_ALIAS, secretKeyEntry);

        when(stsKeyStore.getKeyEntries()).thenReturn(keyEntries);
        when(stsKeyStore.getKeyStore()).thenReturn(keyStore);

        when(keyStoreGenerator.generateEncryptionKeyEntryForInstantUsage()).thenReturn(generatedEncryptionKeyPair);
        when(keyStoreGenerator.generateSignatureKeyEntryForInstantUsage()).thenReturn(generatedSignatureKeyPair);
        when(keyStoreGenerator.generateSecretKeyEntryForInstantUsage()).thenReturn(generatedSecretKey);

        when(encryptionKeyPairRotationProperties.getMinKeys()).thenReturn(1);
        when(signatureKeyPairRotationProperties.getMinKeys()).thenReturn(1);
        when(secretKeyRotationProperties.getMinKeys()).thenReturn(1);

        when(encryptionKeyPairRotationProperties.isEnabled()).thenReturn(true);
        when(signatureKeyPairRotationProperties.isEnabled()).thenReturn(true);
        when(secretKeyRotationProperties.isEnabled()).thenReturn(true);

        when(rotationProperties.getEncKeyPairs()).thenReturn(encryptionKeyPairRotationProperties);
        when(rotationProperties.getSignKeyPairs()).thenReturn(signatureKeyPairRotationProperties);
        when(rotationProperties.getSecretKeys()).thenReturn(secretKeyRotationProperties);

        keyRotationService = new KeyRotationService(
                keyStoreGenerator,
                clock,
                rotationProperties
        );
    }

    public class KeyStoreWithExpiredSignatureKeyPair {

        private KeyRotationService.KeyRotationResult keyRotationResult;

        @Before
        public void setup() throws Exception {
            when(signatureKeyEntry.getState()).thenReturn(StsKeyEntry.State.EXPIRED);

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
            when(encryptionKeyEntry.getState()).thenReturn(StsKeyEntry.State.EXPIRED);

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
            when(secretKeyEntry.getState()).thenReturn(StsKeyEntry.State.EXPIRED);

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
