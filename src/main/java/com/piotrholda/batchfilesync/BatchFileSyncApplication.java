package com.piotrholda.batchfilesync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
class BatchFileSyncApplication {

        public static void main(String[] args) {
            SpringApplication.run(BatchFileSyncApplication.class, args);
        }
}
