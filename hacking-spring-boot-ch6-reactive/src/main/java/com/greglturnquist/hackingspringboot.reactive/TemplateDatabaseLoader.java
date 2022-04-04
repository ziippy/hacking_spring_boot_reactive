package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

@Component
public class TemplateDatabaseLoader {

    @Bean
    CommandLineRunner initialize(MongoOperations mongo) {
        return args -> {
            //mongo.remove(mongo.findAll(Item));
            mongo.save(new Item("Alf alarm clock by template", 19.99));
            mongo.save(new Item("Smurf TV tray by template", 24.99));
            mongo.save(new Item("Test", 99.99));
        };
    }
}
