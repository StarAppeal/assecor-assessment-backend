package de.assecor.persons;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration;
import org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration;

// will be included only if 'mongo' profile is active
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, DataMongoAutoConfiguration.class})
public class PersonsApplication {

  public static void main(String[] args) {
    SpringApplication.run(PersonsApplication.class, args);
  }
}
