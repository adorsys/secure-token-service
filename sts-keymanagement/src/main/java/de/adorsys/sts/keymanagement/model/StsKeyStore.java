package de.adorsys.sts.keymanagement.model;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.time.LocalDateTime;
import java.util.Map;

import lombok.Setter;
import org.adorsys.jkeygen.keystore.KeyStoreService;

import lombok.Builder;
import lombok.Getter;

@Getter
@Setter
@Builder
public class StsKeyStore {

    private LocalDateTime lastChangeDate;
    private final Map<String, StsKeyEntry> keyEntries;
    private final KeyStore keyStore;

    public void addKey(StsKeyEntry keyEntry) {
    	KeyStoreService.addToKeyStore(keyStore, keyEntry.getKeyEntry());

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
