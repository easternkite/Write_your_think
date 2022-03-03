package com.multimedia.writeyourthink.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "diaries"
)
data class Diary (
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var userUID: String?,
    var profile: String?,
    var where: String?,
    var contents: String?,
    var date: String?,
    var location: String?,
)
