package com.multimedia.writeyourthink.ui.fragments

import android.view.animation.AnimationSet
import android.graphics.drawable.Drawable
import android.app.DatePickerDialog.OnDateSetListener
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import android.app.DatePickerDialog
import android.app.Activity
import android.app.AlertDialog
import android.util.Log
import com.bumptech.glide.Glide
import android.view.View
import android.widget.Toast
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.gamingservices.GameRequestDialog.show
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import com.multimedia.writeyourthink.*
import com.multimedia.writeyourthink.Util.Constants.Companion.DOWN
import com.multimedia.writeyourthink.Util.Constants.Companion.UP
import com.multimedia.writeyourthink.Util.getDiaryActivity
import com.multimedia.writeyourthink.adapters.DiaryAdapter
import com.multimedia.writeyourthink.databinding.FragmentDiaryListBinding
import com.multimedia.writeyourthink.models.Diary
import com.multimedia.writeyourthink.ui.DiaryActivity
import com.multimedia.writeyourthink.viewmodels.DiaryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DiaryListFragment : Fragment() {
    private var _binding: FragmentDiaryListBinding? = null
    private val binding get() = _binding!!

    val set = AnimationSet(true)
    private val myFormat = "yyyy-MM-dd" // 출력형식   2018/11/28
    private val sdf = SimpleDateFormat(myFormat, Locale.KOREA)
    private val sdf2 = SimpleDateFormat("HH:mm:ss", Locale.KOREA)
    private var time: String? = null

    /**
     * FireBase Setting
     */

    private var date: String? = null
    var drawable: Drawable? = null


    private lateinit var diaryAdapter: DiaryAdapter
    val viewModel: DiaryViewModel by activityViewModels()

    @Inject
    lateinit var myCalendar: Calendar

    var myDatePicker = OnDateSetListener { view, year, month, dayOfMonth ->
        myCalendar[Calendar.YEAR] = year
        myCalendar[Calendar.MONTH] = month
        myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        date = "$year/$month/$dayOfMonth"
        updateLabel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiaryListBinding.inflate(inflater, container, false) // view binding
        setRecyclerView()
        observeUiState()

        val cur = sdf.format(Date()).also {
            viewModel.setDate(it)
        }

        binding.fab.setOnClickListener {
            exitTransition = MaterialElevationScale(true)
            reenterTransition = MaterialElevationScale(true)
            val action = DiaryListFragmentDirections.actionDiaryListFragmentToAddNoteFragment(
                diary = Diary()
            )
            val extras = FragmentNavigatorExtras(it to "fab_transition")
            findNavController().navigate(
                directions = action,
                extras
            )
        }
        binding.rv.setHasFixedSize(true)

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val diary = diaryAdapter.differ.currentList[position]
                viewModel.deleteDiary(diary)
                Snackbar.make(requireView(), R.string.deleteData, Snackbar.LENGTH_LONG)
                    .apply {
                        viewModel.deleteDiary(diary)
                        setAction(R.string.undo) {
                            viewModel.saveDiary(diary)
                        }
                        show()
                    }
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rv)
        }
        val layoutManager = GridLayoutManager(context, 1)
        binding.rv.layoutManager = layoutManager

        diaryAdapter.setOnItemClickListener { cardView, diary ->
            exitTransition = MaterialElevationScale(false).apply {
                duration = 300L
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = 300L
            }
            val transitionName = getString(R.string.diary_detail_transition_name)
            val extras = FragmentNavigatorExtras(cardView to transitionName)
            val action =
                DiaryListFragmentDirections.actionDiaryListFragmentToDiaryDetailFragment(diary)
            findNavController().navigate(action, extras)

        }

        binding.tvDateAndTime.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                myDatePicker,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        } //달력 꺼내기

        binding.DateDown.setOnClickListener(View.OnClickListener {
            dateUpDown(DOWN)
        })
        binding.DateUp.setOnClickListener {
            dateUpDown(UP)
        }

        return binding.root
    }
    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { uiState ->

                    if (uiState.diaryList.isNotEmpty()) {
                        showHideProgressBar(false)
                    }

                    diaryAdapter.differ.submitList(uiState.filteredByDate)

                    if (uiState.errorMassege.isNotEmpty()) {
                        hideProgressBar()
                        Toast.makeText(requireContext(), "error occurred : ${uiState.errorMassege}", Toast.LENGTH_LONG).show()
                    }

                    uiState.selectedDateTime.let {
                        binding.tvDateAndTime.text = it
                        viewModel.setDate(it)
                    }

                    viewModel.setFilter(uiState.selectedDateTime)

                }
            }
        }
    }
    private fun dateUpDown(op: Int) {
        var day = sdf.format(myCalendar.time).replace("-", "")
        var dayInt = day.toInt()
        dayInt += op
        day = dayInt.toString()
        val sdfmt = SimpleDateFormat("yyyyMMdd")
        try {
            val date = sdfmt.parse(day)
            day = SimpleDateFormat("yyyy-MM-dd").format(date)
            myCalendar.time = date
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        viewModel.setDate(day)
    }

    private fun setRecyclerView() {
        diaryAdapter = DiaryAdapter()
        binding.rv.apply {
            adapter = diaryAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    val uri = data!!.data

                } catch (e: Exception) {
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    private fun showHideProgressBar(isVisible: Boolean) {
        binding.progressBar.isVisible = isVisible
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun updateLabel() {
        viewModel.setDate(sdf.format(myCalendar.time))
    }

    companion object {
        private const val REQUEST_CODE = 0
    }

    /**
     * Fragment에서 View Binding을 사용할 경우 Fragment는 View보다 오래 지속되어,
     * Fregment의 Lifecycle로 인해 메모리 누수가 발생할 수 있다.
     * 따라서 반드시 binding 변수를 onDestroyView() 이후에 null로 만들어 주어야한다.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}