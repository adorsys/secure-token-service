package de.adorsys.sts.tests.config;

import de.adorsys.sts.pop.EnablePOP;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@EnableAutoConfiguration(exclude = MongoAutoConfiguration.class)
@EnablePOP
public class WithPopConfig {
}
