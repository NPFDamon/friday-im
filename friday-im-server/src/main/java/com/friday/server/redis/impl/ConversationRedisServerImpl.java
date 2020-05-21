package com.friday.server.redis.impl;

import com.friday.common.constant.Constants;
import com.friday.common.redis.ConversationRedisServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-17:11:32
 */
@Component
public class ConversationRedisServerImpl implements ConversationRedisServer {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void saveUserClientId(String uid, String clientId) {
        redisTemplate.boundZSetOps(Constants.USER_CLIENT_INFO + uid).removeRangeByScore(0, System.currentTimeMillis() - (1000 * 60 * 5));
        redisTemplate.boundZSetOps(Constants.USER_CLIENT_INFO + uid).add(clientId, System.currentTimeMillis());
    }

    @Override
    public boolean isUserCidExit(String uid, String clientId) {
        return redisTemplate.boundZSetOps(Constants.USER_CLIENT_INFO + uid).rank(clientId) == null;
    }
}
