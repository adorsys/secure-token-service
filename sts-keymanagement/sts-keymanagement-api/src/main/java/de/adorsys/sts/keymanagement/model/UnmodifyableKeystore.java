package de.adorsys.sts.keymanagement.model;

import de.adorsys.keymanagement.api.Juggler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Delegate;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.function.Supplier;

public class UnmodifyableKeystore {

    @Getter(AccessLevel.PACKAGE)
    @Delegate(excludes = ExcludeOperations.class)
    private final KeyStore delegate;

    public UnmodifyableKeystore(KeyStore delegate) {
        this.delegate = delegate;
    }

    public byte[] toBytes(Juggler juggler, Supplier<char[]> readKeyStorePassword) {
        return juggler.serializeDeserialize().serialize(delegate, readKeyStorePassword);
    }

    private interface ExcludeOperations {

        void load(InputStream stream, char[] password);
        void load(KeyStore.LoadStoreParameter param);
        void deleteEntry(String alias);
        void setEntry(String alias, KeyStore.Entry entry, KeyStore.ProtectionParameter protParam);
        void setCertificateEntry(String alias, Certificate cert);
        void setKeyEntry(String alias, byte[] key, Certificate[] chain);
        void setKeyEntry(String alias, Key key, char[] password, Certificate[] chain);
    }
}
