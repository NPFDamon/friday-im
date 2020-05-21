package com.friday.common.bean.resVo;

import com.friday.common.enums.LoginStatusEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * Copyright (C),Damon
 *
 * @Description: login res
 * @Author: Damon(npf)
 * @Date: 2020-05-13:14:30
 */
@Data
public class LoginResVo implements Serializable {
    /**
     * 登录信息
     */
    private LoginStatusEnum loginStatus;
    /**
     * token信息
     */
    private String token;
}
