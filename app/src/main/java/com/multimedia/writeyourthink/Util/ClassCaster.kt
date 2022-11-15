package com.multimedia.writeyourthink.Util

import android.app.Activity
import android.content.Context
import com.multimedia.writeyourthink.ui.DiaryActivity
import kotlin.reflect.KClass

fun Context.getDiaryActivity(): DiaryActivity {
    return (this as DiaryActivity)
}