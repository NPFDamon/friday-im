package com.friday.server.netty;

import io.netty.channel.Channel;

import java.util.List;

public interface UidChannelManager {
    void addUserToChannel(String uid, Channel channel);

    List<Channel> getChannelById(String uid);

    String getIdByChannel(Channel channel);

    void removeChannel(Channel channel);

    List<String> getAllIds();

    List<Channel> getAllChannels();
}
