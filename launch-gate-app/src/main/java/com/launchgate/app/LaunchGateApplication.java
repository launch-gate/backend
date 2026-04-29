package com.launchgate.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.launchgate")
@EntityScan("com.launchgate")
@EnableJpaRepositories("com.launchgate")
public class LaunchGateApplication {

    public static void main(String[] args) {
        SpringApplication.run(LaunchGateApplication.class, args);
    }
}
