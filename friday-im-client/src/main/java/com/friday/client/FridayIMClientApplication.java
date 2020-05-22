package com.friday.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class FridayIMClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(FridayIMClientApplication.class, args);
        log.info("Test Client is run ^_^!");
    }
}
