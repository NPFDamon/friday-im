package com.friday.route.fegin.interfaces;

import com.friday.common.bean.reqVo.MessageContext;
import com.friday.common.bean.reqVo.UserLoginBeanVO;
import com.friday.common.bean.reqVo.UserReqVo;
import com.friday.common.bean.resVo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface FeginService {

    @PostMapping("/registry")
    Result registry(@RequestBody UserReqVo reqVo);

    @PostMapping("/token")
    Result getToken(@RequestBody UserReqVo reqVo);

    @PostMapping("/serverInfo")
    Result getServer(@RequestHeader String token);

    @PostMapping("/send-message")
    Result sendMsg(@RequestBody MessageContext messageContext);

    @PostMapping("/login")
    Result login(@RequestBody UserLoginBeanVO loginBeanVO);

    @PostMapping("/loginOut")
    Result logout(@RequestBody UserLoginBeanVO userLoginBeanVO);
}
