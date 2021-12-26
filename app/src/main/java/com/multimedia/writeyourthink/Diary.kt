package com.multimedia.writeyourthink

class Diary {
    var userUID: String? = null
        private set
    var profile: String? = null
    var where: String? = null
    var contents: String? = null
    var date: String? = null
    var location: String? = null

    constructor() {}
    constructor(
        userUID: String?,
        profile: String?,
        where: String?,
        contents: String?,
        date: String?,
        location: String?
    ) {
        this.userUID = userUID
        this.profile = profile
        this.where = where
        this.contents = contents
        this.date = date
        this.location = location
    }

    fun setUserName(userUID: String?) {
        this.userUID = userUID
    }
}