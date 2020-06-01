package com.friday.common.bean.reqVo;

import com.friday.common.bean.im.ServerInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-06-01:11:40
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserLoginBeanVO extends BaseBean implements Serializable {
    private String uid;
    private String token;
    private ServerInfo serverInfo;
}
