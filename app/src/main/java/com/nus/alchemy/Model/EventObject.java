package com.nus.alchemy.Model;


import java.time.*;

public class EventObject {
    public String eventName;
    public String name;
    public String preferred_gender;
    public LocalDateTime startTime;
    private String chatID;
    private String userID;
    public int maxRoomSize;

    //null constructor for firebase ORM
    public EventObject(){};
}
