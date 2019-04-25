package de.adorsys.sts.example.config;

import de.adorsys.sts.persistence.mongo.config.EnableMongoPersistence;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableMongoPersistence
@Profile({"mongo"})
public class MongoConfiguration {
}
