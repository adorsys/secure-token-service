package de.adorsys.sts.tests.e2e;

import de.adorsys.sts.persistence.jpa.config.EnableJpaPersistence;
import de.adorsys.sts.tests.BaseEndpointTest;
import de.adorsys.sts.tests.JpaPersistenceAutoConfiguration;
import de.adorsys.sts.tests.config.WithControllableClock;
import de.adorsys.sts.tests.config.WithPopConfig;
import de.adorsys.sts.tests.config.WithRotation;
import de.adorsys.sts.tests.config.WithoutWebSecurityConfig;
import de.adorsys.sts.tests.e2e.testcomponents.PopRotationValidator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@JpaPersistenceAutoConfiguration
@ContextConfiguration(classes = {
        WithPopConfig.class,
        WithoutWebSecurityConfig.class,
        WithControllableClock.class,
        WithRotation.class,
        PopRotationValidator.class
})
class PopControllerRotationJpaTest extends BaseEndpointTest {

    @Autowired
    private PopRotationValidator validator;

    @Test
    @SneakyThrows
    void testPopRotates() {
        validator.testPopRotates();
    }
}
