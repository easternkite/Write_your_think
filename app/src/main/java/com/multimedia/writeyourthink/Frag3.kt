package com.multimedia.writeyourthink

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.github.sundeepk.compactcalendarview.CompactCalendarView.CompactCalendarViewListener
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.multimedia.writeyourthink.LoginActivity
import com.multimedia.writeyourthink.databinding.Frag3Binding
import java.text.SimpleDateFormat
import java.util.*

class Frag3 : Fragment() {
    private lateinit var binding: Frag3Binding
    private lateinit var sqLiteManager: SQLiteManager
    private val simpleDateFormat = SimpleDateFormat("MMMM-YYYY", Locale.getDefault())
    private val DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val text: String? = null
    private lateinit var myCalendar: Calendar

    /**
     * 데이터 관련
     */
    private var auth // 파이어 베이스 인증 객체
            : FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var userUID: String? = null
    var sdf: SimpleDateFormat? = null
    var c: Date? = null
    var df: SimpleDateFormat? = null
    var formattedDate: String? = null
    private val selectedDate = ArrayList<String>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = Frag3Binding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance() // 파이어베이스 인증 객체 초기화.
        user = auth!!.currentUser

        val intent = activity!!.intent
        val nickName = intent.getStringExtra("nickName") // MainActivity로 부터 닉네임 전달받음.
        val photoUrl = intent.getStringExtra("photoUrl") // MainActivity로 부터 프로필사진 Url 전달받음.
        sqLiteManager = SQLiteManager(activity, "writeYourThink.db", null, 1)
        userUID = user!!.uid
        updateUserInfo()
        updateList()

        calendarlistener()
        Setdate()
        binding.btnLogout.setOnClickListener {
            auth!!.signOut()
            LoginManager.getInstance().logOut()
            sqLiteManager!!.deleteAll()
            Toast.makeText(activity, getString(R.string.logout), Toast.LENGTH_SHORT).show()
            val intent1 = Intent(activity, LoginActivity::class.java)
            startActivity(intent1)
            activity!!.finish()
        }
        binding.text.text = simpleDateFormat.format(myCalendar!!.time)
        binding.layoutLeft!!.setOnClickListener {
            binding.compactcalendarView.showCalendarWithAnimation()
            binding.compactcalendarView.showNextMonth()
        }
        binding.layoutRight!!.setOnClickListener {
            binding.compactcalendarView.showCalendarWithAnimation()
            binding.compactcalendarView.showPreviousMonth()
        }
        return binding.root
    }


    fun calendarlistener() {
        binding.compactcalendarView.setListener(object : CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date) {
                val profile_counts =
                    sqLiteManager!!.getProfilesCount(DateFormat.format(dateClicked))
                sqLiteManager!!.close()
                binding.tvSelDate.text = DateFormat.format(dateClicked)
                if (profile_counts > 0) {
                    binding.tvCount.text =
                        getString(R.string.frag3_2) + " : " + profile_counts + " " + getString(R.string.cases)
                } else {
                    binding.tvCount.text = getString(R.string.frag3_1)
                }
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date) {
                binding.compactcalendarView.removeAllEvents()
                Setdate()
                binding.text.text = simpleDateFormat.format(firstDayOfNewMonth)
            }
        })
    }

    //get current date
    fun Setdate() {
        c = Calendar.getInstance().time
        df = SimpleDateFormat("yyyy-MM-dd")
        val profile_counts = sqLiteManager!!.getProfilesCount(DateFormat.format(c))
        sqLiteManager!!.close()
        binding.compactcalendarView.setUseThreeLetterAbbreviation(true)
        sdf = SimpleDateFormat("MMMM yyyy")
        formattedDate = df!!.format(c)
        binding.tvSelDate.text = formattedDate
        if (profile_counts > 0) {
            binding.tvCount.text =
                getString(R.string.frag3_2) + " : " + profile_counts + " " + getString(R.string.cases)
        } else {
            binding.tvCount.text = getString(R.string.frag3_1)
        }
        myCalendar = Calendar.getInstance()
        for (j in selectedDate.indices) {
            val mon = selectedDate[j].substring(5, 7).toInt()
            myCalendar.set(Calendar.YEAR, selectedDate[j].substring(0, 4).toInt())
            myCalendar.set(Calendar.MONTH, mon - 1)
            myCalendar.set(Calendar.DAY_OF_MONTH, selectedDate[j].substring(8).toInt())
            val event = Event(Color.RED, myCalendar.getTimeInMillis(), "test")
            binding.compactcalendarView.addEvent(event)
        }
    }

    private fun updateUserInfo() {
        val array = sqLiteManager!!.resultUser // DB의 내용을 배열단위로 모두 가져온다
        try {
            val length = array.size // 배열의 길이
            for (idx in 0 until length) {  // 배열의 길이만큼 반복
                val `object` = array[idx] // json의 idx번째 object를 가져와서,
                val userName = `object`.getString("userName") // object 내용중 name를 가져와 저장.
                val userProfile = `object`.getString("userProfile") // object 내용중 profile를 가져와 저장.

                // 저장한 내용을 토대로 ListView에 다시 그린다.
                binding.tvNickname.text = userName
                Glide.with(activity!!.applicationContext).load(userProfile.toString()).into(
                    binding.ivProfile
                )
            }
        } catch (e: Exception) {
            Log.i("seo", "error : $e")
        }
    }

    private fun updateList() {
        selectedDate.clear()
        val array = sqLiteManager!!.result2 // DB의 내용을 배열단위로 모두 가져온다
        try {
            val length = array.size // 배열의 길이
            for (idx in 0 until length) {                  // 배열의 길이만큼 반복
                val `object` = array[idx] // json의 idx번째 object를 가져와서,
                val date = `object`.getString("date") // object 내용중 date를 가져와 저장.
                selectedDate.add(date)
                // 저장한 내용을 토대로 ListView에 다시 그린다.
            }
        } catch (e: Exception) {
            Log.i("seo", "error : $e")
        }
    }
}