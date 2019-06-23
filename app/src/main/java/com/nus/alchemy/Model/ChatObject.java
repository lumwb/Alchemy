package com.nus.alchemy.Model;

public class ChatObject {

    String chatID;
    String otherUserID;
    String otherUserName;
    String otherUserProfileImage;

    public ChatObject(String chatID, String otherUserID, String otherUserName, String otherUserProfileImage) {
        this.chatID = chatID;
        this.otherUserID = otherUserID;
        this.otherUserName = otherUserName;
        this.otherUserProfileImage = otherUserProfileImage;
    }

    public String getChatID() { return chatID; }

    public String getOtherUserID() {
        return otherUserID;
    }

    public String getOtherUserName() {return otherUserName; }

    public String getOtherUserProfileImage() { return otherUserProfileImage; }
}

