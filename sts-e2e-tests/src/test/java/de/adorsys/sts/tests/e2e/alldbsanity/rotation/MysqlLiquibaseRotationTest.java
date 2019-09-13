package de.adorsys.sts.tests.e2e.alldbsanity.rotation;

import de.adorsys.sts.tests.e2e.alldbsanity.BaseJdbcDbTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = {"jpa", "liquibase", "mysql", "test-db-mysql"})
class MysqlLiquibaseRotationTest extends BaseJdbcDbTest {

    @Test
    void testMigratesOk() {
    }
}
