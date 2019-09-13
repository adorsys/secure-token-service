package de.adorsys.sts.tests;

import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * Listener that drops data in H2 before and after test class.
 */
public class CleanupDbBeforeAfterClassListener extends AbstractTestExecutionListener {

    private static final int ORDER = 3000;

    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    public void beforeTestClass(TestContext testContext) {
        dropAll(testContext);
    }

    @Override
    public void afterTestClass(TestContext testContext) {
        dropAll(testContext);
    }

    private void dropAll(TestContext testContext) {
        ApplicationContext appContext = testContext.getApplicationContext();
        JdbcOperations jdbcOper = appContext.getBean(JdbcOperations.class);
        jdbcOper.update("DROP ALL OBJECTS DELETE FILES");
    }
}
