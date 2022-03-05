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
import com.bumptech.glide.Glide
import android.view.View
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.gamingservices.GameRequestDialog.show
import com.google.android.material.snackbar.Snackbar
import com.multimedia.writeyourthink.*
import com.multimedia.writeyourthink.Util.Constants.Companion.DOWN
import com.multimedia.writeyourthink.Util.Constants.Companion.UP
import com.multimedia.writeyourthink.adapters.DiaryAdapter
import com.multimedia.writeyourthink.databinding.FragmentDiaryListBinding
import com.multimedia.writeyourthink.ui.DiaryActivity
import com.multimedia.writeyourthink.viewmodels.DiaryViewModel
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class DiaryListFragment : Fragment(R.layout.fragment_diary_list),
    BottomSheetDialogFragment.BottomSheetListener {
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
    private lateinit var viewModel: DiaryViewModel

    //리사이클러뷰 등장
    var myCalendar = Calendar.getInstance()
    var myDatePicker = OnDateSetListener { view, year, month, dayOfMonth ->
        myCalendar[Calendar.YEAR] = year
        myCalendar[Calendar.MONTH] = month
        myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        date = "$year/$month/$dayOfMonth"
        updateLabel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiaryListBinding.inflate(inflater, container, false) // view binding
        viewModel = (activity as DiaryActivity).viewModel
        setRecyclerView()

        viewModel.selectedDateTime.observe(viewLifecycleOwner) {
            viewModel.setFilter()
            binding.tvDateAndTime.text = it
        }
        viewModel.filteredList.observe(viewLifecycleOwner) {
            diaryAdapter.differ.submitList(it)
        }
        viewModel.diaryData.observe(viewLifecycleOwner, Observer { diary ->
            // 데이터가 변경되면 filterlist를 바꿔주어야한다.
            viewModel.setDate(binding.tvDateAndTime.text.toString())
            viewModel.setFilter()
            hideProgressBar()
        })

        Thread(r).start()

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
        diaryAdapter.setOnItemClickListener { diary ->
            val args = Bundle().apply {
                putParcelable("diary", diary)
            }
            val bottomSheet = BottomSheetDialogFragment()
            bottomSheet.arguments = args
            bottomSheet.show(requireFragmentManager(), "BS")
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
        updateLabel()
        binding.DateDown.setOnClickListener(View.OnClickListener {
            dateUpDown(DOWN)
        })
        binding.DateUp.setOnClickListener {
            dateUpDown(UP)
        }



        return binding.root
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

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun updateLabel() {
        if (viewModel.selectedDateTime.value.isNullOrEmpty()) {
            viewModel.setDate(sdf.format(myCalendar.time))
        }
    }

    var r = Runnable {
        while (true) {
            try {
                Thread.sleep(1000)
            } catch (e: Exception) {
            }
            if (activity != null) {
                requireActivity().runOnUiThread { time = sdf2.format(Date()) }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 0
    }

    override fun onButtonClicked(text: String?) {

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