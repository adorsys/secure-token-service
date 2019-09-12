package de.adorsys.sts.lock;

import de.adorsys.sts.common.lock.LockClient;
import net.javacrumbs.shedlock.core.DefaultLockingTaskExecutor;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.Instant;

@Configuration
public class ExecutionLockConfiguration {

    public static final String DEFAULT_TABLE_KEY = "${de.adorsys.sts.lock.table:sts.sts_lock}";

    @Value("${de.adorsys.sts.lock.expiry:600s}")
    private Duration expiry;

    @Bean
    LockClient lockClient(LockProvider lockProvider) {
        LockingTaskExecutor executor = new DefaultLockingTaskExecutor(lockProvider);
        Instant lockAtMostUntil = Instant.now().plus(expiry);

        return (rotationLockName, toExecute) ->
                executor.executeWithLock(toExecute, new LockConfiguration(rotationLockName, lockAtMostUntil));
    }
}
