package de.adorsys.sts.keymanagement.model;

import lombok.Builder;
import lombok.Getter;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.time.ZonedDateTime;
import java.util.Map;

@Getter
@Builder
public class StsKeyStore {

    private final Map<String, StsKeyEntry> keyEntries;
    private final KeyStore keyStore;
    private ZonedDateTime lastUpdate;

    public void addKey(StsKeyEntry keyEntry) {
    	// FIXME-cleanup KeyStoreService.addToKeyStore(keyStore, keyEntry.getKeyEntry());

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

    public void setLastUpdate(ZonedDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
