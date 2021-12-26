package com.multimedia.writeyourthink

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import com.multimedia.writeyourthink.BottomSheetFragment.BottomSheetListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.widget.TextView
import android.os.Bundle
import android.content.Intent
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import android.widget.Toast
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import android.os.Build
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.multimedia.writeyourthink.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity(), BottomSheetListener {
    private lateinit var binding: ActivityMainBinding
    private var backpressedTime: Long = 0
    private var fm: FragmentManager? = null
    private var ft: FragmentTransaction? = null
    private var bottomSheetFragment: BottomSheetFragment? = null
    private var frag1: Frag1? = null
    private var frag3: Frag3? = null
    private var userEmail: String? = null
    private var userProfile: String? = null
    private var userUID: String? = null
    private var userName: String? = null
    var sqLiteManager: SQLiteManager? = null
    private var database: FirebaseDatabase? = null
    private var databaseReference: DatabaseReference? = null
    private var fbLogin = 0


    /**
     * FireBase 등장
     */
    private var auth // 파이어 베이스 인증 객체
            : FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private val mTextView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fcm = Intent(applicationContext, MyFirebaseMessaging::class.java)
        startService(fcm)
        FirebaseApp.initializeApp( /*context=*/this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance()
        )
        tedPermission()
        val intent = intent
        val Token = intent.getStringExtra("accessToken")
        fbLogin = intent.getIntExtra("fbLogin", 0)
        /**
         * 파이어베이스 초기 셋팅
         */
        auth = FirebaseAuth.getInstance() // 파이어베이스 인증 객체 초기화.
        user = auth!!.currentUser
        userUID = user!!.uid
        userProfile = user!!.photoUrl.toString()
        userName = user!!.displayName
        userEmail = user!!.email
        database = FirebaseDatabase.getInstance() // 파이어베이스 데이터베이스 연동
        databaseReference = database!!.getReference(userUID!!) // DB 테이블 연결
        val photoUrl = "$userProfile?height=500&access_token=$Token"
        if (fbLogin == 1) {
            if (Locale.getDefault().isO3Language == "kor") {
                Toast.makeText(this, user!!.displayName + "님, 환영합니다!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "hello, " + user!!.displayName, Toast.LENGTH_SHORT).show()
            }
        } else if (fbLogin == 2) {
            sqLiteManager = SQLiteManager(this, "writeYourThink.db", null, 1)
            database = FirebaseDatabase.getInstance() // 파이어베이스 데이터베이스 연동
            databaseReference = database!!.getReference(userUID!!) // DB 테이블 연결
            databaseReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                    for (snapshot in dataSnapshot.children) { // 반복문으로 데이터 List를 추출해냄
                        val userInfo = snapshot.getValue(
                            UserInfo::class.java
                        ) // 만들어뒀던 User 객체에 데이터를 담는다.
                        if (userInfo!!.userName != null) {
                            userUID = userInfo.userUID
                            userName = userInfo.userName
                            userProfile = userInfo.userProfile
                            userEmail = userInfo.userEmail
                            sqLiteManager!!.insertUser2(userUID, userName, userProfile, userEmail)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // 디비를 가져오던중 에러 발생 시
                    Log.e("MainActivity", databaseError.toException().toString()) // 에러문 출력
                }
            })
            writeNewUser(userUID, userName, photoUrl, userEmail)
            fbLogin = 0
        }

        binding.bottomNavi.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_list -> {
                    setFrag(0)
                    item.isChecked = true
                }
                R.id.action_calendar -> {
                    item.isCheckable = false
                    setFrag(1)
                    setFrag(0)
                }
                R.id.action_chart -> setFrag(2)
            }
            true
        })
        frag1 = Frag1()
        bottomSheetFragment = BottomSheetFragment()
        frag3 = Frag3()
        setFrag(0)

        binding.button3.setOnClickListener(View.OnClickListener {
            val bottomSheet = BottomSheetFragment()
            bottomSheet.show(supportFragmentManager, "exampleBottomSheet")
        })
    }

    private fun setFrag(n: Int) {
        fm = supportFragmentManager
        ft = fm!!.beginTransaction()
        when (n) {
            0 -> {
                ft!!.replace(R.id.main_frame, frag1!!)
                ft!!.commit()
            }
            1 -> {
                val bottomSheet = BottomSheetFragment()
                bottomSheet.show(supportFragmentManager, "exampleBottomSheet")
            }
            2 -> {
                ft!!.replace(R.id.main_frame, frag3!!)
                ft!!.commit()
            }
        }
    }

    private fun tedPermission() {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                //권한요청성공
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {}
        }
        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setRationaleMessage(resources.getString(R.string.permission_3))
            .setDeniedMessage(resources.getString(R.string.permission_1))
            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            .check()
    }

    override fun onButtonClicked(text: String) {
        mTextView!!.text = text
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() > backpressedTime + 2000) {
            backpressedTime = System.currentTimeMillis()
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else if (System.currentTimeMillis() <= backpressedTime + 2000) {
            finish()
        }
    }

    fun writeNewUser(
        userUID: String?,
        userName: String?,
        userProfile: String?,
        userEmail: String?
    ) {
        val userInfo = UserInfo(userUID, userName, userProfile, userEmail)
        databaseReference!!.child("UserInfo").setValue(userInfo)
    }

    private fun changeLocale(localeLang: String) {
        var locale: Locale? = null
        when (localeLang) {
            "ko" -> locale = Locale("ko")
            "en" -> locale = Locale("en")
        }
        val config = applicationContext.resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale)
        } else {
            config.locale = locale
        }
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    companion object {
        private const val AD_UNIT_ID = "ca-app-pub-9450003299415787/4031932869"
        private const val TAG = "MyActivity"
    }
}