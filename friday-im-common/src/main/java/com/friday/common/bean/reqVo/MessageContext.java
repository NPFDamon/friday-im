package com.friday.common.bean.reqVo;

import com.friday.common.protobuf.Message;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-06-01:11:36
 */
@Data
@Builder
public class MessageContext extends BaseBean{
    private Message.ConverType converType;
    private Message.MessageType messageType;
    private String fromUid;
    private String toUid;
    private List<String> toUids;
    private String content;
}
