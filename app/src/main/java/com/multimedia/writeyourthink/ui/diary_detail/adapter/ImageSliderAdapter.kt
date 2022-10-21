package com.multimedia.writeyourthink.ui.diary_detail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.multimedia.writeyourthink.databinding.ItemDiaryImageBinding
import com.smarteist.autoimageslider.SliderViewAdapter

class ImageSliderAdapter(
    val urls: List<String>
): SliderViewAdapter<ImageSliderViewHolder>() {
    override fun getCount(): Int {
        return if (urls.size == 1) 0 else urls.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?): ImageSliderViewHolder {
        return ImageSliderViewHolder(ItemDiaryImageBinding.inflate(
            LayoutInflater.from(parent?.context),
            parent,
            false)
        )
    }

    override fun onBindViewHolder(viewHolder: ImageSliderViewHolder?, position: Int) {
        viewHolder?.bind(urls[position])
    }
}