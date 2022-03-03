package com.multimedia.writeyourthink.adapters

import android.view.View
import com.multimedia.writeyourthink.adapters.DiaryAdapter

open interface OnItemClickListener {
    fun onItemClick(holder: DiaryAdapter.ViewHolder?, view: View?, position: Int)
}