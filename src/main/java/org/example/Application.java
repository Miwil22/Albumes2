package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableCaching
@SpringBootApplication(scanBasePackages = {"org.example"})
@EntityScan(basePackages = {"org.example.albumes", "org.example.artistas"})
@EnableJpaRepositories(basePackages = {"org.example.albumes", "org.example.artistas"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}