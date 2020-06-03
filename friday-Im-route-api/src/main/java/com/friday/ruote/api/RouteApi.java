package com.friday.ruote.api;


import com.friday.common.bean.reqVo.MessageContext;
import com.friday.common.bean.reqVo.UserLoginBeanVO;
import com.friday.common.bean.resVo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Copyright (C),Damon
 *
 * @Description: route api interface
 * @Author: Damon(npf)
 * @Date: 2020-05-12:16:37
 */
public interface RouteApi {
    @PostMapping("/send-message")
    void sendMsg(MessageContext messageContext);

    @PostMapping("/login")
    Result login(@RequestBody UserLoginBeanVO loginBeanVO);
}
