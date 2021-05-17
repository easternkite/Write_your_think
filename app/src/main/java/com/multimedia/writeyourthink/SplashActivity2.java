package com.multimedia.writeyourthink;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class SplashActivity2 extends AppCompatActivity {
    ConstraintLayout Splash_View;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash2);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            Splash_View = findViewById(R.id.Splash_View);
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha); //Context와 Animation xml파일
            animation.setAnimationListener(new Animation.AnimationListener() {  //Animation Listener 순서대로 시작할때, 끝날때, 반복될때
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    try{
                        Thread.sleep(800); //2.5초간 화면 표시
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }

                    Intent intent2 = new Intent(SplashActivity2.this, LoginActivity.class);
                    intent2.putExtra("viewNum", 1);
                    startActivity(intent2);
                    overridePendingTransition(0, 0);
                    finish();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            animation.setFillEnabled(false);    //애니메이션 이 끝난곳에 고정할지 아닐지
            Splash_View.startAnimation(animation);    //애니메이션 시작



        }




    }

    private final class MyAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        public void onAnimationEnd(Animation animation) {
            Intent intent2 = new Intent(SplashActivity2.this, MainActivity.class);
            startActivity(intent2);
            finish();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}