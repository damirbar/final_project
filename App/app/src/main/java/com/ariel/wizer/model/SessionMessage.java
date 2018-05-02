package com.ariel.wizer.model;

public class SessionMessage {

    String guid;
    String channel_guid;
    String user_guid;
    long timestamp;
    String content;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getChannelGuid() {
        return channel_guid;
    }

    public void setChannelGuid(String channelGuid) {
        this.channel_guid = channelGuid;
    }

    public String getUserGuid() {
        return user_guid;
    }

    public void setUserGuid(String userGuid) {
        this.user_guid = userGuid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
