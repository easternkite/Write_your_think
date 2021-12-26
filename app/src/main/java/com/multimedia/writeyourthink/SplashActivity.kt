package com.multimedia.writeyourthink

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.util.Log
import com.multimedia.writeyourthink.SplashActivity2
import com.multimedia.writeyourthink.SplashActivity3

class SplashActivity : Activity() {
    var activites = intArrayOf(1, 2)
    var actRand = 0
    override fun onCreate(savedInstanceStare: Bundle?) {
        super.onCreate(savedInstanceStare)
        actRand = (Math.random() * activites.size).toInt() + 1
        Log.d("Lee", "result :$actRand")
        when (actRand) {
            1 -> {
                val intent2 = Intent(this@SplashActivity, SplashActivity2::class.java)
                startActivity(intent2)
                finish()
            }
            2 -> {
                val intent = Intent(this@SplashActivity, SplashActivity3::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}