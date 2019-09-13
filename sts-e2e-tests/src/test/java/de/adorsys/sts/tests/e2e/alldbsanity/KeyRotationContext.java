package de.adorsys.sts.tests.e2e.alldbsanity;

import de.adorsys.sts.tests.config.WithControllableClock;
import de.adorsys.sts.tests.config.WithPopConfig;
import de.adorsys.sts.tests.config.WithRotation;
import de.adorsys.sts.tests.config.WithoutWebSecurityConfig;
import de.adorsys.sts.tests.e2e.testcomponents.PopRotationValidator;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration(classes = {
        WithPopConfig.class,
        WithoutWebSecurityConfig.class,
        WithControllableClock.class,
        WithRotation.class,
        PopRotationValidator.class
})
@Retention(RetentionPolicy.RUNTIME)
public @interface KeyRotationContext {
}
