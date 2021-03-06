package com.friday.common.redis;

import com.friday.common.bean.im.ServerInfo;
import com.friday.common.bean.resVo.LoginResVo;

/**
 * Copyright (C),Damon
 *
 * @Description: redis server interface
 * @Author: Damon(npf)
 * @Date: 2020-05-12:16:37
 */
public interface UserInfoRedisService {

    /**
     * 保存登录信息
     */
    void storeUserLoginInfo(LoginResVo loginResVo);

    /***
     * 下线
     */
    void offLine(String token);

    /**
     * 保存user,server对应关系
     */
    void storeIMServerInfo(String uid, ServerInfo serverInfo);


}
