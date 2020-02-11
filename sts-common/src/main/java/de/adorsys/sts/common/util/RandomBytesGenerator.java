package de.adorsys.sts.common.util;

import de.adorsys.sts.common.model.ByteArray;

import java.security.SecureRandom;

public class RandomBytesGenerator {

    public ByteArray generate(int size) {
        byte[] generatedBytes = new byte[size];

        SecureRandom random = new SecureRandom();
        random.nextBytes(generatedBytes);

        return new ByteArray(generatedBytes);
    }
}
