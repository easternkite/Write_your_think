package com.multimedia.writeyourthink;

public class UserInfo {
    private String userUID;
    private String userName;
    private String userProfile;
    private String userEmail;


    public UserInfo(){
    }



    public UserInfo(String userUID, String userName, String  userProfile, String userEmail){
        this.userUID = userUID;
        this.userName = userName;
        this.userProfile = userProfile;
        this.userEmail = userEmail;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

}
