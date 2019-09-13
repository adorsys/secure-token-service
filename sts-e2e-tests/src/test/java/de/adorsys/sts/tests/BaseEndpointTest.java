package de.adorsys.sts.tests;

import de.adorsys.sts.common.tests.BaseMockitoTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class BaseEndpointTest extends BaseMockitoTest {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    private JdbcOperations jdbcOper;

    @AfterEach
    void destroyAndCreateEmptySchema() {
        jdbcOper.update("DROP ALL OBJECTS DELETE FILES");
        jdbcOper.update("CREATE SCHEMA sts");
    }
}
