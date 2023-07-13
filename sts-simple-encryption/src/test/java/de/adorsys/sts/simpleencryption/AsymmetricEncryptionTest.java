package de.adorsys.sts.simpleencryption;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import org.junit.Before;
import org.junit.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

public class AsymmetricEncryptionTest {

    private static final String RSA_OAEP_256 = "RSA-OAEP-256";
    private static final String A_256_GCM = "A256GCM";
    private StaticKeyEncryptionFactory encryptionFactory;
    private ObjectEncryption objectEncryption;

    @Before
    public void setup() throws Exception {
        String keyPairAsJson = createKeyPair();
        encryptionFactory = new StaticKeyEncryptionFactory(new JacksonObjectMapper());
        objectEncryption = encryptionFactory.create(
                RSA_OAEP_256,
                A_256_GCM,
                keyPairAsJson
        );
    }

    @Test
    public void shouldDecryptEncryptedText() {
        String plainText = "my super secret plain text which needs to be encrypted";
        String encrypted = objectEncryption.encrypt(plainText);

        String decrypted = objectEncryption.decrypt(encrypted);

        assertThat(decrypted, is(equalTo(plainText)));
    }

    @Test
    public void shouldDecryptEncryptedObject() {
        TestObject plainObject = getTestObject();
        String encrypted = objectEncryption.encrypt(plainObject);

        TestObject decrypted = objectEncryption.decrypt(encrypted, TestObject.class);

        assertThat(decrypted, is(equalTo(plainObject)));
    }

    @Test
    public void shouldNotDecryptEncryptedTextWithWrongKey() throws Exception {
        String plainText = "my super secret plain text which needs to be encrypted";
        String encrypted = objectEncryption.encrypt(plainText);

        String otherKeyPairAsJson = createKeyPair();
        ObjectEncryption decryptionWithWrongKey = encryptionFactory.create(
                RSA_OAEP_256,
                A_256_GCM,
                otherKeyPairAsJson
        );


        EncryptionException exception = assertThrows(EncryptionException.class, () -> decryptionWithWrongKey.decrypt(encrypted));

        assertNotNull(exception);
    }

    @Test
    public void shouldNotDecryptEncryptedObjectWithWrongKey() throws Exception {
        TestObject plainObject = getTestObject();
        String encrypted = objectEncryption.encrypt(plainObject);

        String otherKeyPairAsJson = createKeyPair();
        ObjectEncryption decryptionWithWrongKey = encryptionFactory.create(
                RSA_OAEP_256,
                A_256_GCM,
                otherKeyPairAsJson
        );

        EncryptionException exception = assertThrows(EncryptionException.class, () -> decryptionWithWrongKey.decrypt(encrypted, TestObject.class));

        assertNotNull(exception);
    }

    private String createKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);

        KeyPair keyPair = keyGen.genKeyPair();

        JWK jwk = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .keyUse(KeyUse.ENCRYPTION)
                .keyID(UUID.randomUUID().toString())
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
