package com.nus.alchemy.Model;


public class EventObject {
    private String eventTitle;
    private String name;
    private String preferredSex;
    private String startTime;
    private String eventDate;
    private String chatID;
    private String creatorUserID;
    private String creatorName;
    private String dateTime;
    private int maxRoomSize;

    //null constructor for firebase ORM
    public EventObject() {};

    public EventObject(String eventTitle, int maxRoomSize, String startTime, String eventDate, String dateTime,
                       String preferredSex, String creatorUserID, String creatorName)
    {
        this.maxRoomSize = maxRoomSize;
        this.startTime = startTime;
        this.preferredSex = preferredSex;
        this.creatorUserID = creatorUserID;
        this.creatorName = creatorName;
        this.eventDate = eventDate;
        this.dateTime = dateTime;
        this.eventTitle = eventTitle;
    }

    //getters
    public int getMaxRoomSize() { return maxRoomSize; }
    public String getCreatorUserID() { return creatorUserID; }
    public String getPreferredSex() { return preferredSex; }
    public String getStartTime() { return startTime; }
    public String getEventDate() { return eventDate; }
    public String getCreatorName() { return creatorName; }
    public String getDateTime() { return dateTime; }
    public String getEventTitle() { return eventTitle; }
    //setters
    public void setEventTitle(String eventName) {
        this.eventTitle = eventName;
    }
    public void setPreferredSex(String sex) {
        this.preferredSex = sex;
    }
    public void setMaxRoomSize(int size) {
        this.maxRoomSize = size;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public void setCreatorName(String name){
        this.creatorName = name;
    }
}
