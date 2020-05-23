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
 * @Date: 2020-05-23:10:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserConversation implements Serializable {

    private String id;

    private int type;

    private List<String> uidList;

    private String groupId;

    private long readMsgId;

    MessageContent messageContent;
}
