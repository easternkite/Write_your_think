package com.multimedia.writeyourthink

import android.annotation.SuppressLint

import android.view.animation.AnimationSet
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import android.graphics.drawable.Drawable
import android.app.DatePickerDialog.OnDateSetListener
import android.view.animation.LayoutAnimationController
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import android.app.DatePickerDialog
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.view.animation.AlphaAnimation
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import com.bumptech.glide.Glide
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.multimedia.writeyourthink.databinding.Frag1Binding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Frag1 : Fragment(), BottomSheetDialogFragment.BottomSheetListener {
    private lateinit var binding: Frag1Binding
    private lateinit var diaryCollectionRef: CollectionReference
    val set = AnimationSet(true)
    var sqLiteManager: SQLiteManager? = null
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
    private var arrayList: ArrayList<Diary>? = null
    private var database: FirebaseDatabase? = null
    private var auth // 파이어 베이스 인증 객체
            : FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var databaseReference: DatabaseReference? = null
    private val idIndicator = ArrayList<String>()
    private val matchtitle = ArrayList<String>()
    private val matchdate = ArrayList<String>()
    private val matchtime = ArrayList<String>()
    private val matchProfile = ArrayList<String>()
    private val matchContents = ArrayList<String>()
    private val matchAddress = ArrayList<String?>()
    private val matchID = ArrayList<String>()
    private var date: String? = null
    var drawable: Drawable? = null

    //리사이클러뷰 등장
    var diaryAdapter: DiaryAdapter? = null
    val words = arrayOf("수정", "삭제")
    var myCalendar = Calendar.getInstance()
    var myDatePicker = OnDateSetListener { view, year, month, dayOfMonth ->
        myCalendar[Calendar.YEAR] = year
        myCalendar[Calendar.MONTH] = month
        myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        date = "$year/$month/$dayOfMonth"
        updateLabel()
        //updateList()
        val controller = LayoutAnimationController(set, 0.17f)
        binding.rv.layoutAnimation = controller
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = Frag1Binding.inflate(inflater, container, false) // view binding
        var fblogin = 0
        val intent = requireActivity().intent
        val Token = intent.getStringExtra("accessToken")
        fblogin = intent.getIntExtra("fbLogin", 0)
        auth = FirebaseAuth.getInstance() // 파이어베이스 인증 객체 초기화.
        user = auth!!.currentUser
        userName = user!!.uid
        diaryCollectionRef = Firebase.firestore.collection(userName!!)
        subscribeToRealtimeUpdates()
        Thread(r).start()
        binding.rv.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(context, 1)
        binding.rv.layoutManager = layoutManager
        arrayList = ArrayList() // User 객체를 담을 어레이 리스트 (어댑터쪽으로)
        diaryAdapter = DiaryAdapter()
        diaryAdapter!!.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(holder: DiaryAdapter.ViewHolder?, view: View?, position: Int) {
                AlertDialog.Builder(context).setItems(words) { dialog, which ->
                    val diary = diaryAdapter!!.getItem(position)
                    when (which) {
                        0 -> {
                            val args = Bundle()
                            args.putString("where", diary.where)
                            args.putString("contents", diary.contents)
                            args.putString("url", diary.profile)
                            args.putString("date", diary.date)
                            args.putString("time", diary.date?.substring(11, 19))
                            args.putString("address", diary.location)
                            args.putString("id", diary.userUID)

                            val bottomSheet = BottomSheetDialogFragment()
                            bottomSheet.arguments = args
                            bottomSheet.show(fragmentManager!!, "BS")
                        }
                        1 -> {
                            diaryDelete(diary)
                        }
                    }
                }.show()
            }
        })
        binding.tvDate.setOnClickListener {
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
            binding.tvDate.text = dayDown
            //updateList()
            val controller = LayoutAnimationController(set, 0.17f)
            binding.rv.layoutAnimation = controller
        })
        binding.DateUp.setOnClickListener(View.OnClickListener {
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
            binding.tvDate.text = dayUp
            //updateList()
            val controller = LayoutAnimationController(set, 0.17f)
            binding.rv.layoutAnimation = controller
        })
        val rtl: Animation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, -1f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, -1f,
            Animation.RELATIVE_TO_SELF, 0f
        )
        rtl.duration = 500
        set.addAnimation(rtl)
        val alpha: Animation = AlphaAnimation(0f, 1f)
        alpha.duration = 700
        set.addAnimation(alpha)
        val controller = arrayOf(LayoutAnimationController(set, 0.17f))
        binding.rv.layoutAnimation = controller[0]
        /**
         * SQLite 제어 설정
         */
        // SQLite 객체 초기화
        //sqLiteManager = SQLiteManager(requireActivity().applicationContext, "writeYourThink.db", null, 1)
        //Log.d("Lee", fblogin.toString() + "ㅁ낭ㄴ망ㅁ")
        return binding.root
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun subscribeToRealtimeUpdates() {
        Log.d("Lee", "${userName}}")
        diaryCollectionRef
            //.whereEqualTo("userUID", "${userName}-${binding.tvDate.text}")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                firebaseFirestoreException?.let {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    Log.d("Lee", it.message.toString())
                    return@addSnapshotListener
                }
                querySnapshot?.let {
                    diaryAdapter?.removeItem()
                    for (document in it) {
                        val person = document.toObject<Diary>()

                        diaryAdapter?.addItem(
                            Diary(
                                person.userUID, person.profile,
                                person.where,
                                person.contents,
                                person.date,
                                person.location
                            )
                        )
                        binding.rv.adapter = diaryAdapter

                        diaryAdapter?.notifyDataSetChanged()
                    }
                }
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
        binding.tvDate.text = sdf.format(myCalendar.time)
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
    private fun diaryDelete(diary: Diary) = CoroutineScope(Dispatchers.IO).launch {
        try {
            diaryCollectionRef.document("${diary.date}").delete().await()
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "데이터 삭제 성공!", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun delete(position: Int) {
        if (matchtime.size > 0) {
            database = FirebaseDatabase.getInstance() // 파이어베이스 데이터베이스 연동
            databaseReference = database!!.getReference(userName!!) // DB 테이블 연결
            databaseReference!!.child(matchdate[position] + "(" + matchtime[position] + ")")
                .setValue(null)
        }
    }
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
                        with1 = diary.where
                        contents1 = diary.contents
                        profile1 = diary.profile
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
                updateList()
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