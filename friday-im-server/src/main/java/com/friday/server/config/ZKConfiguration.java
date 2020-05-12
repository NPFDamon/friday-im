package com.friday.server.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Copyright (C),Damon
 *
 * @Description: app config
 * @Author: Damon(npf)
 * @Date: 2020-05-12:16:37
 */
@Data
@Component
public class ZKConfiguration {
    @Value("${zk.root}")
    private String zkRoot;

    @Value("${zk.address}")
    private String zkAddress;

    @Value("${zk.switch}")
    private boolean zkSwitch;

//    @Value("${}")
//    private int imServerPort;
//
//    @Value("${}")
//    private String routerUrl;

    @Value("${zk.connection.timeout}")
    private int zkConnectTimeOut;
}
