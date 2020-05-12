package com.friday.server.config;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Copyright (C),Damon
 *
 * @Description: bean config
 * @Author: Damon(npf)
 * @Date: 2020-05-12:17:12
 */
@Configuration
public class BeanConfig {

    @Autowired
    private ZKConfiguration zkConfiguration;

    @Bean
    public ZkClient buildZKClient() {
        return new ZkClient(zkConfiguration.getZkAddress(), zkConfiguration.getZkConnectTimeOut());
    }
}
