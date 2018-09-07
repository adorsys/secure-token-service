package de.adorsys.sts.common.util;

import de.adorsys.sts.common.model.ByteArray;

import java.util.Random;

public class RandomBytesGenerator {

    public ByteArray generate(int size) {
        byte[] generatedBytes = new byte[size];

        Random random = new Random();
        random.nextBytes(generatedBytes);

        return new ByteArray(generatedBytes);
    }
}
