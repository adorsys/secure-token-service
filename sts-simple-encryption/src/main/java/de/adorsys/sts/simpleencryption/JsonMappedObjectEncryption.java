package de.adorsys.sts.simpleencryption;

import de.adorsys.sts.cryptoutils.ObjectMapperSPI;
import de.adorsys.sts.simpleencryption.decrypt.Decrypter;
import de.adorsys.sts.simpleencryption.encrypt.Encrypter;

import java.io.IOException;
import java.util.Optional;

class JsonMappedObjectEncryption implements ObjectEncryption {

    private final ObjectMapperSPI jsonMapper;
    private final Encrypter encrypter;
    private final Decrypter decrypter;

    JsonMappedObjectEncryption(ObjectMapperSPI jsonMapper, Encrypter encrypter, Decrypter decrypter) {
        this.jsonMapper = jsonMapper;
        this.encrypter = encrypter;
        this.decrypter = decrypter;
    }

    @Override
    public <T> T decrypt(String encrypted, Class<T> type) {
        String decryptedJson = decrypter.decrypt(encrypted);

        try {
            return jsonMapper.readValue(decryptedJson, type);
        } catch (IOException e) {
            throw new EncryptionException(e);
        }
    }

    @Override
    public String decrypt(String encrypted) {
        return decrypter.decrypt(encrypted);
    }

    @Override
    public <T> Optional<T> tryToDecrypt(String encrypted, Class<T> type) {
        Optional<T> decrypted = Optional.empty();
        Optional<String> maybeDecrypted = decrypter.tryToDecrypt(encrypted);

        if(maybeDecrypted.isPresent()) {
            try {
                T t = jsonMapper.readValue(maybeDecrypted.get(), type);
                decrypted = Optional.of(t);
            } catch (IOException e) {
                decrypted = Optional.empty();
            }
        }

        return decrypted;
    }

    @Override
    public Optional<String> tryToDecrypt(String encrypted) {
        return decrypter.tryToDecrypt(encrypted);
    }

    @Override
    public String encrypt(Object object) throws EncryptionException {
        String json;
        try {
            json = jsonMapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new EncryptionException(e);
        }

        return encrypter.encrypt(json);
    }

    @Override
    public String encrypt(String plainText) {
        return encrypter.encrypt(plainText);
    }
}
