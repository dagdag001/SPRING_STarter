package com.example.task.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SpelDemo {
    
    // SpEL in @Value
    @Value("#{systemProperties['user.home']}")
    private String userHome;

    @Value("#{systemEnvironment['MY_REGION'] ?: 'default-region'}")
    private String region;

    public String getUserHome() {
        return userHome;
    }

    public String getRegion() {
        return region;
    }
}
