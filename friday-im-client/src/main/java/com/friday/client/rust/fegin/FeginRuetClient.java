package com.friday.client.rust.fegin;

import com.friday.common.bean.reqVo.MessageContext;
import com.friday.common.bean.reqVo.UserLoginBeanVO;
import com.friday.common.bean.reqVo.UserReqVo;
import com.friday.common.bean.resVo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-06-03:10:28
 */
@FeignClient(value = "friday-im-router")
public interface FeginRuetClient {
    @PostMapping("/registry")
    Result registry(@RequestBody UserReqVo reqVo);

    @PostMapping("/token")
    Result getToken(@RequestBody UserReqVo reqVo);

    @PostMapping("/serverInfo")
    Result getServer(@RequestHeader String token);

    @PostMapping("/send-message")
    Result sendMsg(@RequestBody MessageContext messageContext);

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    Result login(@RequestBody UserLoginBeanVO loginBeanVO);


    @PostMapping("/loginOut")
    Result logout(@RequestBody UserLoginBeanVO userLoginBeanVO);
}
