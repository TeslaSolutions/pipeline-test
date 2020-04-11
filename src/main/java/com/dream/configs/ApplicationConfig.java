package com.dream.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class ApplicationConfig {

    @Bean
    public ObjectSSEEmitter dreamSSEEmitter() {
        ObjectSSEEmitter dreamSSEEmitter = new ObjectSSEEmitter();
        return dreamSSEEmitter;
    }

    @Bean
    public ObjectSSEEmitter commentSSEEmitter() {
        ObjectSSEEmitter commentSSEEmitter = new ObjectSSEEmitter();
        return commentSSEEmitter;
    }

}
