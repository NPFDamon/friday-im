package com.friday.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@Slf4j
@EnableDiscoveryClient
@EnableFeignClients
public class FridayIMClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(FridayIMClientApplication.class, args);
        log.info("Test Client is run ^_^!");
    }
}
