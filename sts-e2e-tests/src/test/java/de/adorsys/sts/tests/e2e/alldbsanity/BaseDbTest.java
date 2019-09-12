package de.adorsys.sts.tests.e2e.alldbsanity;

import de.adorsys.sts.persistence.jpa.config.EnableJpaPersistence;
import de.adorsys.sts.tests.BaseSpringTest;
import de.adorsys.sts.tests.config.WithRotation;
import org.springframework.test.context.ContextConfiguration;

@EnableJpaPersistence
@ContextConfiguration(classes = WithRotation.class)
public abstract class BaseDbTest extends BaseSpringTest {
}
