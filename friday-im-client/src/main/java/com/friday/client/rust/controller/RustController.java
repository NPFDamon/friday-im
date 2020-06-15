package com.friday.client.rust.controller;

import com.friday.client.rust.fegin.FeginRuetClient;
import com.friday.common.bean.reqVo.MessageContext;
import com.friday.common.bean.reqVo.UserLoginBeanVO;
import com.friday.common.bean.reqVo.UserReqVo;
import com.friday.common.bean.resVo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-22:14:41
 */
@RestController
@RequestMapping("friday-im")
public class RustController {
    @Autowired
    private FeginRuetClient feignClient;

    @RequestMapping(value = "/send-message", method = RequestMethod.POST)
    public Result sendMsg(@RequestBody MessageContext messageContext) {
        return feignClient.sendMsg(messageContext);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result login(@RequestBody UserLoginBeanVO loginBeanVO) {
        return feignClient.login(loginBeanVO);
    }

    @RequestMapping(value = "/token", method = RequestMethod.POST)
    Result getToken(@RequestBody UserReqVo reqVo) {
        return feignClient.getToken(reqVo);
    }

    @RequestMapping(value = "/serverInfo", method = RequestMethod.POST)
    Result getServer(@RequestHeader String token) {
        return feignClient.getServer(token);
    }

}
