package de.adorsys.sts.tests;

import de.adorsys.sts.common.tests.BaseMockitoTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Test that assumes we do not do REST calls focusing on pure internals
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties="spring.main.banner-mode=off", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public abstract class BaseSpringTest extends BaseMockitoTest {
}
