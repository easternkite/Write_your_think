package com.multimedia.writeyourthink

import android.content.Context

import androidx.recyclerview.widget.RecyclerView
import com.multimedia.writeyourthink.CustomAdapter.CustomViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.multimedia.writeyourthink.R
import android.widget.TextView
import com.bumptech.glide.Glide
import java.util.ArrayList

class CustomAdapter(private val arrayList: ArrayList<Diary>?, private val context: Context) :
    RecyclerView.Adapter<CustomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycle_layout, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val diary = arrayList!![position]
        holder.setItem(diary)
    }

    override fun getItemCount(): Int {
        // 삼항 연산자
        return arrayList?.size ?: 0
    }

    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iconImageView: ImageView
        var textView: TextView
        var textView2: TextView
        var tv_date: TextView
        var tv_location: TextView
        fun setItem(diary: Diary) {
            Glide.with(itemView).load(diary.profile).into(iconImageView)
            textView.text = diary.where
            textView2.text = diary.contents
            tv_date.text = diary.date
            tv_location.text = diary.location
        }

        init {
            iconImageView = itemView.findViewById(R.id.iconImageView)
            textView = itemView.findViewById(R.id.textView)
            textView2 = itemView.findViewById(R.id.textView2)
            tv_date = itemView.findViewById(R.id.tv_date)
            tv_location = itemView.findViewById(R.id.tv_location)
        }
    }
}