package de.adorsys.sts.tests.e2e.alldbsanity.object;

import de.adorsys.sts.persistence.mongo.config.EnableMongoPersistence;
import de.adorsys.sts.tests.BaseSpringTest;
import de.adorsys.sts.tests.e2e.alldbsanity.KeyRotationContext;
import de.adorsys.sts.tests.e2e.testcomponents.PopRotationValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.test.context.ActiveProfiles;

@KeyRotationContext
@EnableMongoPersistence
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
@ActiveProfiles(profiles = {"mongo"})
class MongoRotationTest extends BaseSpringTest {

    @Autowired
    private PopRotationValidator validator;

    @Test
    void testPopRotates() {
        validator.testPopRotates();
    }
}
