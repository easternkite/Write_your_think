package com.multimedia.writeyourthink;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.AccessToken;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements BottomSheetFragment.BottomSheetListener {
    private static final String AD_UNIT_ID = "ca-app-pub-9450003299415787/4031932869";
    private static final String TAG = "MyActivity";

    private long backpressedTime = 0;
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private BottomSheetFragment bottomSheetFragment;
    private Frag1 frag1;
    private Frag3 frag3;
    private AccessToken accessToken;
    private String userEmail;
    private String userProfile;
    private String userUID;;
    private String userName;
    private int loginNum;
    public SQLiteManager sqLiteManager;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private int fbLogin;
    /**
     * FireBase Setting
     */
    //SQL Setting
    private String date;
    private String location1;
    private String with1;
    private String profile1;
    private String userUID1;
    private String contents1;
    /**
     * FireBase 등장
     */
    private FirebaseAuth auth; // 파이어 베이스 인증 객체
    private FirebaseUser user;
    private TextView mTextView;
    private ImageButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(/*context=*/ this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());
        tedPermission();





        Intent intent = getIntent();
        String Token = intent.getStringExtra("accessToken");
        fbLogin = intent.getIntExtra("fbLogin",0);
        /**
         * 파이어베이스 초기 셋팅
         */
        auth = FirebaseAuth.getInstance(); // 파이어베이스 인증 객체 초기화.
        user = auth.getCurrentUser();
        userUID = user.getUid();
        if (fbLogin == 1){
            if (Locale.getDefault().getISO3Language().equals("kor")){
                Toast.makeText(this, user.getDisplayName() + "님, 환영합니다!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "hello, "+user.getDisplayName(), Toast.LENGTH_SHORT).show();
            }
        }else if(fbLogin == 2){
            firebaseUpdate();
            fbLogin = 0;
        }

        bottomNavigationView = findViewById(R.id.bottomNavi);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_list:
                        setFrag(0);
                        item.setChecked(true);

                        break;
                    case R.id.action_calendar:

                        item.setCheckable(false);
                        setFrag(1);
                        setFrag(0);

                        break;
                    case R.id.action_chart:
                        setFrag(2);
                        break;

                }
                return true;
            }
        });
        frag1 = new Frag1();
        bottomSheetFragment = new BottomSheetFragment();
        frag3 = new Frag3();
        setFrag(0);

        button = findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetFragment bottomSheet = new BottomSheetFragment();
                bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
            }
        });

    }

    private void setFrag(int n) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n) {
            case 0:
                ft.replace(R.id.main_frame, frag1);
                ft.commit();
                break;
            case 1:
                BottomSheetFragment bottomSheet = new BottomSheetFragment();
                bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
                break;
            case 2:
                ft.replace(R.id.main_frame, frag3);
                ft.commit();
                break;
        }
    }


    private void tedPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //권한요청성공
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {

            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_3))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    @Override
    public void onButtonClicked(String text) {
        mTextView.setText(text);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backpressedTime + 2000) {
            backpressedTime = System.currentTimeMillis();
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() <= backpressedTime + 2000) {
            finish();
        }
    }
    public void writeNewUser(String userUID, String userName , String userProfile, String userEmail) {
        UserInfo userInfo = new UserInfo(userUID, userName, userProfile, userEmail);

        databaseReference.child("UserInfo").setValue(userInfo);
    }

    private void changeLocale(String localeLang){

        Locale locale = null;

        switch (localeLang){
            case "ko":
                locale = new Locale("ko");
                break;

            case "en":
                locale = new Locale("en");
                break;
        }
        Configuration config = getApplicationContext().getResources().getConfiguration();

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ) {
            config.setLocale(locale);
        }
        else {
            config.locale = locale;
        }

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }


    private void firebaseUpdate(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.syncData));
        progressDialog.setCancelable(false);
        progressDialog.show();


        sqLiteManager = new SQLiteManager(this, "writeYourThink.db", null, 1);



        Log.d("Lee", "랄라랄ㄹㄹ: "+userUID);
        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference = database.getReference(userUID); // DB 테이블 연결
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { // 반복문으로 데이터 List를 추출해냄
                    Diary diary = snapshot.getValue(Diary.class); // 만들어뒀던 User 객체에 데이터를 담는다.
                    if (diary.getDate() != null){
                        date = diary.getDate();
                        location1 = diary.getLocation();
                        with1 = diary.getWhere();
                        contents1 = diary.getContents();
                        profile1= diary.getProfile();
                        userUID1= diary.getUserUID();


                        sqLiteManager.insert2(userUID1,
                                with1,
                                contents1,
                                profile1,
                                date.substring(0,10),
                                date.substring(11,19), location1.equals(" ") || location1.equals(null)?" ":location1);
                    }


                }

                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 디비를 가져오던중 에러 발생 시
                Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

    }
}
