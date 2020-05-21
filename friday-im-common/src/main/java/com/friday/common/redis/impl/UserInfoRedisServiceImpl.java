package com.friday.common.redis.impl;

import com.friday.common.bean.im.ServerInfo;
import com.friday.common.bean.resVo.LoginResVo;
import com.friday.common.constant.Constants;
import com.friday.common.redis.UserInfoRedisService;
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
public class UserInfoRedisServiceImpl implements UserInfoRedisService {

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
