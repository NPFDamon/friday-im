package com.friday.ruote.api;


import com.friday.common.bean.im.ServerInfo;
import com.friday.common.bean.reqVo.MessageContext;
import com.friday.common.bean.reqVo.UserLoginBeanVO;
import com.friday.common.bean.resVo.Result;

import java.util.List;

/**
 * Copyright (C),Damon
 *
 * @Description: route api interface
 * @Author: Damon(npf)
 * @Date: 2020-05-12:16:37
 */
public interface RouteApi {
    void connect(ServerInfo serverInfo);

    void sendMsg(MessageContext messageContext);

    Result login(UserLoginBeanVO loginBeanVO);

    List<ServerInfo> getAllServer();

    ServerInfo getLbServer(String uid);
}
