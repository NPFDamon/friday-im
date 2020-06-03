package com.friday.client.rust.controller;

import com.friday.client.rust.fegin.FeginRuetClient;
import com.friday.common.bean.reqVo.MessageContext;
import com.friday.common.bean.reqVo.UserLoginBeanVO;
import com.friday.common.bean.resVo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-22:14:41
 */
@RestController
@RequestMapping("friday-im")
public class RustController{
    @Autowired
    private FeginRuetClient feignClient;

    @PostMapping("/send-message")
    public void sendMsg(@RequestBody MessageContext messageContext) {
        feignClient.sendMsg(messageContext);
    }

    @PostMapping("/login")
    public Result login(@RequestBody UserLoginBeanVO loginBeanVO){
        return feignClient.login(loginBeanVO);
    }

}
