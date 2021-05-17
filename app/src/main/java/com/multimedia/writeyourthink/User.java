package com.multimedia.writeyourthink;

public class User {
    private String profile;
    private String id;
    private int pw;
    private String userName;
    private String userPhone;

    public User(){
    }



    public User(String profile, String id, int pw, String userName, String userPhone){
        this.profile = profile;
        this.id = id;
        this.pw = pw;
        this.userName = userName;
        this.userPhone = userPhone;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPw() {
        return pw;
    }

    public void setPw(int pw) {
        this.pw = pw;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}
