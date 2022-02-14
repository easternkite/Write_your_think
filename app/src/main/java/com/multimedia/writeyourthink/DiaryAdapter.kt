package com.multimedia.writeyourthink


import androidx.recyclerview.widget.RecyclerView
import com.multimedia.writeyourthink.Diary
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import com.multimedia.writeyourthink.R
import android.widget.TextView
import com.bumptech.glide.Glide
import java.util.*

class DiaryAdapter : RecyclerView.Adapter<DiaryAdapter.ViewHolder>(), OnItemClickListener, Filterable {
    var items = ArrayList<Diary>()
    var filteredList = ArrayList<Diary>(items)
    var unFilteredList = ArrayList<Diary>(items)
    var listener: OnItemClickListener? = null
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val itemview = inflater.inflate(R.layout.recycle_layout, viewGroup, false)
        return ViewHolder(itemview, this)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val diary = filteredList[position]
        viewHolder.setItem(diary)
        viewHolder.itemView.isLongClickable = true
    }

    override fun getItemCount(): Int = items.size


    fun addItem(diary: Diary) = items.add(diary)
    fun removeItem() = items.clear()
    fun getItem(position: Int) : Diary = items[position]
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    override fun onItemClick(holder: ViewHolder?, view: View?, position: Int) {
        listener?.onItemClick(holder, view, position)
    }

    inner class ViewHolder(itemView: View, listener: OnItemClickListener?) :
        RecyclerView.ViewHolder(itemView) {
        val locale = Locale.getDefault().isO3Language
        var iconImageView: ImageView
        var textView: TextView
        var textView2: TextView
        var date: TextView
        var location: TextView
        fun setItem(diary: Diary) {
            Glide.with(itemView).load(diary.profile).into(iconImageView)
            textView.text = if (locale == "kor") {
                if (diary.location == " ") {
                    "${diary.where}에서.."
                } else {
                    "의 ${diary.where}에서.."
                }
            } else {
                if (diary.location == " ") {
                    "At a ${diary.where}"
                }
                else "${diary.where}"
            }

            textView2.text = diary.contents
            date.text = diary.date
            location.text = if (locale != "kor") {
                if (diary.location == " ") {
                    "At a ${diary.location},"
                } else {
                    "${diary.location}"
                }
            } else {
                "${diary.location}"
            }
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

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint.toString()
                filteredList = if (charString.isEmpty()) { //⑶
                    unFilteredList
                } else {
                    var filteringList = ArrayList<Diary>()
                    for (item in unFilteredList) {
                        if (item.date!!.substring(0, 11) == charString) filteringList.add(item)
                    }
                    filteringList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as ArrayList<Diary>
                notifyDataSetChanged()
            }
        }
    }

}
