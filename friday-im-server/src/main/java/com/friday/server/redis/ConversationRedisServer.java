package com.friday.server.redis;

public interface ConversationRedisServer {
    void saveUserClientId(String uid, String clientId);

    boolean isUserCidExit(String uid, String clientId);
}
