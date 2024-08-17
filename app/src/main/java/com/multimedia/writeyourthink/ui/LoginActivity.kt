package com.multimedia.writeyourthink.ui

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.facebook.CallbackManager
import android.os.Bundle
import android.content.Intent
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.Auth
import com.facebook.FacebookCallback
import com.facebook.login.LoginResult
import com.facebook.FacebookException
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.facebook.AccessToken
import com.google.firebase.auth.FacebookAuthProvider
import com.google.android.gms.common.SignInButton
import android.widget.TextView
import com.google.android.gms.ads.MobileAds
import com.multimedia.writeyourthink.R
import com.multimedia.writeyourthink.databinding.ActivityLoginBinding
import com.multimedia.writeyourthink.ui.fragments.DiaryListFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {
    private lateinit var binding: ActivityLoginBinding
    @Inject
    lateinit var auth: FirebaseAuth

    var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    // 페이스북 콜백 매니저
    @Inject
    lateinit var callbackManager: CallbackManager

    // 파이어베이스 인증 객체 생성
    lateinit var googleApiClient: GoogleApiClient
    lateinit var accessToken: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        callbackManager = CallbackManager.Factory.create()
        val intent2 = intent
        val viewNum = intent2.getIntExtra("viewNum", 1)
        val w = window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        if (viewNum == 1) {
            binding.loginView.background = getDrawable(R.drawable.write_your_think_1)
        } else if (viewNum == 2) {
            binding.loginView.background = getDrawable(R.drawable.write_your_think_2)
        }
        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.alpha)
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
            .build()

        animation.isFillEnabled = false //애니메이션 이 끝난곳에 고정할지 아닐지
        binding.btnGoogle.startAnimation(animation) //애니메이션 시작
        binding.btnGoogle.setOnClickListener{
                // 구글 로그인 버튼을 클릭했을 때 이곳을 수행.
                val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
                startActivityForResult(intent, REQ_SIGN_GOOGLE)
            }
        /** 만약 이미 로그인이 되어있는 상태라면?
         * 바로 메인액티비티로 간다(자동 로그인)  */
        if (user != null) {
            val intent = Intent(applicationContext, DiaryActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnFacebook.startAnimation(animation)
        binding.btnFacebook.setReadPermissions("email", "public_profile")
        binding.btnFacebook.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                handleFacebookAccessToken(loginResult.accessToken)
                accessToken = loginResult.accessToken.token
            }

            override fun onCancel() {}
            override fun onError(error: FacebookException) {}
        })
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) { // 구글 로그인 인증을 요청 했을 때 결과 값을 되돌려 받는 곳.
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_SIGN_GOOGLE) {
            data ?: return
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result != null) {
                if (result.isSuccess) { // 인증결과가 성공적이면..
                    val account =
                        result?.signInAccount // account 라는 데이터는 구글로그인 정보를 담고있습니다. (닉네임,프로필사진Url,이메일주소...등)
                    resultLogin(account) // 로그인 결과 값 출력 수행하라는 메소드
                }
            }
        } else if (requestCode == REQ_SIGN_FACEBOOK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // 구글 로그인 성공
                val account = task.getResult(ApiException::class.java)
                //firebaseAuthWithGoogle(account);
            } catch (e: ApiException) {
                Toast.makeText(this, "Sign in Error Occured: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun resultLogin(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        auth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) { // 로그인이 성공했으면...
                    val intent = Intent(applicationContext, DiaryActivity::class.java)
                    startActivity(intent)
                    finish()
                } else { // 로그인이 실패했으면..
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.login_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {}

    // 페이스북 로그인 이벤트
    // 사용자가 정상적으로 로그인한 후 페이스북 로그인 버튼의 onSuccess 콜백 메소드에서 로그인한 사용자의
    // 액세스 토큰을 가져와서 Firebase 사용자 인증 정보로 교환하고,
    // Firebase 사용자 인증 정보를 사용해 Firebase에 인증.
    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 로그인 성공
                    val intent = Intent(applicationContext, DiaryActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // 로그인 실패
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.login_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    companion object {
        private const val REQ_SIGN_GOOGLE = 100 // 구글 로그인 결과 코드
        private const val REQ_SIGN_FACEBOOK = 200 // 페이스북 로그인 결과 코드
    }
}