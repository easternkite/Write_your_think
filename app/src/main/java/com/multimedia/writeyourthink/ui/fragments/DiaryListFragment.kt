package com.multimedia.writeyourthink.ui.fragments

import android.view.animation.AnimationSet
import android.graphics.drawable.Drawable
import android.app.DatePickerDialog.OnDateSetListener
import android.view.animation.LayoutAnimationController
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import android.app.DatePickerDialog
import android.app.Activity
import android.app.AlertDialog
import com.bumptech.glide.Glide
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.multimedia.writeyourthink.*
import com.multimedia.writeyourthink.adapters.DiaryAdapter
import com.multimedia.writeyourthink.databinding.FragmentDiaryListBinding
import com.multimedia.writeyourthink.ui.DiaryActivity
import com.multimedia.writeyourthink.viewmodels.DiaryViewModel
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class DiaryListFragment : Fragment(R.layout.fragment_diary_list), BottomSheetDialogFragment.BottomSheetListener {
    private var _binding: FragmentDiaryListBinding? = null
    private val binding get() = _binding!!

    val set = AnimationSet(true)
    private val myFormat = "yyyy-MM-dd" // 출력형식   2018/11/28
    private val sdf = SimpleDateFormat(myFormat, Locale.KOREA)
    private val sdf2 = SimpleDateFormat("HH:mm:ss", Locale.KOREA)
    private var time: String? = null
    private var location1: String? = null
    private var with1: String? = null
    private var profile1: String? = null
    private var userUID1: String? = null
    private var contents1: String? = null
    private var userName: String? = null

    /**
     * FireBase Setting
     */

    private var date: String? = null
    var drawable: Drawable? = null


    private lateinit var diaryAdapter: DiaryAdapter
    private lateinit var viewModel: DiaryViewModel
    //리사이클러뷰 등장
    val words = arrayOf("수정", "삭제")
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
        }
        viewModel.filteredList.observe(viewLifecycleOwner) {
            diaryAdapter.differ.submitList(it)
        }
        viewModel.getData().observe(viewLifecycleOwner, Observer {  diary ->
            // 데이터가 변경되면 filterlist를 바꿔주어야한다.
            viewModel.setDate(binding.tvDateAndTime.text.toString())
            viewModel.setFilter()
            //diaryAdapter.differ.submitList(diary!!)
            //diaryAdapter.differ.submitList(diary.filter { it.date.isNotEmpty() &&it.date.substring(0,10) == binding.tvDateAndTime.text.toString() })
        })

        var fblogin = 0
        val intent = requireActivity().intent
        val Token = intent.getStringExtra("accessToken")
        fblogin = intent.getIntExtra("fbLogin", 0)


        Thread(r).start()
        binding.rv.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(context, 1)
        binding.rv.layoutManager = layoutManager
        diaryAdapter.setOnItemClickListener { diary ->
            AlertDialog.Builder(context).setItems(words) { dialog, which ->
                when (which) {
                    0 -> {
                        val args = Bundle().apply {
                            putParcelable("diary", diary)
                        }
                        val bottomSheet = BottomSheetDialogFragment()
                        bottomSheet.arguments = args
                        bottomSheet.show(requireFragmentManager(), "BS")
                    }
                    1 -> {
                        Snackbar.make(requireView(), "데이터를 성공적으로 삭제했습니다.", Snackbar.LENGTH_LONG).apply {
                            viewModel.deleteDiary(diary)
                            setAction("복원") {
                                viewModel.saveDiary(diary)
                            }
                            show()
                        }
                    }
                }
            }.show()
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
            var dayDown = sdf.format(myCalendar.time).replace("-", "")
            var dayDownint = dayDown.toInt()
            dayDownint = dayDownint - 1
            dayDown = dayDownint.toString()
            val sdfmt = SimpleDateFormat("yyyyMMdd")
            try {
                val date = sdfmt.parse(dayDown)
                dayDown = SimpleDateFormat("yyyy-MM-dd").format(date)
                myCalendar.time = date
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            viewModel.setDate(dayDown)
            binding.tvDateAndTime.text = dayDown

        })
        binding.DateUp.setOnClickListener {
            var dayUp = sdf.format(myCalendar.time).replace("-", "")
            var dayUpint = dayUp.toInt()
            dayUpint = dayUpint + 1
            dayUp = dayUpint.toString()
            val sdfmt = SimpleDateFormat("yyyyMMdd")
            try {
                val date = sdfmt.parse(dayUp)
                dayUp = SimpleDateFormat("yyyy-MM-dd").format(date)
                myCalendar.time = date
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            viewModel.setDate(dayUp)
            binding.tvDateAndTime.text = dayUp
        }



        return binding.root
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
                    Glide.with(requireActivity().applicationContext).load(uri.toString()).into(binding.imageView)
                    binding.editUpload.setText(uri.toString())
                } catch (e: Exception) {
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    private fun updateLabel() {
        date = sdf.format(myCalendar.time)
        binding.tvDateAndTime.text = sdf.format(myCalendar.time)
        viewModel.setDate(sdf.format(myCalendar.time))
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
}