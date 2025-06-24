package com.showcase.application.simplerestclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class SimpleRestClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleRestClientApplication.class, args);
    }
}
