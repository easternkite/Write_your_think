package com.multimedia.writeyourthink;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SplashActivity  extends Activity {

    int[] activites = new int[] {1,2};
    int actRand = 0;

    @Override
    protected void onCreate(Bundle savedInstanceStare) {
        super.onCreate(savedInstanceStare);




        actRand = (int) (Math.random() * activites.length)+1;
        Log.d("Lee", "result :" + actRand);
        switch (actRand) {
            case 1 :
                Intent intent2 = new Intent(SplashActivity.this, SplashActivity2.class);
                startActivity(intent2);
                finish();
                break;
            case 2 :
                Intent intent = new Intent(SplashActivity.this, SplashActivity3.class);
                startActivity(intent);
                finish();
                break;

        }
    }
}