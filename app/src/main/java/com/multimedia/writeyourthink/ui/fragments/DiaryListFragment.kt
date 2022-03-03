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
        viewModel.getData().observe(viewLifecycleOwner, Observer {  diary ->
            diaryAdapter.differ.submitList(diary)
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
                        val builder = AlertDialog.Builder(context)
                        builder.setPositiveButton("예") { dialog, which ->
                            viewModel.deleteDiary(diary)
                            Toast.makeText(
                                requireActivity().applicationContext,
                                "삭제 완료",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        builder.setCancelable(true)
                        builder.setNegativeButton("아니오", null)
                        builder.setTitle("데이터 삭제")
                        builder.setMessage("[ + ${diary.date?.substring(0, 10)} ] 데이터를 삭제하시겠습니까?")
                        builder.show()
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
    /*
    @SuppressLint("NotifyDataSetChanged")
    private fun updateList() {
        idIndicator.clear()
        matchtitle.clear()
        matchContents.clear()
        matchAddress.clear()
        matchProfile.clear()
        matchdate.clear()
        matchtime.clear()
        matchID.clear()
        diaryAdapter!!.removeItem() // ListView 내용 모두 삭제
        val array = sqLiteManager!!.getResult(binding.tvDate.text.toString()) // DB의 내용을 배열단위로 모두 가져온다
        try {
            val length = array.size // 배열의 길이
            for (idx in 0 until length) {  // 배열의 길이만큼 반복
                val `object` = array[idx] // json의 idx번째 object를 가져와서,
                val userName = `object`.getString("userName") // object 내용중 id를 가져와 저장.
                val id = `object`.getString("id") // object 내용중 id를 가져와 저장.
                val title = `object`.getString("title") // object 내용중 id를 가져와 저장.
                val contents = `object`.getString("contents") // object 내용중 contents를 가져와 저장.
                val profile = `object`.getString("profile") // object 내용중 profile를 가져와 저장.
                val date = `object`.getString("date") // object 내용중 date를 가져와 저장.
                val time = `object`.getString("time") // object 내용중 date를 가져와 저장.
                val address = `object`.getString("address")
                matchID.add(id)
                matchAddress.add(address)
                matchContents.add(contents)
                matchProfile.add(profile)
                matchdate.add(date)
                matchtime.add(time)
                idIndicator.add(id)
                matchtitle.add(title)
                if (Locale.getDefault().isO3Language == "eng") {
                    diaryAdapter!!.addItem(
                        Diary(
                            userName, profile,
                            "At a $title, $address",
                            contents,
                            date.substring(0, 4) + "-" + date.substring(5, 7) + "-" +
                                    date.substring(8) + "-" + "(" + time + ")", ""
                        )
                    )
                    binding.rv.adapter = diaryAdapter
                } else {
                    // 저장한 내용을 토대로 ListView에 다시 그린다.
                    diaryAdapter!!.addItem(
                        Diary(
                            userName, profile,
                            if (address == " " || address == null) title + "에서.." else "의 " + title + "에서..",
                            contents,
                            date.substring(0, 4) + "년 " + date.substring(5, 7) + "월 " +
                                    date.substring(8) + "일" + "(" + time + ")", address
                        )
                    )
                    binding.rv.adapter = diaryAdapter
                }
            }
        } catch (e: Exception) {
            Log.i("seo", "error : $e")
        }
        diaryAdapter!!.notifyDataSetChanged()
    }

     */

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

    override fun onButtonClicked(text: String?) {}

    /*
    private fun firebaseUpdate() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setTitle(getString(R.string.syncData))
        progressDialog.setCancelable(false)
        progressDialog.show()
        /** SQLite DB 선언  */
        sqLiteManager = SQLiteManager(context, "writeYourThink.db", null, 1)
        database = FirebaseDatabase.getInstance()
        /** 파이어베이스 데이터베이스 연동  */
        databaseReference = database!!.getReference(userName!!)
        /** DB 테이블 연결  */
        databaseReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                /** 파이어베이스 데이터베이스의 데이터를 받아오는 곳  */
                for (snapshot in dataSnapshot.children) {
                    /** 반복문으로 데이터 List를 추출해냄  */
                    val diary = snapshot.getValue(Diary::class.java)
                    /** 만들어뒀던 User 객체에 데이터를 담는다.  */
                    if (diary!!.date != null) {
                        date = diary.date
                        location1 = diary.location
                        with1 = diary.place
                        contents1 = diary.contents
                        profile1 = diary.uploadedPictureUrl
                        userUID1 = diary.userUID
                        /** Firebase DB 데이터를 불러오자마자 바로 SQLite DB에 삽입  */
                        sqLiteManager!!.insert2(
                            userUID1,
                            with1,
                            contents1,
                            profile1,
                            date!!.substring(0, 10),
                            date!!.substring(11, 19),
                            if (location1 == " " || location1 == null) " " else location1
                        )
                    }
                }
                val controller = LayoutAnimationController(set, 0.17f)
                binding.rv.layoutAnimation = controller
                //updateList()
                progressDialog.dismiss()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 디비를 가져오던중 에러 발생 시
                Log.e("MainActivity", databaseError.toException().toString()) // 에러문 출력
            }
        })
    }

     */

    companion object {
        private const val REQUEST_CODE = 0
    }
}