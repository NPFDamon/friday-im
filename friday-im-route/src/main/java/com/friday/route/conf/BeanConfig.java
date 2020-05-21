package com.friday.route.conf;

import com.friday.route.lb.ServerRouteLoadBalanceHandler;
import com.friday.server.exception.BizException;
import com.friday.server.netty.ServerChannelManager;
import com.friday.server.netty.UidChannelManager;
import com.friday.server.netty.impl.ServerChannelManagerImpl;
import com.friday.server.netty.impl.UidChannelManagerImpl;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@Slf4j
public class BeanConfig {

    @Autowired
    private ZKConfiguration zkConfiguration;

    @Value("${app.router.method}")
    private String lb;

    @Bean
    public ZkClient buildZKClient() {
        return new ZkClient(zkConfiguration.getZkAddress(), zkConfiguration.getZkConnectTimeOut());
    }

    @Bean
    public LoadingCache<String, String> buildCache() {
        return CacheBuilder.newBuilder().build(new CacheLoader<String, String>() {
            @Override
            public String load(String s) throws Exception {
                return null;
            }
        });
    }

    @Bean
    public ServerRouteLoadBalanceHandler choseLBHandler() {
        try {
            return (ServerRouteLoadBalanceHandler) Class.forName(lb).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            log.error("LB Class load fail");
            throw new BizException("Server Load Balance Is Not Set Or Server Load Balance Set Wrong ...");
        }
    }

    @Bean
    public ServerChannelManager getServerChannelManager() {
        return new ServerChannelManagerImpl();
    }

    @Bean
    public UidChannelManager uidChannelManager() {
        return new UidChannelManagerImpl();
    }
}
