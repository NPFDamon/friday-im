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

    private final ZK zk;
    private final String ip;
    private final int imServerPort;
    private final int httpPort;
    private final int tcpPort;
    private final int wsPort;

    private final ZKConfiguration zkConfiguration;

    public RegistryZK(String ip, int imServerPort, int httpPort, int tcpPort, int wsPort) {
        this.ip = ip;
        this.imServerPort = imServerPort;
        this.httpPort = httpPort;
        this.tcpPort = tcpPort;
        this.wsPort = wsPort;
        zk = SpringBeanFactory.getBean(ZK.class);
        zkConfiguration = SpringBeanFactory.getBean(ZKConfiguration.class);
    }

    @Override
    public void run() {
        //创建父节点
        zk.creatRootNode();

        if (zkConfiguration.isZkSwitch()) {
            String path = zkConfiguration.getZkRoot() + "/ip-" + ip + ":" + imServerPort + ":" + httpPort + ":" + tcpPort + ":" + wsPort;
            zk.createNode(path);
            log.info("Registry zK successful, path = [{}]", path);
        }

    }
}
