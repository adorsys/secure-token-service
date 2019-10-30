package de.adorsys.sts.tests.e2e.alldbsanity.rdbms.oldcompat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.adorsys.sts.keymanagement.KeyStoreInitializationRunner;
import de.adorsys.sts.keymanagement.model.KeyState;
import de.adorsys.sts.keymanagement.model.StsKeyEntry;
import de.adorsys.sts.keymanagement.model.StsKeyStore;
import de.adorsys.sts.persistence.jpa.DatabaseKeyStoreRepository;
import de.adorsys.sts.tests.KeyRotationContext;
import de.adorsys.sts.tests.Resource;
import de.adorsys.sts.tests.config.WithControllableClock;
import de.adorsys.sts.tests.e2e.alldbsanity.rdbms.BaseJdbcDbTest;
import de.adorsys.sts.tests.e2e.testcomponents.PopRotationValidator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static org.assertj.core.api.Assertions.assertThat;

@KeyRotationContext
@EnableAutoConfiguration(exclude = {EmbeddedMongoAutoConfiguration.class, MongoAutoConfiguration.class})
@Sql(scripts = {"classpath:fixture/old-compat/key_store.sql", "classpath:fixture/old-compat/key_entry.sql"})
@ActiveProfiles(profiles = {"jpa", "flyway", "mysql", "test-db-mysql"})
class MysqlFlywayOldDbCompatTest extends BaseJdbcDbTest {

    @MockBean
    private KeyStoreInitializationRunner runner;

    @Autowired
    private WithControllableClock.ClockTestable clock;

    @Autowired
    private PopRotationValidator validator;

    @Autowired
    private DatabaseKeyStoreRepository keyStoreRepository;

    private ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(FAIL_ON_EMPTY_BEANS)
            .disable(WRITE_DATES_AS_TIMESTAMPS);

    @Test
    @SneakyThrows
    void testMigratesAndPopRotates() {
        clock.setInstant(Instant.parse("2019-10-29T16:30:41Z"));

        StsKeyStore oldKeyStore = keyStoreRepository.load();
        JSONAssert.assertEquals(
                Resource.read("fixture/old-compat/old_entries.json"),
                mapper.writeValueAsString(oldKeyStore.getEntries()),
                true
        );

        validator.testPopRotates();

        StsKeyStore newKeyStore = keyStoreRepository.load();
        // Old valid keys are removed
        assertThat(newKeyStore.getEntries()).doesNotContainKeys(
                "sts-secret-server-dev-GC6D4",
                "sts-secret-server-dev-ba0be913-e46b-416b-b9ba-b8c083fddec8",
                "sts-secret-server-dev-K5EFK"
        );
        // Replenished
        assertThat(newKeyStore.getEntries().values().stream().filter(it -> it.getState() == KeyState.CREATED))
                .hasSize(3);
        assertThat(newKeyStore.getEntries().values().stream().filter(it -> it.getState() == KeyState.VALID))
                .hasSize(3);
        // Validate that old entries are in `Valid` state now
        JSONAssert.assertEquals(
                Resource.read("fixture/old-compat/after_rotation_kept.json"),
                mapper.writeValueAsString(newKeyStore.getEntries()),
                false
        );
    }
}
