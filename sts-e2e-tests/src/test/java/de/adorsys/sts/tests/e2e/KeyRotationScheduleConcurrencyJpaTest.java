package de.adorsys.sts.tests.e2e;

import de.adorsys.sts.common.lock.LockClient;
import de.adorsys.sts.keymanagement.persistence.KeyStoreRepository;
import de.adorsys.sts.keymanagement.service.KeyManagementProperties;
import de.adorsys.sts.keymanagement.service.KeyRotationService;
import de.adorsys.sts.keyrotation.KeyRotationSchedule;
import de.adorsys.sts.tests.BaseSpringTest;
import de.adorsys.sts.tests.CleanupDbBeforeAfterClass;
import de.adorsys.sts.tests.JpaPersistenceAutoConfiguration;
import de.adorsys.sts.tests.config.WithControllableClock;
import de.adorsys.sts.tests.config.WithRotation;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.time.Clock;
import java.time.Duration;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

/**
 * Tests that multiple key-rotation executions are prevented (allows clustered execution).
 */
@JpaPersistenceAutoConfiguration
@ContextConfiguration(classes = {
        KeyRotationScheduleConcurrencyJpaTest.KeyRotationScheduleTestable.class,
        WithControllableClock.class,
        WithRotation.class
})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@CleanupDbBeforeAfterClass
class KeyRotationScheduleConcurrencyJpaTest extends BaseSpringTest {

    private static final Duration RUNNING_WINDOW = Duration.ofSeconds(10);
    private static final int THREAD_COUNT = Math.max(10, Runtime.getRuntime().availableProcessors());
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(THREAD_COUNT);

    @MockBean
    @Qualifier("cached")
    private KeyStoreRepository keyStoreRepository;

    @SpyBean
    private KeyRotationScheduleTestable rotationSchedule;

    @Test
    @SneakyThrows
    void testPopRotationExecutesOnce() {
        // ensure thread-reentry won't happen because lock was released
        when(keyStoreRepository.exists()).thenAnswer(inv -> {
            doSleep();
            return true;
        });
        CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT);

        // imitate that cluster tries to rotate key
        IntStream.range(0, THREAD_COUNT)
                .forEach(it -> EXECUTOR.submit(() -> {
                    doWaitOnBarrier(barrier);
                    rotationSchedule.scheduledRotation();
                }));

        EXECUTOR.awaitTermination(RUNNING_WINDOW.getSeconds() + 1, TimeUnit.SECONDS);
        // Rotation attempt is done only once
        verify(rotationSchedule).doRotate();
    }

    @SneakyThrows
    private void doSleep() {
        Thread.sleep(RUNNING_WINDOW.toMillis());
    }

    @SneakyThrows
    private void doWaitOnBarrier(CyclicBarrier barrier) {
        barrier.await();
    }

    @Component
    @Primary
    static class KeyRotationScheduleTestable extends KeyRotationSchedule {

        public KeyRotationScheduleTestable(KeyRotationService keyRotationService,
                                           @Qualifier("cached") KeyStoreRepository keyStoreRepository,
                                           LockClient lockClient,
                                           KeyManagementProperties properties,
                                           Clock clock) {
            super(keyRotationService, keyStoreRepository, lockClient, properties, clock);
        }

        @Override
        public void doRotate() {
            super.doRotate();
        }
    }
}
