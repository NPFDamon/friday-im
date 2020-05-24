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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-17:11:32
 */
@Component
@Slf4j
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
        return conversation != null && conversation.getType() == Message.ConverType.SINGLE.getNumber();
    }

    @Override
    public String getGroupIdByConversationId(String conversationId) {
        Conversation conversation = getConversation(conversationId);
        if (conversation == null && conversation.getType() == Message.ConverType.GROUP.getNumber()) {
            return conversation.getGroupId();
        }
        return null;
    }

    @Override
    public void saveMsgToConversation(Message.MessageContent messageContent, String conversationId) {
        MessageContent msgContent = new MessageContent().builder().id(messageContent.getId()).uid(messageContent.getUid())
                .type(messageContent.getType().getNumber()).content(messageContent.getContent()).time(messageContent.getTime()).build();
        String str = JsonHelper.toJsonString(msgContent);
        redisTemplate.boundZSetOps(Constants.MESSAGE_ID + conversationId).add(str, msgContent.getId());
    }

    @Override
    public List<MessageContent> getHistoryMsg(String conversationId, long beginId) {
        Set<String> messages = redisTemplate.opsForZSet()
                .rangeByScore(Constants.MESSAGE_ID + conversationId, beginId + 1, Long.MAX_VALUE, 0, 100);
        return Objects.requireNonNull(messages).stream().map(message -> JsonHelper.readValue(message, MessageContent.class)).collect(Collectors.toList());
    }

    @Override
    public long getHistoryUnreadCount(String conversationId, long beginId) {
        Long unReadCount = redisTemplate.boundZSetOps(Constants.MESSAGE_ID + conversationId)
                .count(beginId + 1, Long.MAX_VALUE);
        return unReadCount;
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
        Conversation conversation = getConversation(conversationId);
        if (null != conversation) {
            UserConversation converListInfo = new UserConversation().builder()
                    .id(conversation.getId()).groupId(conversation.getGroupId())
                    .uidList(conversation.getUidList())
                    .type(conversation.getType()).build();
            Long readMsgId = getUserReadMsg(uid, conversationId);
            if (null != readMsgId) {
                converListInfo.setReadMsgId(readMsgId);
            }
            getMessgContent(conversation, converListInfo);
            return converListInfo;
        }
        return null;
    }

    @Override
    public List<UserConversation> getConversationListByUid(String uid) {
        List<UserConversation> list = new ArrayList<>();
        Map<String, Long> converList = redisTemplate.boundHashOps(Constants.CONVERSATION_LIST + uid).entries();
        Objects.requireNonNull(converList).forEach((key, value) -> {
            Conversation conversation = getConversation(key);
            if (null != conversation) {
                UserConversation converListInfo = new UserConversation().builder()
                        .id(conversation.getId()).groupId(conversation.getGroupId())
                        .uidList(conversation.getUidList()).type(conversation.getType())
                        .readMsgId(value).build();
                getMessgContent(conversation, converListInfo);
                list.add(converListInfo);
            }
        });
        return list;
    }

    private void getMessgContent(Conversation conversation, UserConversation converListInfo) {
        Set<String> strs = redisTemplate.boundZSetOps(Constants.MESSAGE_ID + conversation.getId()).range(-1, -1);
        if (strs != null && strs.size() >= 1) {
            MessageContent msgContent = JsonHelper.readValue(strs.iterator().next(), MessageContent.class);
            converListInfo.setMessageContent(msgContent);
        }
    }

    @Override
    public List<String> getUidListByConversation(String conversationId) {
        Conversation conversation = getConversation(conversationId);
        return conversation.getUidList();
    }

    @Override
    public List<String> getUidListByConversationExcludeSender(String conversation, String fromUid) {
        List<String> uids = getUidListByConversation(conversation);
        uids.remove(fromUid);
        return uids;
    }

    @Override
    public void updateUserReadMessageId(String uid, String conversationId, Long msgId) {
        Object oldMsgId = redisTemplate.boundHashOps(Constants.CONVERSATION_LIST + uid).get(conversationId);
        if (null != oldMsgId) {
            if ((Long) oldMsgId < msgId) {
                redisTemplate.boundHashOps(Constants.CONVERSATION_LIST + uid).put(conversationId, msgId);
            }
        }
    }

    @Override
    public Long getUserReadMsg(String uid, String conversationId) {
        Object msgId = redisTemplate.boundHashOps(Constants.CONVERSATION_LIST + uid).get(conversationId);
        if (null != msgId) {
            return (Long) msgId;
        }
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
        String key = Constants.WAIT_USER_ACK + uid;
        Set<String> keyList = redisTemplate.opsForZSet().rangeByScore(key, 0, System.currentTimeMillis() - 5000, 0, 3);
        List<Message.UpDownMessage> upDownMessages = new ArrayList<>();
        Objects.requireNonNull(keyList).forEach(str -> {
            log.info("not ack msg key:{}", str);
            String converId = str.split(Constants.DEFAULT_SEPARATES)[0];
            String msgId = str.split(Constants.DEFAULT_SEPARATES)[1];
            Set<String> messages = redisTemplate.opsForZSet().rangeByScore(Constants.MESSAGE_ID + converId, Long.parseLong(msgId), Long.parseLong(msgId), 0, 1);
            if (CollectionUtils.isNotEmpty(messages)) {
                MessageContent msgContent = JsonHelper
                        .readValue(messages.iterator().next(), MessageContent.class);
                Message.MessageContent content = Message.MessageContent.newBuilder()
                        .setId(Objects.requireNonNull(msgContent).getId())
                        .setUid(msgContent.getUid())
                        .setContent(msgContent.getContent())
                        .setTime(msgContent.getTime())
                        .setType(Message.MessageType.valueOf(msgContent.getType())).build();
                Conversation conversation = getConversation(converId);
                Message.UpDownMessage downMessage = Message.UpDownMessage.newBuilder()
                        .setRequestId(msgContent.getId())
                        .setFromUid(msgContent.getUid())
                        .setToUid(uid)
                        .setConverType(Message.ConverType.forNumber(conversation.getType()))
                        .setContent(content)
                        .setConverId(converId)
                        .build();
                upDownMessages.add(downMessage);
            }
        });
        return upDownMessages;
    }


}
