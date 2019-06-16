package com.nus.alchemy.Model;

import java.util.ArrayList;

public class MessageObject {

    String senderID;
    String messageID;
    String message;
    ArrayList<String> mediaUrlList;

    public MessageObject(String senderID, String messageID, String message, ArrayList<String> mediaUrlList) {
        this.senderID = senderID;
        this.messageID = messageID;
        this.message = message;
        this.mediaUrlList = mediaUrlList;
    }

    public String getSenderID() { return senderID; }
    public String getMessageID() { return messageID; }
    public String getMessage() { return message; }
    public ArrayList<String> getMediaUrlList() {     return mediaUrlList; }

}
