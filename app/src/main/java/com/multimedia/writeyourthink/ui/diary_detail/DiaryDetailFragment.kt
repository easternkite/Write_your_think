package com.multimedia.writeyourthink.ui.diary_detail

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialContainerTransform
import com.multimedia.writeyourthink.R
import com.multimedia.writeyourthink.databinding.FragmentDiaryDetailBinding

class DiaryDetailFragment : Fragment() {

    private var _binding: FragmentDiaryDetailBinding? = null
    val binding get() = _binding!!

    val args: DiaryDetailFragmentArgs by navArgs()

    private val adapter by lazy {
        Log.d("LEE", args.diary.profile.toString())

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = 300L
            scrimColor = Color.TRANSPARENT
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiaryDetailBinding.inflate(layoutInflater, container, false)
        initToolbar()
        binding.diary = args.diary

        return binding.root
    }

    private fun initToolbar() {
        binding.toolbar.apply {
            isTitleCentered = true
            title = "Diary"
            inflateMenu(R.menu.top_menu)
            menu.removeItem(R.id.action_add)
            setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            setOnMenuItemClickListener { menu ->
                when (menu.itemId) {
                    R.id.action_edit -> {
                        val action =
                            DiaryDetailFragmentDirections.actionDiaryDetailFragmentToAddNoteFragment(
                                args.diary
                            )
                        findNavController().navigate(action)
                        true
                    }
                    else -> true
                }
            }
        }
    }
}