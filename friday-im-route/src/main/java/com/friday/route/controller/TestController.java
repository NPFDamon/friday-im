package com.friday.route.controller;

import com.friday.route.cache.ServerCache;
import com.friday.route.kafka.KafkaConsumerManage;
import com.friday.route.service.AccountService;
import com.friday.server.bean.reqVo.UserReqVo;
import com.friday.server.bean.resVo.LoginResVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Copyright (C),Damon
 *
 * @Description: test
 * @Author: Damon(npf)
 * @Date: 2020-05-14:11:47
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ServerCache serverCache;

    @Autowired
    private AccountService accountService;

    @Autowired
    private KafkaConsumerManage manage;

    @GetMapping("/server")
    public List<String> getServer() {
        return serverCache.getServerList();
    }

    @PostMapping("/login")
    public LoginResVo login(@RequestBody UserReqVo userReqVo) {
        return accountService.login(userReqVo);
    }

    @DeleteMapping("/offline/{uid}")
    public void offline(@PathVariable("uid")Long uid,@RequestHeader String token){
        accountService.offLine(uid,token);
    }

}
