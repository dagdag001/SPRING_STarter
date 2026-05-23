package com.example.task.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder {
    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @PostConstruct
    public void init() {
        logger.info("DataSeeder: Bean ready after properties set - Seeding initial data...");
    }

    @PreDestroy
    public void cleanup() {
        logger.info("DataSeeder: Bean about to be destroyed - Cleaning up resources...");
    }
}
