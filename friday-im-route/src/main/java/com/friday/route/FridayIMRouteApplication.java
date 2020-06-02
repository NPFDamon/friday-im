package com.friday.route;

import com.friday.route.zk.ZKListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Copyright (C),Damon
 *
 * @Description: application
 * @Author: Damon(npf)
 * @Date: 2020-05-13:10:45
 */

@SpringBootApplication
@Slf4j
@EnableDiscoveryClient
public class FridayIMRouteApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(FridayIMRouteApplication.class, args);
        log.info("Friday IM Route Run Successful ... ^_^");
    }

    @Override
    public void run(String... args) throws Exception {
        Thread thread = new Thread(new ZKListener());
        thread.setName("ZK-LISTENER");
        thread.start();
    }
}
