package com.friday.common.bean.im;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-23:10:19
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Conversation implements Serializable {

    private String id;

    private int type;

    private List<String> uidList;

    private String groupId;
}
