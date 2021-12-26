package com.multimedia.writeyourthink


import androidx.recyclerview.widget.RecyclerView
import com.multimedia.writeyourthink.Diary
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.multimedia.writeyourthink.R
import android.widget.TextView
import com.bumptech.glide.Glide
import java.util.ArrayList

class DiaryAdapter : RecyclerView.Adapter<DiaryAdapter.ViewHolder>(), OnItemClickListener {
    var items = ArrayList<Diary>()
    var listener: OnItemClickListener? = null
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val itemview = inflater.inflate(R.layout.recycle_layout, viewGroup, false)
        return ViewHolder(itemview, this)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val diary = items[position]
        viewHolder.setItem(diary)
        viewHolder.itemView.isLongClickable = true
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addItem(diary: Diary) {
        items.add(diary)
    }

    fun removeItem() {
        items.clear()
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    override fun onItemClick(holder: ViewHolder?, view: View?, position: Int) {
        if (listener != null) {
            listener!!.onItemClick(holder, view, position)
        }
    }

    inner class ViewHolder(itemView: View, listener: OnItemClickListener?) :
        RecyclerView.ViewHolder(itemView) {
        var iconImageView: ImageView
        var textView: TextView
        var textView2: TextView
        var date: TextView
        var location: TextView
        fun setItem(diary: Diary) {
            Glide.with(itemView).load(diary.profile).into(iconImageView)
            textView.text = diary.where
            textView2.text = diary.contents
            date.text = diary.date
            location.text = diary.location
        }

        init {
            iconImageView = itemView.findViewById(R.id.iconImageView)
            textView = itemView.findViewById(R.id.textView)
            textView2 = itemView.findViewById(R.id.textView2)
            date = itemView.findViewById(R.id.tv_date)
            location = itemView.findViewById(R.id.tv_location)
            itemView.setOnClickListener { view ->
                val position = adapterPosition
                listener?.onItemClick(this@ViewHolder, view, position)
            }
        }
    }
}