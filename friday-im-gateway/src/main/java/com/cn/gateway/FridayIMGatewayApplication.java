package com.cn.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Copyright (C),Damon
 *
 * @Description: application
 * @Author: Damon(npf)
 * @Date: 2020-05-13:10:23
 */
@SpringBootApplication
@Slf4j
@EnableEurekaServer
public class FridayIMGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(FridayIMGatewayApplication.class, args);
        log.info("Friday IM Gateway Application Run Successful ... ^_^");
    }
}
