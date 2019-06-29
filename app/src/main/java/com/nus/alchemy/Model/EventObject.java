package com.nus.alchemy.Model;


public class EventObject {
    private String eventTitle;
    private String preferredSex;
    private String startTime;
    private String eventDate;
    private String chatID;
    private String creatorUserID;
    private String creatorName;
    private String dateTime;
    private String eventID;
    //private int maxRoomSize;
    private boolean isActive;

    //null constructor for firebase ORM
    public EventObject() {};

    public EventObject(String eventTitle, String startTime, String eventDate, String dateTime,
                       String preferredSex, String creatorUserID, String creatorName, String eventID)
    {
        //this.maxRoomSize = maxRoomSize;
        this.startTime = startTime;
        this.preferredSex = preferredSex;
        this.creatorUserID = creatorUserID;
        this.creatorName = creatorName;
        this.eventDate = eventDate;
        this.dateTime = dateTime;
        this.eventTitle = eventTitle;
        this.isActive = false;
        this.eventID = eventID;
        this.chatID = "unassigned";
    }

    //getters
    //public int getMaxRoomSize() { return maxRoomSize; }
    public String getCreatorUserID() { return creatorUserID; }
    public String getPreferredSex() { return preferredSex; }
    public String getStartTime() { return startTime; }
    public String getEventDate() { return eventDate; }
    public String getCreatorName() { return creatorName; }
    public String getDateTime() { return dateTime; }
    public String getEventTitle() { return eventTitle; }
    public String getEventID() { return eventID; }
    public String getChatID() { return chatID; }
    public boolean getActive() { return isActive; }

    //setters
    public void setEventTitle(String eventName) {
        this.eventTitle = eventName;
    }
    public void setPreferredSex(String sex) {
        this.preferredSex = sex;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public void setCreatorName(String name){
        this.creatorName = name;
    }
    public void setChatID(String chatID) { this.chatID = chatID; }
    public void setActive(boolean status) { this.isActive = status; }
}
