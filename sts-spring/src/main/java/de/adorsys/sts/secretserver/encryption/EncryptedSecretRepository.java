package de.adorsys.sts.secretserver.encryption;

import de.adorsys.sts.secret.Secret;
import de.adorsys.sts.secret.SecretEncryptionException;
import de.adorsys.sts.secret.SecretReadException;
import de.adorsys.sts.secret.SecretRepository;
import de.adorsys.sts.simpleencryption.ObjectEncryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Objects;
import java.util.Optional;

public class EncryptedSecretRepository implements SecretRepository {

    private final SecretRepository decoratedSecretRepository;
    private final ObjectEncryption objectEncryption;

    public EncryptedSecretRepository(
            SecretRepository secretRepository,
            ObjectEncryption objectEncryption
    ) {
        this.decoratedSecretRepository = secretRepository;
        this.objectEncryption = objectEncryption;
    }

    @Override
    public Secret get(String subject) throws SecretEncryptionException {
        Secret encryptedSecret = decoratedSecretRepository.get(subject);
        SecretValue decryptedSecretValue = objectEncryption.decrypt(encryptedSecret.getValue(), SecretValue.class);

        if(Objects.equals(decryptedSecretValue.getSubject(), subject)) {
            return new Secret(decryptedSecretValue.getValue());
        }

        throw new SecretEncryptionException("Encrypted subject '" + decryptedSecretValue.getSubject() + "' does not equals the requested: '" + subject + "'");
    }

    @Override
    public Optional<Secret> tryToGet(String subject) {
        Optional<Secret> decryptedSecret = Optional.empty();
        Optional<Secret> maybeEncryptedSecret = decoratedSecretRepository.tryToGet(subject);

        if(maybeEncryptedSecret.isPresent()) {
            Secret encryptedSecret = maybeEncryptedSecret.get();
            decryptedSecret = tryToDecrypt(subject, encryptedSecret);
        }

        return decryptedSecret;
    }

    private Optional<Secret> tryToDecrypt(String requestedSubject, Secret encryptedSecret) {
        Optional<Secret> decryptedSecret = Optional.empty();
        Optional<SecretValue> maybeDecryptedSecret = objectEncryption.tryToDecrypt(encryptedSecret.getValue(), SecretValue.class);

        if(maybeDecryptedSecret.isPresent()) {
            SecretValue secretValue = maybeDecryptedSecret.get();

            /*
             * This check ensures the secret server against database manipulation
             */
            if(Objects.equals(secretValue.getSubject(), requestedSubject)) {
                String actualSecret = secretValue.getValue();
                decryptedSecret = Optional.of(new Secret(actualSecret));
            } else {
                throw new SecretReadException("Data manipulation detected: got secret for subject '" + secretValue.getSubject() + "' instead of '" + requestedSubject + "'");
            }
        }

        return decryptedSecret;
    }

    @Override
    public void save(String subject, Secret secret) {
        SecretValue secretValue = new SecretValue(subject, secret.getValue());
        String encryptedSecretAsText = objectEncryption.encrypt(secretValue);

        decoratedSecretRepository.save(subject, new Secret(encryptedSecretAsText));
    }

    private static class SecretValue {
        private String subject;
        private String value;

        /**
         * Nee
         */
        private SecretValue() {
        }

        private SecretValue(String subject, String value) {
            this.subject = subject;
            this.value = value;
        }

        public String getSubject() {
            return subject;
        }

        public String getValue() {
            return value;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
