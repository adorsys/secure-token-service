package de.adorsys.sts.tests.e2e.alldbsanity;

import de.adorsys.sts.persistence.jpa.config.EnableJpaPersistence;
import de.adorsys.sts.tests.BaseSpringTest;
import de.adorsys.sts.tests.config.WithRotation;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.test.context.ContextConfiguration;

/**
 * Ensures that after each test method there is an empty {@code sts} schema;
 */
@EnableJpaPersistence
@ContextConfiguration(classes = WithRotation.class)
public abstract class BaseJdbcDbTest extends BaseSpringTest {

    @Autowired
    private Environment env;

    @Autowired
    private JdbcOperations jdbcOper;

    @AfterEach
    void destroyAndCreateEmptySchema() {
        if (env.acceptsProfiles(Profiles.of("postgres"))) {
            jdbcOper.update("DROP SCHEMA IF EXISTS sts CASCADE");
        } else {
            jdbcOper.update("DROP SCHEMA IF EXISTS sts");
        }

        jdbcOper.update("CREATE SCHEMA sts");
    }
}
