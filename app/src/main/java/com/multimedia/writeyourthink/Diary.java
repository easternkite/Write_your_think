package com.multimedia.writeyourthink;

public class Diary {
    private String userUID;
    private String profile;
    private String with;
    private String contents;
    private String date;
    private String location;


    public Diary(){
    }

    public Diary(String userUID, String profile, String with, String contents, String date, String location){
        this.userUID = userUID;
        this.profile = profile;
        this.with = with;
        this.contents = contents;
        this.date = date;
        this.location = location;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserName(String userUID) {
        this.userUID = userUID;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getWith() {
        return with;
    }

    public void setWith(String with) {
        this.with = with;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
