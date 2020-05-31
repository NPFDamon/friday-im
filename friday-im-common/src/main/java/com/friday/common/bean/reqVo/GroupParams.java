package com.friday.common.bean.reqVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-31:11:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupParams {
    private String groupId;

    private String name;

    private String portrait;

    private List<String> members;
}
