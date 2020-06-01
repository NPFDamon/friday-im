package com.friday.route.client;

import com.friday.common.bean.im.ServerInfo;
import com.friday.common.bean.reqVo.MessageContext;
import com.friday.common.bean.reqVo.UserLoginBeanVO;
import com.friday.common.bean.resVo.Result;


public interface Client {
    void connect(ServerInfo serverInfo);

    void sendMsg(MessageContext messageContext);

    Result login(UserLoginBeanVO loginBeanVO);

    void reconnection();

}
