package com.friday.common.bean.im;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-23:10:17
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MessageContent implements Serializable {

    private long id;

    private String uid;

    private int type;

    private String content;

    private long time;
}
