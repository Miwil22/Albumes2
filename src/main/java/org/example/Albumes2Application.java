package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class Albumes2Application {

    public static void main(String[] args) {
        SpringApplication.run(Albumes2Application.class, args);
    }
}