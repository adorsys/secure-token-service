package de.adorsys.sts.tests.e2e.alldbsanity.rdbms.rotation;

import de.adorsys.sts.tests.e2e.alldbsanity.rdbms.BaseJdbcDbTest;
import de.adorsys.sts.tests.e2e.testcomponents.PopRotationValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@KeyRotationContext
@ActiveProfiles(profiles = {"jpa", "flyway", "postgres", "test-db-postgres"})
class PostgresFlywayRotationTest extends BaseJdbcDbTest {

    @Autowired
    private PopRotationValidator validator;

    @Test
    void testMigratesOk() {
        validator.testPopRotates();
    }
}
