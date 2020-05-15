package com.friday.route.redis.impl;

import com.friday.route.redis.UserServerRedisService;
import com.friday.server.bean.im.ServerInfo;
import com.friday.server.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

/**
 * Copyright (C),Damon
 *
 * @Description: impl
 * @Author: Damon(npf)
 * @Date: 2020-05-15:14:06
 */
public class UserServerRedisServiceImpl implements UserServerRedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void addUserToServer(String uid, ServerInfo serverInfo) {
        redisTemplate.boundHashOps(Constants.USER_SERVER_INFO).put(uid, serverInfo.toString());
        redisTemplate.boundSetOps(Constants.SERVER_ROUTE_INFO + serverInfo.hashCode()).add(uid);
    }

    @Override
    public void removeUserFromServer(String uid, ServerInfo serverInfo) {
        redisTemplate.boundHashOps(Constants.USER_SERVER_INFO).delete(uid);
        redisTemplate.boundSetOps(Constants.SERVER_ROUTE_INFO + serverInfo.hashCode()).remove(uid);
    }

    @Override
    public void serverOffLine(ServerInfo serverInfo) {
        Cursor cursor = redisTemplate.boundSetOps(Constants.SERVER_ROUTE_INFO + serverInfo.hashCode())
                .scan(ScanOptions.scanOptions().count(Long.MIN_VALUE).build());
        while (cursor.hasNext()) {
            String uid = (String) cursor.next();
            redisTemplate.boundHashOps(Constants.USER_SERVER_INFO).rename(uid);
        }
        redisTemplate.delete(Constants.SERVER_ROUTE_INFO + serverInfo.hashCode());
    }

    @Override
    public ServerInfo getServerInfoByUid(String uid) {
        Object o = redisTemplate.boundHashOps(Constants.USER_SERVER_INFO).get(uid);
        if (o == null) {
            return null;
        }
        return (ServerInfo) o;
    }
}
