package com.nus.alchemy.Model;

public class ChatObject {

    String chatID;
    String otherUserID;
    String otherUserName;

    public ChatObject(String chatID, String otherUserID, String otherUserName) {
        this.chatID = chatID;
        this.otherUserID = otherUserID;
        this.otherUserName = otherUserName;
    }

    public String getChatID() { return chatID; }

    public String getOtherUserID() {
        return otherUserID;
    }

    public String getOtherUserName() {return otherUserName; }
}
