package de.adorsys.sts.keymanagement.service;

import de.adorsys.sts.keymanagement.model.StsKeyEntry;

import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class KeyStoreFilter {

    private final Clock clock;

    public KeyStoreFilter(
            Clock clock
    ) {
        this.clock = clock;
    }

    public List<String> filterLegacy(Collection<StsKeyEntry> keyAliases) {
        return keyAliases.stream()
                .filter(IsLegacy)
                .map(StsKeyEntry::getAlias)
                .collect(Collectors.toList());
    }

    public List<String> filterValid(Collection<StsKeyEntry> keyEntries) {
        return keyEntries.stream()
                .filter(IsValid)
                .map(StsKeyEntry::getAlias)
                .collect(Collectors.toList());
    }

    public boolean isLegacy(StsKeyEntry keyEntry) {
        return IsLegacy.test(keyEntry);
    }

    public boolean isValid(StsKeyEntry keyEntry) {
        return IsValid.test(keyEntry);
    }

    public boolean isInvalid(StsKeyEntry keyEntry) {
        return !IsValid.test(keyEntry) && !IsLegacy.test(keyEntry);
    }

    private final Predicate<StsKeyEntry> IsValid = new Predicate<StsKeyEntry>() {
        @Override
        public boolean test(StsKeyEntry attributes) {
            ZonedDateTime createdAt = attributes.getCreatedAt();
            Long validityInterval = attributes.getValidityInterval();

            Instant invalidAt = createdAt.toInstant().plusMillis(validityInterval);

            return clock.instant().isBefore(invalidAt);
        }
    };

    private final Predicate<StsKeyEntry> IsLegacy = new Predicate<StsKeyEntry>() {
        @Override
        public boolean test(StsKeyEntry keyEntry) {
            ZonedDateTime createdAt = keyEntry.getCreatedAt();
            Long validityInterval = keyEntry.getValidityInterval();
            Long legacyInterval = keyEntry.getLegacyInterval();

            Instant invalidAt = createdAt.toInstant().plusMillis(validityInterval).plusMillis(legacyInterval);
            Instant legacySince = createdAt.toInstant().plusMillis(legacyInterval);

            Instant clockInstant = clock.instant();

            return clockInstant.isAfter(legacySince) && clockInstant.isBefore(invalidAt);
        }
    };
}
