package de.adorsys.sts.keymanagement.model;

import com.google.common.collect.ImmutableMap;
import de.adorsys.keymanagement.api.types.ResultCollection;
import de.adorsys.keymanagement.api.types.entity.KeyEntry;
import de.adorsys.keymanagement.api.view.EntryView;
import de.adorsys.keymanagement.api.view.QueryResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.security.KeyStore;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Builder
@AllArgsConstructor
public class StsKeyStore {

    @NonNull
    private final KeyStore keyStore;

    @Getter
    @NonNull
    private final EntryView<?> view;

    @Getter
    private ZonedDateTime lastUpdate;

    /**
     * @return Read only representation of all entries in form of `alias` - `key entry`.
     */
    public Map<String, StsKeyEntry> getEntries() {
        ResultCollection<KeyEntry> entries = view.all();
        Map<String, StsKeyEntry> result = new HashMap<>();
        for (KeyEntry entry : entries) {
            result.put(entry.getAlias(), new ImmutableStsKeyEntry((StsKeyEntry) entry.getMeta()));
        }

        return ImmutableMap.copyOf(result);
    }

    /**
     * @return Underlying KeyStore object, one should not attempt to modify it - use {@link StsKeyStore#getView()}
     * to modify data.
     */
    public UnmodifyableKeystore getKeyStoreCopy() {
        return new UnmodifyableKeystore(keyStore);
    }

    public void setLastUpdate(ZonedDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
