package com.friday.route.fegin;

import com.friday.common.bean.reqVo.UserLoginBeanVO;
import com.friday.common.bean.resVo.Result;
import com.friday.route.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-06-02:12:22
 */
@RestController
public class FeginServer {
    @Autowired
    private Client client;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result login(@RequestBody UserLoginBeanVO vo) {
        return client.login(vo);
    }
}
