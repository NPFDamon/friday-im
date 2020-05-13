package com.friday.server.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * Copyright (C),Damon
 *
 * @Description: bean
 * @Author: Damon(npf)
 * @Date: 2020-05-13:11:47
 */
@Data
public class BaseBean implements Serializable {

    private String reqNo;

    private long timeStamp;

    public BaseBean(){
        this.timeStamp = System.currentTimeMillis();
    }

}
