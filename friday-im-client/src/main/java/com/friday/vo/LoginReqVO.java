package com.friday.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Copyright (C),Damon
 *
 * @Description: Login info
 * @Author: Damon(npf)
 * @Date: 2020-05-11:10:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginReqVO implements Serializable {

    private String userId;
    private String passWord;


}
