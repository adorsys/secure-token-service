package de.adorsys.sts.simpleencryption;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SymmetricEncryptionTest {

    private static final String A_256_GCMKW_ALGORITHM_NAME = "A256GCMKW";
    private static final String A_256_GCM_ENCRYPTION_METHOD_NAME = "A256GCM";

    private StaticKeyEncryptionFactory encryptionFactory;
    private ObjectEncryption objectEncryption;

    @BeforeEach
    public void setup() throws Exception {
        encryptionFactory = new StaticKeyEncryptionFactory(new JacksonObjectMapper());
        String secretKeyAsJson = createSecretKey();

        objectEncryption = encryptionFactory.create(
                A_256_GCMKW_ALGORITHM_NAME,
                A_256_GCM_ENCRYPTION_METHOD_NAME,
                secretKeyAsJson
        );
    }

    @Test
    void shouldDecryptEncryptedText() {
        String plainText = "my super secret plain text which needs to be encrypted";
        String encrypted = objectEncryption.encrypt(plainText);

        String decrypted = objectEncryption.decrypt(encrypted);

        assertThat(decrypted).isEqualTo(plainText);
    }

    @Test
    void shouldDecryptEncryptedObject() {
        TestObject plainObject = getTestObject();
        String encrypted = objectEncryption.encrypt(plainObject);

        TestObject decrypted = objectEncryption.decrypt(encrypted, TestObject.class);

        assertThat(decrypted).isEqualTo(plainObject);
    }

    @Test
    void shouldNotDecryptEncryptedTextWithWrongKey() throws Exception {
        String plainText = "my super secret plain text which needs to be encrypted";
        String encrypted = objectEncryption.encrypt(plainText);

        String otherSecretKeyAsJson = createSecretKey();
        ObjectEncryption decryptionWithWrongKey = encryptionFactory.create(
                A_256_GCMKW_ALGORITHM_NAME,
                A_256_GCM_ENCRYPTION_METHOD_NAME,
                otherSecretKeyAsJson
        );
        EncryptionException exception = assertThrows(EncryptionException.class, () -> decryptionWithWrongKey.decrypt(encrypted));

        assertNotNull(exception);
    }

    @Test
    void shouldNotDecryptEncryptedObjectWithWrongKey() throws Exception {
        TestObject plainObject = getTestObject();
        String encrypted = objectEncryption.encrypt(plainObject);

        String otherSecretKeyAsJson = createSecretKey();
        ObjectEncryption decryptionWithWrongKey = encryptionFactory.create(
                A_256_GCMKW_ALGORITHM_NAME,
                A_256_GCM_ENCRYPTION_METHOD_NAME,
                otherSecretKeyAsJson
        );

        EncryptionException exception = assertThrows(EncryptionException.class, () -> decryptionWithWrongKey.decrypt(encrypted, TestObject.class));

        assertNotNull(exception);
    }

    private String createSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);

        SecretKey secretKey = keyGen.generateKey();

        JWK jwk = new OctetSequenceKey.Builder(secretKey)
                .keyID(UUID.randomUUID().toString())
                .algorithm(EncryptionMethod.A256GCM)
                .build();

        return jwk.toJSONString();
    }

    private TestObject getTestObject() {
        return new TestObject(
                "my string",
                234234,
                34234.24234,
                23434012L,
                LocalDate.of(2017, 3, 3),
                LocalDateTime.of(2017, 3, 3, 8, 3, 43),
                new TestObject.InnerTestObject("my inner string", 234987234, 24234.234)
        );
    }
}
