package com.showcase.application.simplerestclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SimpleRestClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleRestClientApplication.class, args);
    }
}
