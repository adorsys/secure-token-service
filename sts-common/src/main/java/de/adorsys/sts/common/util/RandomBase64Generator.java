package de.adorsys.sts.common.util;

import de.adorsys.sts.common.model.ByteArray;

public class RandomBase64Generator {

    private final RandomBytesGenerator randomBytesGenerator = new RandomBytesGenerator();
    private final Base64Encoder base64Encoder = new Base64Encoder();

    public String generate(int bytesLength) {
        ByteArray stateBytes = randomBytesGenerator.generate(bytesLength);
        return base64Encoder.toBase64(stateBytes);
    }
}
