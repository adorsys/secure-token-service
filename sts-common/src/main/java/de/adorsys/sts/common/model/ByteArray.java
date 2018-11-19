package de.adorsys.sts.common.model;


public class ByteArray extends BaseTypeByteArray {
    public ByteArray(byte[] value) {
        super(value);
    }

    public ByteArray(int[] unsignedOctets) {
        super(toSigned(unsignedOctets));
    }

    private static byte[] toSigned(int[] integers) {
        byte[] bytes = new byte[integers.length];

        for(int i = 0; i < integers.length; i++) {
            int integer = integers[i];
            bytes[i] = (byte)integer;
        }

        return bytes;
    }
}
