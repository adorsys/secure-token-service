package de.adorsys.sts.common.model;

import java.io.Serializable;
import java.util.Arrays;

public class BaseTypeByteArray implements Serializable {
    private byte[] value;

    protected BaseTypeByteArray() {}

    protected BaseTypeByteArray(byte[] value) {
        this.value = value;
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseTypeByteArray that = (BaseTypeByteArray) o;

        return Arrays.equals(value, that.value);

    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
