package com.friday.route.fegin.interfaces;

import com.friday.common.bean.reqVo.MessageContext;
import com.friday.common.bean.reqVo.UserLoginBeanVO;
import com.friday.common.bean.resVo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface FeginService {
    @PostMapping("/send-message")
    void sendMsg(@RequestBody MessageContext messageContext);

    @PostMapping("/login")
    Result login(@RequestBody UserLoginBeanVO loginBeanVO);
}
