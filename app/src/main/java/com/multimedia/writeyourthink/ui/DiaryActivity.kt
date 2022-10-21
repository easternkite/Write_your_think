package com.multimedia.writeyourthink.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.FirebaseUser
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.multimedia.writeyourthink.*
import com.multimedia.writeyourthink.R
import com.multimedia.writeyourthink.databinding.ActivityMainBinding
import com.multimedia.writeyourthink.models.UserInfo
import com.multimedia.writeyourthink.services.MyFirebaseMessaging
import com.multimedia.writeyourthink.ui.fragments.BottomSheetDialogFragment
import com.multimedia.writeyourthink.viewmodels.DiaryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class DiaryActivity : AppCompatActivity(), BottomSheetDialogFragment.BottomSheetListener {
    private lateinit var binding: ActivityMainBinding
    private var isSignedIn = 0

    val viewModel: DiaryViewModel by viewModels()

    @Inject
    lateinit var user: FirebaseUser

    @Inject
    lateinit var safetyNetAppCheckProviderFactory: SafetyNetAppCheckProviderFactory

    @Inject
    lateinit var firebaseAppCheck: FirebaseAppCheck

    private var mInterstitialAd: InterstitialAd? = null
    private val TAG = "DiaryActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNav()
        MobileAds.initialize(this)


        var adRequest = AdRequest.Builder().build()
        val test = Arrays.asList("8D3429159CBA267C445F2273BB6CE315")
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(test).build()
        MobileAds.setRequestConfiguration(configuration)
        InterstitialAd.load(this,"ca-app-pub-9450003299415787/4031932869", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError.message)
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })

        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad was dismissed.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.d(TAG, "Ad failed to show.")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed fullscreen content.")
                mInterstitialAd = null;
            }
        }
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.")
        }

        val fcm = Intent(applicationContext, MyFirebaseMessaging::class.java)
        startService(fcm)
        binding.adView.loadAd(adRequest)
        firebaseAppCheck.installAppCheckProviderFactory(
            safetyNetAppCheckProviderFactory
        )
        tedPermission()
        val intent = intent
        val Token = intent.getStringExtra("accessToken")
        isSignedIn = intent.getIntExtra("fbLogin", 0)


        val userInfo = UserInfo(user.uid, user.displayName, user.photoUrl.toString(), user.email)
        viewModel.saveUser(userInfo)

        if (viewModel.selectedDateTime.value.isNullOrEmpty()) {
            if (Locale.getDefault().isO3Language == "kor") {
                Toast.makeText(this, user!!.displayName + "님, 환영합니다!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "hello, " + user!!.displayName, Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun initNav() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.diaryNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavi.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.diaryListFragment -> showHideBottomBar(true)
                R.id.calendarFragment -> showHideBottomBar(true)
                R.id.addNoteFragment -> showHideBottomBar(false)
                R.id.diaryDetailFragment -> showHideBottomBar(false)
            }
        }
    }
    fun showHideBottomBar(isShow: Boolean) {
        val visibility = if (isShow) View.VISIBLE else View.GONE
        binding.bottomNavi.visibility = visibility
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
        binding.textViewButtonClicked.text = text
    }
}