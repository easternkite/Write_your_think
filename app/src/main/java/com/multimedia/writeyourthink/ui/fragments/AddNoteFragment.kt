package com.multimedia.writeyourthink.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import com.google.android.material.transition.MaterialContainerTransform
import com.multimedia.writeyourthink.R
import com.multimedia.writeyourthink.Util.getDiaryActivity
import com.multimedia.writeyourthink.Util.themeColor
import com.multimedia.writeyourthink.databinding.FragmentAddNoteBinding

class AddNoteFragment: Fragment(R.layout.fragment_add_note) {

    private var _binding: FragmentAddNoteBinding? = null
    val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddNoteBinding.inflate(layoutInflater, container, false)
        initToolbar()
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = 300L
            scrimColor = Color.TRANSPARENT
            containerColor = requireContext().themeColor(R.attr.colorSurface)
            startContainerColor = requireContext().themeColor(R.attr.colorSurface)
            endContainerColor = requireContext().themeColor(R.attr.colorSurface)
        }
        return binding.root
    }

    private fun initToolbar() {
        binding.toolbar.inflateMenu(R.menu.top_menu)
        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.isTitleCentered = true
        binding.toolbar.menu.removeItem(R.id.action_edit)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_add -> {
                    Toast.makeText(activity, "add item successfully.", Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                    true
                }
                else -> false
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}