package com.multimedia.writeyourthink.ui.diary_detail

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.multimedia.writeyourthink.databinding.FragmentDiaryDetailBinding
import com.multimedia.writeyourthink.ui.diary_detail.adapter.ImageSliderAdapter
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderView

class DiaryDetailFragment : Fragment() {

    private var _binding: FragmentDiaryDetailBinding? = null
    val binding get() = _binding!!

    val args: DiaryDetailFragmentArgs by navArgs()

    private val adapter by lazy {
        Log.d("LEE", args.urls.profile.toString())
        ImageSliderAdapter(
            listOf(
                args.urls.profile ?: "",
                "https://static.wikia.nocookie.net/pokemon/images/6/6c/Char-pikachu.png/revision/latest?cb=20190430034300",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTTIfRjkXA9Vaj71vkqPKpG97hrSvgC1HTNxekAOKmVAcTKampfHVOABDe_VfEcurT8bIs&usqp=CAU"
                )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiaryDetailBinding.inflate(layoutInflater, container, false)
        binding.imageSlider.apply {
            val list = listOf(
                args.urls.profile ?: "",

            )
            val adapter = ImageSliderAdapter(
                list
            )
            setSliderAdapter(adapter)
            setIndicatorAnimation(IndicatorAnimationType.WORM)
        }
        return binding.root
    }

}