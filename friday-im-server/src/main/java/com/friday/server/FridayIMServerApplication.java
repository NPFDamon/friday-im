package com.friday.server;

import com.friday.server.server.FridayIMServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class FridayIMServerApplication implements CommandLineRunner {

    @Autowired
    private FridayIMServer fridayIMServer;

    public static void main(String[] args) {
        SpringApplication.run(FridayIMServerApplication.class, args);
        log.info("Friday IM Server Application is run ^_^");
    }

    @Override
    public void run(String... args) {
        fridayIMServer.start();
    }
}
