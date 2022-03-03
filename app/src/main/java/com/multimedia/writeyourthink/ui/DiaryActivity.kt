package com.multimedia.writeyourthink.ui

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
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
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.multimedia.writeyourthink.*
import com.multimedia.writeyourthink.databinding.ActivityMainBinding
import com.multimedia.writeyourthink.models.UserInfo
import com.multimedia.writeyourthink.repositories.DiaryRepository
import com.multimedia.writeyourthink.services.MyFirebaseMessaging
import com.multimedia.writeyourthink.ui.fragments.BottomSheetDialogFragment
import com.multimedia.writeyourthink.ui.fragments.DiaryListFragment
import com.multimedia.writeyourthink.ui.fragments.CalendarFragment
import com.multimedia.writeyourthink.viewmodels.DiaryViewModel
import com.multimedia.writeyourthink.viewmodels.DiaryViewModelProviderFactory
import java.util.*

class DiaryActivity : AppCompatActivity(), BottomSheetDialogFragment.BottomSheetListener {
    private lateinit var binding: ActivityMainBinding
    private var backpressedTime: Long = 0
    private var fm: FragmentManager? = null
    private var ft: FragmentTransaction? = null
    private var bottomSheetFragment: BottomSheetDialogFragment? = null
    private var diaryListFragment: DiaryListFragment? = null
    private var calendarFragment: CalendarFragment? = null
    private var userEmail: String? = null
    private var userProfile: String? = null
    private var userUID: String? = null
    private var userName: String? = null
    private var database: FirebaseDatabase? = null
    private lateinit var databaseReference: DatabaseReference
    private var isSignedIn = 0


    /**
     * FireBase 등장
     */
    private var auth // 파이어 베이스 인증 객체
            : FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private val mTextView: TextView? = null

    lateinit var viewModel: DiaryViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.diaryNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavi.setupWithNavController(navController)
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
        isSignedIn = intent.getIntExtra("fbLogin", 0)
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


        val firebaseRepository = DiaryRepository(databaseReference)
        val viewModelProviderFactory = DiaryViewModelProviderFactory(firebaseRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(DiaryViewModel::class.java)


        val photoUrl = "$userProfile?height=500&access_token=$Token"
        if (isSignedIn == 1) {
            if (Locale.getDefault().isO3Language == "kor") {
                Toast.makeText(this, user!!.displayName + "님, 환영합니다!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "hello, " + user!!.displayName, Toast.LENGTH_SHORT).show()
            }
        } else if (isSignedIn == 2) {
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
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // 디비를 가져오던중 에러 발생 시
                    Log.e("MainActivity", databaseError.toException().toString()) // 에러문 출력
                }
            })
            val userInfo = UserInfo(userUID, userName, photoUrl, userEmail)
            viewModel.saveUser(userInfo)
            isSignedIn = 0
        }

        binding.button3.setOnClickListener(View.OnClickListener {
            // Dialog창 중복 실행 방지를 위한 싱글톤 패턴 적용
            var bottomSheet : BottomSheetDialogFragment? = null
            if (bottomSheet == null) {
                bottomSheet = BottomSheetDialogFragment()
            }
            bottomSheet.show(supportFragmentManager, "exampleBottomSheet")
        })
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

    override fun onButtonClicked(text: String?) {
        mTextView!!.text = text
    }



    companion object {
        private const val AD_UNIT_ID = "ca-app-pub-9450003299415787/4031932869"
        private const val TAG = "MyActivity"
    }
}