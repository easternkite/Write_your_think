package com.multimedia.writeyourthink.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Build
import android.view.WindowManager
import android.view.animation.Animation
import android.content.Intent
import android.view.animation.AnimationUtils
import com.multimedia.writeyourthink.R
import com.multimedia.writeyourthink.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 스플래시 액티비티를 랜덤 교체
        val actRand = (Math.random() * 2).toInt() + 1
        when (actRand) {
            1 -> binding.SplashView.setBackgroundResource(R.drawable.write_your_think_1)
            2 -> binding.SplashView.setBackgroundResource(R.drawable.write_your_think_2)
        }
        binding.SplashView
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
                    val intent2 = Intent(this@SplashActivity, LoginActivity::class.java)
                    intent2.putExtra("viewNum", actRand)
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