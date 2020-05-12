package com.friday.client;

import com.friday.client.client.FridayIMClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class FridayIMClientApplication implements CommandLineRunner {

    @Autowired
    private FridayIMClient fridayIMClient;

    public static void main(String[] args) {
        SpringApplication.run(FridayIMClientApplication.class, args);
        log.info("Test Client is run ^_^!");
    }


    @Override
    public void run(String... args) throws Exception {
        fridayIMClient.start();
    }
}
