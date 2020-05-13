package com.friday.route.conf;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Copyright (C),Damon
 *
 * @Description: bean config
 * @Author: Damon(npf)
 * @Date: 2020-05-13:10:49
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
