package com.nus.alchemy.Model;

public class MessageObject {

    String senderID;
    String messageID;
    String message;

    public MessageObject(String senderID, String messageID, String message) {
        this.senderID = senderID;
        this.messageID = messageID;
        this.message = message;
    }

    public String getSenderID() { return senderID; }
    public String getMessageID() { return messageID; }
    public String getMessage() { return message; }

}
