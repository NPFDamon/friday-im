package com.friday.route.service.impl;

import com.friday.route.redis.RedisService;
import com.friday.route.service.AccountService;
import com.friday.server.bean.reqVo.UserReqVo;
import com.friday.server.bean.resVo.LoginResVo;
import com.friday.server.bean.token.Token;
import com.friday.server.enums.LoginStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Copyright (C),Damon
 *
 * @Description: impl
 * @Author: Damon(npf)
 * @Date: 2020-05-13:14:37
 */
public class AccountServiceImpl implements AccountService {

    @Value("123456")
    private String uid;
    @Value("test123456")
    private String secret;

    @Autowired
    private RedisService redisService;


    @Override
    public LoginResVo login(UserReqVo userReqVo) {
        LoginResVo resVo = new LoginResVo();
        if (userReqVo.getUid().equals(uid) && userReqVo.getSecret().equals(secret)) {
            resVo.setLoginStatus(LoginStatusEnum.SUCCESS);
        } else {
            resVo.setLoginStatus(LoginStatusEnum.ACCOUNT_NOT_MATCH);
        }
        resVo.setToken(new Token(uid, secret).getToken(secret));
        redisService.storeUserLoginInfo(resVo);
        // 获取路由信息  绑定账号与路由关系
//        redisService.storeIMServerInfo(uid,);
        return resVo;
    }

    @Override
    public void offLine(Long uid) {
        redisService.offLine(toString());
    }
}
