package de.adorsys.sts.keymanagement.model;

import lombok.Builder;
import lombok.Getter;
import org.adorsys.jkeygen.keystore.KeyStoreService;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Map;

@Getter
@Builder
public class StsKeyStore {

    private final Map<String, StsKeyEntry> keyEntries;
    private final KeyStore keyStore;

    public void addKey(StsKeyEntry keyEntry) {
        try {
            KeyStoreService.addToKeyStore(keyStore, keyEntry.getKeyEntry());
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }

        this.keyEntries.put(keyEntry.getAlias(), keyEntry);
    }

    public void removeKey(String keyAlias) {
        keyEntries.remove(keyAlias);

        try {
            keyStore.deleteEntry(keyAlias);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }
}
