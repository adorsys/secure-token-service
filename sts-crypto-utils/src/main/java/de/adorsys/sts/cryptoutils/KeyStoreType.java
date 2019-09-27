package de.adorsys.sts.cryptoutils;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode(callSuper = true)
public class KeyStoreType extends BaseTypeString {
    private static final String KEYSTORE_TYPE = "KEYSTORE_TYPE";

    public static final KeyStoreType BOUNCY_CASTLE = new KeyStoreType("UBER");
    public static final KeyStoreType DEFAULT = getDefaultKeyStoreType();

    public KeyStoreType(@NonNull String value) {
        super(value);
    }

    private static KeyStoreType getDefaultKeyStoreType() {
        String keystoreType = System.getProperty(KEYSTORE_TYPE);
        if (null == keystoreType || "".equals(keystoreType.trim())) {
            return BOUNCY_CASTLE;
        }

        return new KeyStoreType(keystoreType);
    }
}
