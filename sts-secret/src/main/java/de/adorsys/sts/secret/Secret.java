package de.adorsys.sts.secret;

import de.adorsys.sts.common.util.RandomBase64Generator;
import de.adorsys.sts.cryptoutils.BaseTypeString;

public class Secret extends BaseTypeString {
    private static final RandomBase64Generator RANDOM_BASE_64_GENERATOR = new RandomBase64Generator();

    public Secret(String value) {
        super(value);
    }

    public static Secret generateRandom(int bytesLength) {
        String generatedBase64 = RANDOM_BASE_64_GENERATOR.generate(bytesLength);
        return new Secret(generatedBase64);
    }
}
