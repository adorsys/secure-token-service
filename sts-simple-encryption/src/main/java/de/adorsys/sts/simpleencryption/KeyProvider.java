package de.adorsys.sts.simpleencryption;

import java.security.Key;

public interface KeyProvider {
    Key getKeyForEncryption();
    Key getKeyForDecryption(String keyId);
}
