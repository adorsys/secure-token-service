package de.adorsys.sts.tests.config;

import lombok.Synchronized;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Configuration
public class WithControllableClock {

    private static final Instant BASE_TIME = Instant.now();

    @Bean
    @Primary
    Clock clock() {
        return new ClockTestable();
    }

    public static class ClockTestable extends Clock {

        private Instant instant = BASE_TIME;

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        @Synchronized
        public Instant instant() {
            return instant;
        }

        @Synchronized
        public void setInstant(Instant instant) {
            this.instant = instant;
        }
    }
}
