package com.friday.server.config;

import com.friday.common.netty.UidChannelManager;
import com.friday.common.netty.impl.UidChannelManagerImpl;
import com.friday.common.redis.ConversationRedisServer;
import com.friday.common.redis.UserInfoRedisService;
import com.friday.common.redis.UserServerRedisService;
import com.friday.common.redis.impl.ConversationRedisServerImpl;
import com.friday.common.redis.impl.UserInfoRedisServiceImpl;
import com.friday.common.redis.impl.UserServerRedisServiceImpl;
import com.friday.common.utils.SnowFlake;
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
 * @Date: 2020-05-12:17:12
 */
@Configuration
public class BeanConfig {

    @Value("${spring.nodeId}")
    private long nodeId;

    @Autowired
    private ZKConfiguration zkConfiguration;

    @Bean
    public ZkClient buildZKClient() {
        return new ZkClient(zkConfiguration.getZkAddress(), zkConfiguration.getZkConnectTimeOut());
    }

    @Bean
    public UidChannelManager uidChannelManager() {
        return new UidChannelManagerImpl();
    }

    @Bean
    public UserInfoRedisService userInfoRedisService() {
        return new UserInfoRedisServiceImpl();
    }

    @Bean
    public ConversationRedisServer conversationRedisServer() {
        return new ConversationRedisServerImpl();
    }

    @Bean
    public UserServerRedisService userServerRedisService() {
        return new UserServerRedisServiceImpl();
    }
    @Bean
    public SnowFlake snowFlake() {
        return new SnowFlake(1, nodeId);
    }
}
