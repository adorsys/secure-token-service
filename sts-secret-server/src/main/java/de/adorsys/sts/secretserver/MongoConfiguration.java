package de.adorsys.sts.secretserver;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import de.adorsys.sts.persistence.mongo.config.EnableMongoPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

@Configuration
@EnableMongoPersistence
@Profile({"mongo"})
public class MongoConfiguration extends AbstractMongoConfiguration {

    @Value("${spring.data.mongodb.database:sts}")
    private String databaseName;

    @Value("${spring.data.mongodb.uri:mongodb://localhost/sts}")
    private String uri;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    public MongoClient mongoClient() {
        return new MongoClient(new MongoClientURI(uri));
    }
}
