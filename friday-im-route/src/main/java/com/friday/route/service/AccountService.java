package com.friday.route.service;

import com.friday.server.bean.reqVo.UserReqVo;
import com.friday.server.bean.resVo.LoginResVo;

/**
 * Copyright (C),Damon
 *
 * @Description: user interface
 * @Author: Damon(npf)
 * @Date: 2020-05-13:14:30
 */
public interface AccountService {
    /**
     * 登录
     * @param userReqVo
     * @return
     */
    LoginResVo login(UserReqVo userReqVo);


    /**
     * 下线
     * @param uid
     */
    void offLine(Long uid,String token);
}