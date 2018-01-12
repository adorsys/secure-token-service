package de.adorsys.sts.keymanagement.service;

import com.nitorcreations.junit.runners.NestedRunner;
import de.adorsys.sts.CollectionHelpers;
import de.adorsys.sts.keymanagement.model.StsKeyEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

@RunWith(NestedRunner.class)
public class KeyStoreFilterTest {

    static final LocalDateTime FIXED_DATE_TIME = LocalDateTime.of(2018, 1, 10, 10, 10);
    static final ZoneId FIXED_ZONE_ID = ZoneId.of("UTC");
    public static final int VALIDITY_DAYS = 5;
    public static final int LEGACY_DAYS = 2;

    KeyStoreFilter keyStoreFilter;

    Clock clock;

    Map<String, StsKeyEntry> keyEntries;

    static final String KEY_ONE_ALIAS = "key one";

    @Mock
    StsKeyEntry keyOneAttributes;

    static final Long DAY_IN_MILLIS = 24 * 60 * 60 * 1000L;

    Long keyValidityInterval;

    Long keyLegacyInterval;

    List<String> keyAliases;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        clock = Clock.fixed(FIXED_DATE_TIME.atZone(FIXED_ZONE_ID).toInstant(), FIXED_ZONE_ID);

        keyValidityInterval = VALIDITY_DAYS * DAY_IN_MILLIS;
        keyLegacyInterval = LEGACY_DAYS * DAY_IN_MILLIS;

        when(keyOneAttributes.getAlias()).thenReturn(KEY_ONE_ALIAS);
        when(keyOneAttributes.getValidityInterval()).thenReturn(keyValidityInterval);
        when(keyOneAttributes.getLegacyInterval()).thenReturn(keyLegacyInterval);

        keyEntries = new HashMap<>();
        keyEntries.put(KEY_ONE_ALIAS, keyOneAttributes);

        keyAliases = CollectionHelpers.asList(KEY_ONE_ALIAS);

        keyStoreFilter = new KeyStoreFilter(clock);
    }

    public class WhenKeyIsValid {

        private List<String> filteredPrivateKeys;
        private List<String> filteredPublicKeys;

        @Before
        public void setup() throws Exception {
            when(keyOneAttributes.getCreatedAt()).thenReturn(FIXED_DATE_TIME.minusDays(LEGACY_DAYS).minusSeconds(1).atZone(FIXED_ZONE_ID));

            filteredPrivateKeys = keyStoreFilter.filterLegacy(CollectionHelpers.asList(keyOneAttributes));
            filteredPublicKeys = keyStoreFilter.filterValid(CollectionHelpers.asList(keyOneAttributes));
        }

        @Test
        public void shouldReturnPrivateKey() throws Exception {
            assertThat(filteredPrivateKeys, hasSize(1));
            assertThat(filteredPrivateKeys.get(0), is(equalTo(KEY_ONE_ALIAS)));
        }

        @Test
        public void shouldReturnPublicKey() throws Exception {
            assertThat(filteredPublicKeys, hasSize(1));
            assertThat(filteredPublicKeys.get(0), is(equalTo(KEY_ONE_ALIAS)));
        }
    }

    public class WhenKeyIsLegacy {

        private List<String> filteredPrivateKeys;
        private List<String> filteredPublicKeys;

        @Before
        public void setup() throws Exception {
            when(keyOneAttributes.getCreatedAt()).thenReturn(FIXED_DATE_TIME.minusDays(VALIDITY_DAYS).minusSeconds(1).atZone(FIXED_ZONE_ID));

            filteredPrivateKeys = keyStoreFilter.filterLegacy(CollectionHelpers.asList(keyOneAttributes));
            filteredPublicKeys = keyStoreFilter.filterValid(CollectionHelpers.asList(keyOneAttributes));
        }

        @Test
        public void shouldReturnPrivateKey() throws Exception {
            assertThat(filteredPrivateKeys, hasSize(1));
            assertThat(filteredPrivateKeys.get(0), is(equalTo(KEY_ONE_ALIAS)));
        }

        @Test
        public void shouldReturnPublicKey() throws Exception {
            assertThat(filteredPublicKeys, hasSize(0));
        }
    }

    public class WhenKeyIsExpired {

        private List<String> filteredPrivateKeys;
        private List<String> filteredPublicKeys;

        @Before
        public void setup() throws Exception {
            when(keyOneAttributes.getCreatedAt()).thenReturn(FIXED_DATE_TIME.minusDays(VALIDITY_DAYS).minusDays(LEGACY_DAYS).atZone(FIXED_ZONE_ID));

            filteredPrivateKeys = keyStoreFilter.filterLegacy(CollectionHelpers.asList(keyOneAttributes));
            filteredPublicKeys = keyStoreFilter.filterValid(CollectionHelpers.asList(keyOneAttributes));
        }

        @Test
        public void shouldReturnPrivateKey() throws Exception {
            assertThat(filteredPrivateKeys, hasSize(0));
        }

        @Test
        public void shouldReturnPublicKey() throws Exception {
            assertThat(filteredPublicKeys, hasSize(0));
        }
    }
}