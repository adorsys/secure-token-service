package de.adorsys.sts.tests;

import de.adorsys.sts.common.tests.BaseMockitoTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Endpoint test that assumes we use H2 as backing storage.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties="spring.main.banner-mode=off", webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class BaseEndpointTest extends BaseMockitoTest {

    @Autowired
    protected MockMvc mvc;
}
