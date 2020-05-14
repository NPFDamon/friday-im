package com.friday.route.redis.impl;

import com.friday.route.redis.RedisService;
import com.friday.server.bean.im.ServerInfo;
import com.friday.server.bean.resVo.LoginResVo;
import com.friday.server.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Copyright (C),Damon
 *
 * @Description: impl
 * @Author: Damon(npf)
 * @Date: 2020-05-13:14:37
 */
@Component
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void storeUserLoginInfo(LoginResVo loginResVo) {
        redisTemplate.opsForSet().add(Constants.USER_TOKEN, loginResVo.getToken());
    }

    @Override
    public void offLine(String token) {
        redisTemplate.opsForSet().remove(Constants.USER_TOKEN, token);
    }

    @Override
    public void storeIMServerInfo(String uid, ServerInfo serverInfo) {
        redisTemplate.opsForValue().set(uid, serverInfo);
    }
}
