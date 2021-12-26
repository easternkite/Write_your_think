package com.multimedia.writeyourthink

class UserInfo {
    var userUID: String? = null
    var userName: String? = null
    var userProfile: String? = null
    var userEmail: String? = null

    constructor() {}
    constructor(userUID: String?, userName: String?, userProfile: String?, userEmail: String?) {
        this.userUID = userUID
        this.userName = userName
        this.userProfile = userProfile
        this.userEmail = userEmail
    }
}