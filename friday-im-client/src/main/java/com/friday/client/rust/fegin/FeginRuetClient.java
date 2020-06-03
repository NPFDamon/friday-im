package com.friday.client.rust.fegin;

import com.friday.common.bean.reqVo.MessageContext;
import com.friday.common.bean.reqVo.UserLoginBeanVO;
import com.friday.common.bean.resVo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-06-03:10:28
 */
@FeignClient(value = "friday-im-router")
public interface FeginRuetClient{
    @PostMapping("/send-message")
    void sendMsg(@RequestBody MessageContext messageContext);

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    Result login(@RequestBody UserLoginBeanVO loginBeanVO);
}
