package de.adorsys.sts.tests.config;

import de.adorsys.sts.keyrotation.EnableKeyRotation;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@EnableAutoConfiguration(exclude = MongoAutoConfiguration.class)
@EnableKeyRotation
public class WithRotation {
}
