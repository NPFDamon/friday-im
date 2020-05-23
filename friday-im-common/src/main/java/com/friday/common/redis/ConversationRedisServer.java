package com.friday.common.redis;


import com.friday.common.bean.im.Conversation;
import com.friday.common.bean.im.MessageContent;
import com.friday.common.bean.im.UserConversation;
import com.friday.common.protobuf.Message;

import java.util.List;

public interface ConversationRedisServer {
    void saveUserClientId(String uid, String clientId);

    boolean isUserCidExit(String uid, String clientId);

    String newSingleConversationId(String fromUid, String toUid);

    String newGroupConversationId(String groupId, List<String> ids);

    void addMemberConversationId(String groupId, List<String> ids);

    void removeMemberConversationList(String groupId, List<String> ids);

    void dismissGroup(String groupId);

    boolean isSingleConversationIdValid(String conversationId);

    String getGroupIdByConversationId(String conversationId);

    void saveMsgToConversation(MessageContent messageContent, String conversationId);

    List<MessageContent> getHistoryMsg(String conversationId, long beginId);

    long getHistoryUnreadCount(String conversationId, long beginId);

    Conversation getConversation(String conversationId);

    UserConversation getConversationListInfo(String uid, String conversationId);

    List<UserConversation> getConversationListByUid(String uid);

    List<String> getUidListByConversation(String conversationId);

    List<String> getUidListByConversationExcludeSender(String conversation, String fromUid);

    void updateUserReadMessageId(String uid, String conversationId, Long msgId);

    Long getUserReadMsg(String uid, String conversationId);

    void saveWaitUserAckMsg(String uid, String conversationId, Long msgId);

    void deleteWaitUserAckMsg(String uid, String conversationId, Long msgId);

    List<Message.UpDownMessage> getWaitUserAckMsg(String uid);

}
