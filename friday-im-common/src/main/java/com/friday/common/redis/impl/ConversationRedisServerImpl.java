package com.friday.common.redis.impl;

import com.friday.common.bean.im.Conversation;
import com.friday.common.bean.im.MessageContent;
import com.friday.common.bean.im.UserConversation;
import com.friday.common.constant.Constants;
import com.friday.common.protobuf.Message;
import com.friday.common.redis.ConversationRedisServer;
import com.friday.common.utils.JsonHelper;
import com.friday.common.utils.UidUtil;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-17:11:32
 */
@Component
public class ConversationRedisServerImpl implements ConversationRedisServer {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void saveUserClientId(String uid, String clientId) {
        redisTemplate.boundZSetOps(Constants.USER_CLIENT_INFO + uid).removeRangeByScore(0, System.currentTimeMillis() - (1000 * 60 * 5));
        redisTemplate.boundZSetOps(Constants.USER_CLIENT_INFO + uid).add(clientId, System.currentTimeMillis());
    }

    @Override
    public boolean isUserCidExit(String uid, String clientId) {
        return redisTemplate.boundZSetOps(Constants.USER_CLIENT_INFO + uid).rank(clientId) == null;
    }

    @Override
    public String newSingleConversationId(String fromUid, String toUid) {
        String conversationId = UidUtil.uuid24By2Factor(fromUid, toUid);
        Set<String> uidList = new HashSet<>();
        uidList.add(fromUid);
        uidList.add(toUid);
        Conversation conversation = Conversation.builder()
                .id(conversationId)
                .type(Message.ConverType.SINGLE.getNumber())
                .uidList(new ArrayList<>(uidList)).build();
        boolean res = redisTemplate.opsForValue().setIfAbsent(conversationId, JsonHelper.toJsonString(conversation));

        if (res) {
            redisTemplate.boundHashOps(Constants.CONVERSATION_LIST + fromUid).put(conversationId, Long.MIN_VALUE);
            redisTemplate.boundHashOps(Constants.CONVERSATION_LIST + toUid).put(conversationId, Long.MIN_VALUE);
        }
        return conversationId;
    }

    @Override
    public String newGroupConversationId(String groupId, List<String> ids) {
        String conversationId = UidUtil.uuid24ByFactor(groupId);
        Conversation conversation = new Conversation().builder().id(conversationId)
                .type(Message.ConverType.GROUP.getNumber())
                .groupId(groupId).build();
        boolean result = redisTemplate.opsForValue()
                .setIfAbsent(conversationId, JsonHelper.toJsonString(conversation));
        if (result) {
            ids.forEach(id -> {
                redisTemplate.boundSetOps(Constants.GROUP_MEMBER + groupId).add(id);
                redisTemplate.boundHashOps(Constants.CONVERSATION_LIST + id).put(conversationId, Long.MIN_VALUE);
            });
        }
        return conversationId;
    }

    @Override
    public void addMemberConversationId(String groupId, List<String> ids) {
        String conversationId = UidUtil.uuid24ByFactor(groupId);
        ids.forEach(id -> {
            redisTemplate.boundSetOps(Constants.GROUP_MEMBER + groupId).add(id);
            redisTemplate.boundHashOps(Constants.CONVERSATION_LIST + id).put(conversationId, Long.MIN_VALUE);
        });
    }

    @Override
    public void removeMemberConversationList(String groupId, List<String> ids) {
        String conversationId = UidUtil.uuid24ByFactor(groupId);
        ids.forEach(id -> {
            redisTemplate.boundSetOps(Constants.GROUP_MEMBER + groupId).remove(id);
            redisTemplate.boundHashOps(Constants.CONVERSATION_LIST + id).delete(conversationId);
        });
    }

    @Override
    public void dismissGroup(String groupId) {
        String conversationId = UidUtil.uuid24ByFactor(groupId);
        Set<String> uids = redisTemplate.boundSetOps(Constants.GROUP_MEMBER + groupId).members();
        assert uids != null;
        uids.forEach(id -> redisTemplate.boundHashOps(Constants.CONVERSATION_LIST + id).delete(conversationId));

        redisTemplate.delete(Constants.GROUP_MEMBER + groupId);
        redisTemplate.delete(conversationId);
    }

    @Override
    public boolean isSingleConversationIdValid(String conversationId) {
        Conversation conversation = getConversation(conversationId);
        return conversation == null ? false
                : conversation.getType() == Message.ConverType.SINGLE.getNumber();
    }

    @Override
    public String getGroupIdByConversationId(String conversationId) {
        Conversation conversation = getConversation(conversationId);
        if (conversation != null ? false : conversation.getType() == Message.ConverType.GROUP.getNumber()) {
            return conversation.getGroupId();
        }
        return null;
    }

    @Override
    public void saveMsgToConversation(MessageContent messageContent, String conversationId) {

    }

    @Override
    public List<MessageContent> getHistoryMsg(String conversationId, long beginId) {
        return null;
    }

    @Override
    public long getHistoryUnreadCount(String conversationId, long beginId) {
        return 0;
    }

    @Override
    public Conversation getConversation(String conversationId) {
        Object ob = redisTemplate.opsForValue().get(conversationId);
        if (null == ob) {
            return null;
        }
        Conversation conversation = JsonHelper.readValue(ob.toString(), Conversation.class);
        if (conversation.getType() == Message.ConverType.GROUP.getNumber()) {
            Set<String> uids = redisTemplate
                    .boundSetOps(Constants.GROUP_MEMBER + conversation.getGroupId()).members();
            conversation.setUidList(Lists.newArrayList(uids));
        }
        return conversation;
    }

    @Override
    public UserConversation getConversationListInfo(String uid, String conversationId) {
        return null;
    }

    @Override
    public List<UserConversation> getConversationListByUid(String uid) {
        return null;
    }

    @Override
    public List<String> getUidListByConversation(String conversationId) {
        return null;
    }

    @Override
    public List<String> getUidListByConversationExcludeSender(String conversation, String fromUid) {
        return null;
    }

    @Override
    public void updateUserReadMessageId(String uid, String conversationId, Long msgId) {

    }

    @Override
    public Long getUserReadMsg(String uid, String conversationId) {
        return null;
    }

    @Override
    public void saveWaitUserAckMsg(String uid, String conversationId, Long msgId) {
        redisTemplate.boundZSetOps(Constants.WAIT_USER_ACK + uid).add(conversationId + Constants.DEFAULT_SEPARATES + msgId, System.currentTimeMillis());
    }

    @Override
    public void deleteWaitUserAckMsg(String uid, String conversationId, Long msgId) {
        redisTemplate.boundZSetOps(Constants.WAIT_USER_ACK + uid).remove(conversationId + Constants.DEFAULT_SEPARATES + msgId);
    }

    @Override
    public List<Message.UpDownMessage> getWaitUserAckMsg(String uid) {
        return null;
    }


}
