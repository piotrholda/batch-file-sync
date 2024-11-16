package com.piotrholda.batchfilesync;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
class ApplicationConfig {

    private final Synchronization synchronization;

    ApplicationConfig(Synchronization synchronization) {
        this.synchronization = synchronization;
    }

    @Profile("!test")
    @Bean
    ApplicationRunner applicationRunner() {
        return args -> synchronization.run();
    }
}
