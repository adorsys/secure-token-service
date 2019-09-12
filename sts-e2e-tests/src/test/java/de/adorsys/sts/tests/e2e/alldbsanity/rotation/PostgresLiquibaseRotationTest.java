package de.adorsys.sts.tests.e2e.alldbsanity.rotation;

import de.adorsys.sts.tests.e2e.alldbsanity.BaseDbTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = {"jpa", "liquibase", "postgres", "test-db-postgres"})
class PostgresLiquibaseRotationTest extends BaseDbTest {

    @Test
    void testMigratesOk() {
    }
}
