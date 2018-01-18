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

    static final int VALIDITY_DAYS = 5;
    static final int LEGACY_DAYS = 2;

    KeyStoreFilter keyStoreFilter;

    Clock clock;

    Map<String, StsKeyEntry> keyEntries;

    static final String KEY_ALIAS = "key one";

    @Mock
    StsKeyEntry keyAttributes;

    static final Long DAY_IN_MILLIS = 24 * 60 * 60 * 1000L;

    List<String> keyAliases;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        clock = Clock.fixed(FIXED_DATE_TIME.atZone(FIXED_ZONE_ID).toInstant(), FIXED_ZONE_ID);

        when(keyAttributes.getAlias()).thenReturn(KEY_ALIAS);
        when(keyAttributes.getValidityInterval()).thenReturn(VALIDITY_DAYS * DAY_IN_MILLIS);
        when(keyAttributes.getLegacyInterval()).thenReturn(LEGACY_DAYS * DAY_IN_MILLIS);

        keyEntries = new HashMap<>();
        keyEntries.put(KEY_ALIAS, keyAttributes);

        keyAliases = CollectionHelpers.asList(KEY_ALIAS);

        keyStoreFilter = new KeyStoreFilter(clock);
    }

    public class WhenKeyIsValid {

        private List<String> filteredLegacyKeys;
        private List<String> filteredValidKeys;

        @Before
        public void setup() throws Exception {
            when(keyAttributes.getCreatedAt()).thenReturn(FIXED_DATE_TIME.minusDays(LEGACY_DAYS).minusSeconds(1).atZone(FIXED_ZONE_ID));

            filteredLegacyKeys = keyStoreFilter.filterLegacy(CollectionHelpers.asList(keyAttributes));
            filteredValidKeys = keyStoreFilter.filterValid(CollectionHelpers.asList(keyAttributes));
        }

        @Test
        public void shouldReturnLegacyKeys() throws Exception {
            assertThat(filteredLegacyKeys, hasSize(0));
        }

        @Test
        public void shouldReturnValidKey() throws Exception {
            assertThat(filteredValidKeys, hasSize(1));
            assertThat(filteredValidKeys.get(0), is(equalTo(KEY_ALIAS)));
        }
    }

    public class WhenKeyIsLegacy {

        private List<String> filteredLegacyKeys;
        private List<String> filteredValidKeys;

        @Before
        public void setup() throws Exception {
            when(keyAttributes.getCreatedAt()).thenReturn(FIXED_DATE_TIME.minusDays(VALIDITY_DAYS).minusSeconds(1).atZone(FIXED_ZONE_ID));

            filteredLegacyKeys = keyStoreFilter.filterLegacy(CollectionHelpers.asList(keyAttributes));
            filteredValidKeys = keyStoreFilter.filterValid(CollectionHelpers.asList(keyAttributes));
        }

        @Test
        public void shouldReturnLegacyKeys() throws Exception {
            assertThat(filteredLegacyKeys, hasSize(1));
            assertThat(filteredLegacyKeys.get(0), is(equalTo(KEY_ALIAS)));
        }

        @Test
        public void shouldReturnValidKey() throws Exception {
            assertThat(filteredValidKeys, hasSize(0));
        }
    }

    public class WhenKeyIsExpired {

        private List<String> filteredLegacyKeys;
        private List<String> filteredValidKeys;

        @Before
        public void setup() throws Exception {
            when(keyAttributes.getCreatedAt()).thenReturn(FIXED_DATE_TIME.minusDays(VALIDITY_DAYS).minusDays(LEGACY_DAYS).atZone(FIXED_ZONE_ID));

            filteredLegacyKeys = keyStoreFilter.filterLegacy(CollectionHelpers.asList(keyAttributes));
            filteredValidKeys = keyStoreFilter.filterValid(CollectionHelpers.asList(keyAttributes));
        }

        @Test
        public void shouldReturnLegacyKeys() throws Exception {
            assertThat(filteredLegacyKeys, hasSize(0));
        }

        @Test
        public void shouldReturnValidKey() throws Exception {
            assertThat(filteredValidKeys, hasSize(0));
        }
    }

    public class WhenValidityIntervalIsUnlimited {

        @Before
        public void setup() throws Exception {
            when(keyAttributes.getValidityInterval()).thenReturn(0L);
        }

        public class WhenKeyIsValid {

            private List<String> filteredLegacyKeys;
            private List<String> filteredValidKeys;

            @Before
            public void setup() throws Exception {
                when(keyAttributes.getCreatedAt()).thenReturn(FIXED_DATE_TIME.minusDays(LEGACY_DAYS).minusSeconds(1).atZone(FIXED_ZONE_ID));

                filteredLegacyKeys = keyStoreFilter.filterLegacy(CollectionHelpers.asList(keyAttributes));
                filteredValidKeys = keyStoreFilter.filterValid(CollectionHelpers.asList(keyAttributes));
            }

            @Test
            public void shouldReturnLegacyKeys() throws Exception {
                assertThat(filteredLegacyKeys, hasSize(0));
            }

            @Test
            public void shouldReturnValidKeys() throws Exception {
                assertThat(filteredValidKeys, hasSize(1));
                assertThat(filteredValidKeys.get(0), is(equalTo(KEY_ALIAS)));
            }
        }

        public class WhenKeyIsLegacy {

            private List<String> filteredLegacyKeys;
            private List<String> filteredValidKeys;

            @Before
            public void setup() throws Exception {
                when(keyAttributes.getCreatedAt()).thenReturn(FIXED_DATE_TIME.minusDays(VALIDITY_DAYS).minusSeconds(1).atZone(FIXED_ZONE_ID));

                filteredLegacyKeys = keyStoreFilter.filterLegacy(CollectionHelpers.asList(keyAttributes));
                filteredValidKeys = keyStoreFilter.filterValid(CollectionHelpers.asList(keyAttributes));
            }

            @Test
            public void shouldReturnLegacyKeys() throws Exception {
                assertThat(filteredLegacyKeys, hasSize(0));
            }

            @Test
            public void shouldReturnValidKey() throws Exception {
                assertThat(filteredValidKeys, hasSize(1));
                assertThat(filteredValidKeys.get(0), is(equalTo(KEY_ALIAS)));
            }
        }

        public class WhenKeyIsExpired {

            private List<String> filteredLegacyKeys;
            private List<String> filteredValidKeys;

            @Before
            public void setup() throws Exception {
                when(keyAttributes.getCreatedAt()).thenReturn(FIXED_DATE_TIME.minusDays(VALIDITY_DAYS).minusDays(LEGACY_DAYS).atZone(FIXED_ZONE_ID));

                filteredLegacyKeys = keyStoreFilter.filterLegacy(CollectionHelpers.asList(keyAttributes));
                filteredValidKeys = keyStoreFilter.filterValid(CollectionHelpers.asList(keyAttributes));
            }

            @Test
            public void shouldReturnLegacyKeys() throws Exception {
                assertThat(filteredLegacyKeys, hasSize(0));
            }

            @Test
            public void shouldReturnValidKey() throws Exception {
                assertThat(filteredValidKeys, hasSize(1));
                assertThat(filteredValidKeys.get(0), is(equalTo(KEY_ALIAS)));
            }
        }
    }

    public class WhenLegacyIntervalIsUnlimited {

        @Before
        public void setup() throws Exception {
            when(keyAttributes.getLegacyInterval()).thenReturn(0L);
        }

        public class WhenKeyIsValid {

            private List<String> filteredLegacyKeys;
            private List<String> filteredValidKeys;

            @Before
            public void setup() throws Exception {
                when(keyAttributes.getCreatedAt()).thenReturn(FIXED_DATE_TIME.minusDays(LEGACY_DAYS).minusSeconds(1).atZone(FIXED_ZONE_ID));

                filteredLegacyKeys = keyStoreFilter.filterLegacy(CollectionHelpers.asList(keyAttributes));
                filteredValidKeys = keyStoreFilter.filterValid(CollectionHelpers.asList(keyAttributes));
            }

            @Test
            public void shouldReturnLegacyKeys() throws Exception {
                assertThat(filteredLegacyKeys, hasSize(1));
                assertThat(filteredLegacyKeys.get(0), is(equalTo(KEY_ALIAS)));
            }

            @Test
            public void shouldReturnValidKeys() throws Exception {
                assertThat(filteredValidKeys, hasSize(1));
                assertThat(filteredValidKeys.get(0), is(equalTo(KEY_ALIAS)));
            }
        }

        public class WhenKeyIsLegacy {

            private List<String> filteredLegacyKeys;
            private List<String> filteredValidKeys;

            @Before
            public void setup() throws Exception {
                when(keyAttributes.getCreatedAt()).thenReturn(FIXED_DATE_TIME.minusDays(VALIDITY_DAYS).minusSeconds(1).atZone(FIXED_ZONE_ID));

                filteredLegacyKeys = keyStoreFilter.filterLegacy(CollectionHelpers.asList(keyAttributes));
                filteredValidKeys = keyStoreFilter.filterValid(CollectionHelpers.asList(keyAttributes));
            }

            @Test
            public void shouldReturnLegacyKeys() throws Exception {
                assertThat(filteredLegacyKeys, hasSize(1));
                assertThat(filteredLegacyKeys.get(0), is(equalTo(KEY_ALIAS)));
            }

            @Test
            public void shouldReturnValidKey() throws Exception {
                assertThat(filteredValidKeys, hasSize(0));
            }
        }

        public class WhenKeyIsExpired {

            private List<String> filteredLegacyKeys;
            private List<String> filteredValidKeys;

            @Before
            public void setup() throws Exception {
                when(keyAttributes.getCreatedAt()).thenReturn(FIXED_DATE_TIME.minusDays(VALIDITY_DAYS).minusDays(LEGACY_DAYS).atZone(FIXED_ZONE_ID));

                filteredLegacyKeys = keyStoreFilter.filterLegacy(CollectionHelpers.asList(keyAttributes));
                filteredValidKeys = keyStoreFilter.filterValid(CollectionHelpers.asList(keyAttributes));
            }

            @Test
            public void shouldReturnLegacyKeys() throws Exception {
                assertThat(filteredLegacyKeys, hasSize(1));
                assertThat(filteredLegacyKeys.get(0), is(equalTo(KEY_ALIAS)));
            }

            @Test
            public void shouldReturnValidKey() throws Exception {
                assertThat(filteredValidKeys, hasSize(0));
            }
        }
    }
}
