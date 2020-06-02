package com.friday.route.fegin;

import com.friday.common.bean.reqVo.GroupOut;
import com.friday.common.bean.reqVo.GroupParams;
import com.friday.common.bean.reqVo.UserReqVo;
import com.friday.common.bean.resVo.LoginResVo;
import com.friday.common.bean.resVo.Result;
import com.friday.route.cache.ServerCache;
import com.friday.route.service.AccountService;
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

    @GetMapping("/server")
    public List<String> getServer() {
        return serverCache.getServerList();
    }

    @PostMapping("/login")
    public LoginResVo getToken(@RequestBody UserReqVo userReqVo) {
        return accountService.getToken(userReqVo);
    }

    @GetMapping("/sendLogin")
    public Result sendLogin(@RequestParam("uid") String uid, @RequestHeader("token") String token) {
        return Result.success();
    }


    @DeleteMapping("/offline/{uid}")
    public void offline(@PathVariable("uid") Long uid, @RequestHeader String token) {
        accountService.offLine(uid, token);
    }

    @GetMapping("/msg")
    public void sendMsg(@RequestParam("token") String uid, @RequestParam("msg") String msg, @RequestParam("toUid") String toUid) {
        accountService.sendMsg(uid, msg, toUid);
    }

    @PostMapping("/createGroup")
    public GroupOut createGroup(@RequestBody GroupParams p) {
        return accountService.createGroup(p);
    }

}
