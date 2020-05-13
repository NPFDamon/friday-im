package com.friday.server.bean.reqVo;

import com.friday.server.bean.BaseBean;
import lombok.Data;

import java.util.Date;

/**
 * Copyright (C),Damon
 *
 * @Description: user vo
 * @Author: Damon(npf)
 * @Date: 2020-05-13:11:52
 */
@Data
public class UserReqVo extends BaseBean {

    private String uid;
    private String secret;
    private String userName;
    private Date createDate;
    private Date updateDate;
}
