package com.friday.route.redis;

import com.friday.server.bean.resVo.LoginResVo;
import org.apache.catalina.util.ServerInfo;

/**
 * Copyright (C),Damon
 *
 * @Description: redis server interface
 * @Author: Damon(npf)
 * @Date: 2020-05-12:16:37
 */
public interface RedisService {

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
