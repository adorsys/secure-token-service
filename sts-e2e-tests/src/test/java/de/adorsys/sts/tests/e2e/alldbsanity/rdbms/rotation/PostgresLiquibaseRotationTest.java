package de.adorsys.sts.tests.e2e.alldbsanity.rdbms.rotation;

import de.adorsys.sts.tests.KeyRotationContext;
import de.adorsys.sts.tests.e2e.alldbsanity.rdbms.BaseJdbcDbTest;
import de.adorsys.sts.tests.e2e.testcomponents.PopRotationValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.ActiveProfiles;

@KeyRotationContext
@EnableAutoConfiguration
@ActiveProfiles(profiles = {"jpa", "liquibase", "postgres", "test-db-postgres"})
class PostgresLiquibaseRotationTest extends BaseJdbcDbTest {

    @Autowired
    private PopRotationValidator validator;

    @Test
    void testMigratesAndPopRotates() {
        validator.testPopRotates();
    }
}
