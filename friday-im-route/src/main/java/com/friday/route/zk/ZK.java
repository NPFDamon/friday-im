package com.friday.route.zk;

import com.alibaba.fastjson.JSON;
import com.friday.common.bean.im.ServerInfo;
import com.friday.common.netty.ServerChannelManager;
import com.friday.common.utils.JsonHelper;
import com.friday.common.utils.ServerInfoParseUtil;
import com.friday.route.client.RouteClient;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Copyright (C),Damon
 *
 * @Description: zk
 * @Author: Damon(npf)
 * @Date: 2020-05-13:10:53
 */
@Component
@Slf4j
public class ZK {

    @Autowired
    private ZkClient zkClient;
    @Autowired
    private RouteClient routeClient;
    @Autowired
    private ServerChannelManager serverChannelManager;


    @Value("${zk.root}")
    private String zkRoot;


    public void subscribe(String path) {
        zkClient.subscribeChildChanges(path, new IZkChildListener() {
            @Override
            public void handleChildChange(String s, List<String> list) throws Exception {
                List<ServerInfo> serverInfos = ServerInfoParseUtil.getServerInfoList(list);
                log.info("Clear and update local cache parentPath=[{}],currentChildren=[{}]", path, JsonHelper.toJsonString(serverInfos));
//                serverInfos.forEach(serverInfo -> {
//                    Channel channel = routeClient.connect(serverInfo);
//                    if (channel != null) {
//                        serverChannelManager.addServerToChannel(serverInfo, channel);
//                        log.info("client connect to server:{} success!", serverInfo);
//                    }
//                });
            }
        });
    }

    public List<String> getAllNode() {
        List<String> node = zkClient.getChildren(zkRoot);
        log.info("Get All Node Success, Node:[{}]", JSON.toJSONString(node));
        return node;
    }
}
