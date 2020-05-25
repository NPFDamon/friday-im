package com.friday.server.zk;

import com.friday.common.utils.JsonHelper;
import com.friday.server.config.ZKConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Copyright (C),Damon
 *
 * @Description: zk tools
 * @Author: Damon(npf)
 * @Date: 2020-05-12:16:47
 */
@Component
@Slf4j
public class ZK {

    @Autowired
    private ZkClient zkClient;

    @Autowired
    private ZKConfiguration zkConfiguration;

    @Value("${zk.root}")
    private String zkRoot;

    /**
     * 创建父及节点
     */
    public void creatRootNode() {
        boolean exit = zkClient.exists(zkConfiguration.getZkRoot());
        if (exit) {
            return;
        }
        //创建 root
        zkClient.createPersistent(zkConfiguration.getZkRoot());
    }

    /**
     * 写入制定节点
     *
     * @param path
     */
    public void createNode(String path) {
        zkClient.createEphemeral(path);
    }

    public List<String> getAllNode() {
        List<String> node = zkClient.getChildren(zkRoot);
        log.info("Get All Node Success, Node:[{}]", JsonHelper.toJsonString(node));
        return node;
    }
}
