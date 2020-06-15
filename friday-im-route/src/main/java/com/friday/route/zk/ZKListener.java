package com.friday.route.zk;

import com.friday.route.conf.ZKConfiguration;
import com.friday.route.util.SpringBeanFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * Copyright (C),Damon
 *
 * @Description: zk ZKListener
 * @Author: Damon(npf)
 * @Date: 2020-05-13:11:04
 */
@Slf4j
public class ZKListener implements Runnable {

    private final ZK zk;

    private final ZKConfiguration zkConfiguration;


    public ZKListener() {
        zk = SpringBeanFactory.getBean(ZK.class);
        zkConfiguration = SpringBeanFactory.getBean(ZKConfiguration.class);
    }

    @Override
    public void run() {
        zk.subscribe(zkConfiguration.getZkRoot());
    }
}
