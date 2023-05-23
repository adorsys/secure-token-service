package de.adorsys.sts.tests;

import de.adorsys.sts.persistence.jpa.config.EnableJpaPersistence;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@EnableJpaPersistence
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class})
public @interface JpaPersistenceAutoConfiguration {
}
