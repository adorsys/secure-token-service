package de.adorsys.sts.simpleencryption.decrypt;

import java.util.Optional;

public interface Decrypter {

    String decrypt(String encrypted);
    Optional<String> tryToDecrypt(String encrypted);
}
