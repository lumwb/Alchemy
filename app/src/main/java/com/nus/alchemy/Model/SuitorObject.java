package com.nus.alchemy.Model;

public class SuitorObject {

    private String suitorId;
    private String suitorName;

    public SuitorObject(String suitorId, String suitorName) {
        this.suitorId = suitorId;
        this.suitorName = suitorName;
    }

    public SuitorObject(String suitorId) {
        this.suitorId = suitorId;
    }

    public String getSuitorId() {
        return suitorId;
    }

    public String getSuitorName() {
        return suitorName;
    }
}
