package com.multimedia.writeyourthink;

public class Diary {
    private String userName;
    private String profile;
    private String title;
    private String contents;
    private String date;
    private String location;


    public Diary(){
    }

    public Diary(String userName, String profile, String title, String contents, String date, String location){
        this.userName = userName;
        this.profile = profile;
        this.title = title;
        this.contents = contents;
        this.date = date;
        this.location = location;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
