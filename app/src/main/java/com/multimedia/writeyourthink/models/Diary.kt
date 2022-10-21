package com.multimedia.writeyourthink.models

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Keep
@Parcelize
data class Diary(
    var userUID: String = "",
    var profile: String = "",
    var where: String = "",
    var contents: String = "",
    var date: String = "",
    var location: String = "",
) : Parcelable {
    companion object {
        val EMPTY = Diary("","","","","","")
    }
}


