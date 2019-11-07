package de.adorsys.sts.tests.e2e.alldbsanity.rdbms.rotation;

import de.adorsys.sts.tests.KeyRotationContext;
import de.adorsys.sts.tests.e2e.alldbsanity.rdbms.BaseJdbcDbTest;
import de.adorsys.sts.tests.e2e.testcomponents.PopRotationValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.test.context.ActiveProfiles;

@KeyRotationContext
@EnableAutoConfiguration(exclude = {EmbeddedMongoAutoConfiguration.class, MongoAutoConfiguration.class})
@ActiveProfiles(profiles = {"jpa", "flyway", "mysql", "test-db-mysql"})
class MysqlFlywayRotationTest extends BaseJdbcDbTest {

    @Autowired
    private PopRotationValidator validator;

    @Test
    void testMigratesAndPopRotates() {
        validator.testPopRotates();
    }
}
