package de.adorsys.sts.common.util;

import de.adorsys.sts.common.model.ByteArray;

import java.util.Base64;

public class Base64Encoder {

    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();

    public String toBase64(ByteArray byteArray) {
        return toBase64(byteArray.getValue());
    }

    public String toBase64(byte[] bytes) {
        byte[] encodedBase64InBytes = BASE64_URL_ENCODER.encode(bytes);

        return new String(encodedBase64InBytes);
    }

}
