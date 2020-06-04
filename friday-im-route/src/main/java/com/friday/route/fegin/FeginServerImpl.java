package com.friday.route.fegin;

import com.friday.common.bean.reqVo.MessageContext;
import com.friday.common.bean.reqVo.UserLoginBeanVO;
import com.friday.common.bean.reqVo.UserReqVo;
import com.friday.common.bean.resVo.Result;
import com.friday.route.client.Client;
import com.friday.route.fegin.interfaces.FeginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-06-02:12:22
 */
@RestController
public class FeginServerImpl implements FeginService {
    @Autowired
    private Client client;

    @Override
    public Result registry(UserReqVo reqVo) {
        return null;
    }

    @Override
    public String getToken(UserReqVo reqVo) {
        return null;
    }

    @Override
    public void sendMsg(MessageContext messageContext) {
        client.sendMsg(messageContext);
    }


    @Override
    public Result login(UserLoginBeanVO vo) {
//        client.login(vo);
        return Result.success("Test Fegin");
    }

    @Override
    public Result logout(UserLoginBeanVO userLoginBeanVO) {
        return null;
    }
}
