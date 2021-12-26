package com.multimedia.writeyourthink

import android.view.View

open interface OnItemClickListener {
    fun onItemClick(holder: DiaryAdapter.ViewHolder?, view: View?, position: Int)
}