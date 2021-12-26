package com.multimedia.writeyourthink

import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import android.os.Bundle
import com.multimedia.writeyourthink.R
import android.os.Build
import android.view.WindowManager
import android.view.animation.Animation
import android.content.Intent
import android.view.animation.AnimationUtils
import com.multimedia.writeyourthink.LoginActivity
import com.multimedia.writeyourthink.databinding.ActivitySplash3Binding
class SplashActivity3 : AppCompatActivity() {
    private lateinit var binding : ActivitySplash3Binding
    var Splash_View: ConstraintLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplash3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            val animation = AnimationUtils.loadAnimation(
                applicationContext, R.anim.alpha
            ) //Context와 Animation xml파일
            animation.setAnimationListener(object : Animation.AnimationListener {
                //Animation Listener 순서대로 시작할때, 끝날때, 반복될때
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    try {
                        Thread.sleep(800) //2.5초간 화면 표시
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    val intent2 = Intent(this@SplashActivity3, LoginActivity::class.java)
                    intent2.putExtra("viewNum", 2)
                    startActivity(intent2)
                    overridePendingTransition(0, 0)
                    finish()
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            animation.isFillEnabled = false //애니메이션 이 끝난곳에 고정할지 아닐지
            binding.SplashView.startAnimation(animation) //애니메이션 시작
        }
    }
}