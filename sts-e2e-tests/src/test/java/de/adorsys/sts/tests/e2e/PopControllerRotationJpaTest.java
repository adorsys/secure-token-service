package de.adorsys.sts.tests.e2e;

import de.adorsys.sts.tests.BaseEndpointTest;
import de.adorsys.sts.tests.CleanupDbBeforeAfterClass;
import de.adorsys.sts.tests.JpaPersistenceAutoConfiguration;
import de.adorsys.sts.tests.KeyRotationContext;
import de.adorsys.sts.tests.e2e.testcomponents.PopRotationValidator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@KeyRotationContext
@JpaPersistenceAutoConfiguration
@CleanupDbBeforeAfterClass
class PopControllerRotationJpaTest extends BaseEndpointTest {

    @Autowired
    private PopRotationValidator validator;

    @Test
    @SneakyThrows
    void testPopRotates() {
        validator.testPopRotates();
    }
}
