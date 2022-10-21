package com.multimedia.writeyourthink.ui.diary_detail.adapter

import com.bumptech.glide.Glide
import com.multimedia.writeyourthink.R
import com.multimedia.writeyourthink.databinding.ItemDiaryImageBinding
import com.smarteist.autoimageslider.SliderViewAdapter

class ImageSliderViewHolder(
    val binding: ItemDiaryImageBinding
    ): SliderViewAdapter.ViewHolder(binding.root) {

    fun bind(url: String) {
        Glide.with(binding.root)
            .load(url)
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .into(binding.ivAutoImageSlider)
    }
}