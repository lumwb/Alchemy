package com.nus.alchemy.Model;

public class UserObject {

    private String id;
    private String name;

    public UserObject(String name) {
        this.name = name;
    }

    public UserObject(String id, String name) {
        this.name = name;
    }

    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
    public String getName() {
        return name;
    }
    public void setName() {this.name = name;};

}
