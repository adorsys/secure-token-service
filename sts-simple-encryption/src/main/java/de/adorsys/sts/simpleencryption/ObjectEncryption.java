package de.adorsys.sts.simpleencryption;

import java.util.Optional;

public interface ObjectEncryption {
    <T> T decrypt(String encrypted, Class<T> type) throws EncryptionException;
    String decrypt(String encrypted) throws EncryptionException;

    <T> Optional<T> tryToDecrypt(String encrypted, Class<T> type);
    Optional<String> tryToDecrypt(String encrypted);

    String encrypt(Object object) throws EncryptionException;
    String encrypt(String plainText) throws EncryptionException;
}
