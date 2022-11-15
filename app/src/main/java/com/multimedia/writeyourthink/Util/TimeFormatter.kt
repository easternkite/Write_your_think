package com.multimedia.writeyourthink.Util

import android.content.Context
import android.util.Log
import com.multimedia.writeyourthink.R
import java.text.SimpleDateFormat
import java.util.Date

fun formatTime(context: Context, dateAndTime: String): String {
    if (dateAndTime.isEmpty()) return ""
    val format = SimpleDateFormat("yyyy-MM-dd(HH:mm:ss)")
    val date = format.parse(dateAndTime)
    val time = date?.time ?: 0
    val curTime = System.currentTimeMillis()
    Log.d("Lee","time [$time]")
    Log.d("Lee","currentTime [$curTime]")
    Log.d("Lee","time formatted [${format.format(time)}]")

    val SEC = 60
    val MIN = 60
    val HOUR = 24
    val DAY = 30
    val MONTH = 12
    val timeList = listOf(SEC, MIN, HOUR, DAY, MONTH)
    context.apply {

    }
    val msgs = listOf(
        context.getString(R.string.minutes),
        context.getString(R.string.hour),
        context.getString(R.string.days),
        context.getString(R.string.months)
    )

    var diffTime = (curTime - time) / 1000
    Log.d("Lee","diffTime [${diffTime}]")

    var message = ""
    if (diffTime < SEC) {
        return context.getString(R.string.now)
    }

    timeList
        .filterIndexed { index, _ -> (index < timeList.size -1) && message.isEmpty() }
        .forEachIndexed { index, num ->
        diffTime /= num
        if (diffTime < timeList[index + 1]) {
            message = "${diffTime}${msgs[index]}"
            Log.d("Lee","MSG in ForEach [${message}]")
            return message
        }
    }
    if (message.isEmpty()) {
        diffTime /= MONTH
        message = "${diffTime}년 전"
    }
   Log.d("Lee","MSG [${message}]")
    return message
}

fun Date.formatDate(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd(HH:mm:ss)")
    return sdf.format(this)
}