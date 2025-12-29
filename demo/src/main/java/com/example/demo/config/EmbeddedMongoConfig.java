package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

@Configuration
@Profile("embedded")
public class EmbeddedMongoConfig {

    // Use new logical database name for embedded profile to match external config
    private static final String DATABASE = "steel_item1";

    @Bean(destroyMethod = "shutdown")
    public MongoServer mongoServer() {
        return new MongoServer(new MemoryBackend());
    }

    @Bean
    public MongoClient mongoClient(MongoServer server) {
        server.bind("localhost", 27018);
        return MongoClients.create("mongodb://localhost:27018");
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient client) {
        return new MongoTemplate(client, DATABASE);
    }
}
