package com.friday.route.conf;

import com.friday.common.redis.ConversationRedisServer;
import com.friday.common.redis.UserInfoRedisService;
import com.friday.common.redis.UserServerRedisService;
import com.friday.common.redis.impl.ConversationRedisServerImpl;
import com.friday.common.redis.impl.UserInfoRedisServiceImpl;
import com.friday.common.redis.impl.UserServerRedisServiceImpl;
import com.friday.common.utils.SnowFlake;
import com.friday.route.lb.ServerRouteLoadBalanceHandler;
import com.friday.common.exception.BizException;
import com.friday.common.netty.ServerChannelManager;
import com.friday.common.netty.UidChannelManager;
import com.friday.common.netty.impl.ServerChannelManagerImpl;
import com.friday.common.netty.impl.UidChannelManagerImpl;
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
    public ServerChannelManager serverChannelManager() {
        return new ServerChannelManagerImpl();
    }

    @Bean
    public UidChannelManager uidChannelManager() {
        return new UidChannelManagerImpl();
    }

    @Bean
    public UserServerRedisService userServerRedisService() {
        return new UserServerRedisServiceImpl();
    }

    @Bean
    public UserInfoRedisService uerInfoRedisService() {
        return new UserInfoRedisServiceImpl();
    }

    @Bean
    public SnowFlake snowFlake() {
        return new SnowFlake(1, 1);
    }

    @Bean
    public ConversationRedisServer conversationRedisServer() {
        return new ConversationRedisServerImpl();
    }
}
