package de.assecor.persons.config;

import org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration;
import org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@Profile("mongo")
@Import({
        MongoAutoConfiguration.class,
        DataMongoAutoConfiguration.class
})
@EnableMongoRepositories(basePackages = "de.assecor.persons.repository")
public class MongoProfileConfig {}
