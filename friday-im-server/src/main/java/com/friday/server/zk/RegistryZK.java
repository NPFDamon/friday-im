package com.friday.server.zk;

import com.friday.server.config.ZKConfiguration;
import com.friday.server.utils.SpringBeanFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * Copyright (C),Damon
 *
 * @Description: Registry zk
 * @Author: Damon(npf)
 * @Date: 2020-05-12:16:56
 */
@Slf4j
public class RegistryZK implements Runnable {

    private ZK zk;

    private String ip;

    private int imServerPort;

    private int httpPort;

    private ZKConfiguration zkConfiguration;

    public RegistryZK(String ip, int imServerPort, int httpPort) {
        this.ip = ip;
        this.imServerPort = imServerPort;
        this.httpPort = httpPort;
        zk = SpringBeanFactory.getBean(ZK.class);
        zkConfiguration = SpringBeanFactory.getBean(ZKConfiguration.class);
    }

    @Override
    public void run() {
        //创建父节点
        zk.creatRootNode();

        if (zkConfiguration.isZkSwitch()) {
            String path = zkConfiguration.getZkRoot() + "/ip-" + ip + ":" + imServerPort + ":" + httpPort;
            zk.createNode(path);
            log.info("Registry zK successful, path = [{}]", path);
        }

    }
}
