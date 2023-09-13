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
import java.time.temporal.ChronoUnit;

@Configuration
public class ExecutionLockConfiguration {

    /**
     * Expects database table to have format database.tableName
     */
    public static final String DEFAULT_JPA_TABLE_KEY = "${de.adorsys.sts.lock.table:sts.sts_lock}";

    /**
     * Expects mongo collection to have format database.collection
     */
    public static final String DEFAULT_MONGO_COLLECTION_KEY = "${de.adorsys.sts.lock.collection:sts.sts_lock}";

    @Value("${de.adorsys.sts.lock.expiry:600s}")
    private Duration expiry;

    @Bean
    LockClient lockClient(LockProvider lockProvider) {
        LockingTaskExecutor executor = new DefaultLockingTaskExecutor(lockProvider);
        return (rotationLockName, toExecute) ->
                executor.executeWithLock(toExecute, new LockConfiguration(Instant.now(), rotationLockName, expiry, Duration.of(5, ChronoUnit.MILLIS)));
    }
}
