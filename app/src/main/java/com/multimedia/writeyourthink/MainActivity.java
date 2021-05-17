package com.multimedia.writeyourthink;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
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

import com.facebook.AccessToken;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;


public class MainActivity extends AppCompatActivity implements BottomSheetFragment.BottomSheetListener {

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
        tedPermission();

        Intent intent = getIntent();
        String Token = intent.getStringExtra("accessToken");
        String fbLogin = intent.getStringExtra("fbLogin");
        /**
         * 파이어베이스 초기 셋팅
         */
        auth = FirebaseAuth.getInstance(); // 파이어베이스 인증 객체 초기화.
        user = auth.getCurrentUser();
        userUID = user.getUid();



        Log.d("Lee", String.valueOf(Token));
        accessToken = AccessToken.getCurrentAccessToken();
        Log.d("Lee", "LoginNum : " + loginNum);
        userProfile = user.getPhotoUrl().toString();
        userName = user.getDisplayName();
        userEmail = user.getEmail();
        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference = database.getReference(userUID); // DB 테이블 연결
        String photoUrl = userProfile + "?height=500&access_token=" + Token;
        sqLiteManager = new SQLiteManager(this, "writeYourThink.db", null, 1);
        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference = database.getReference(userUID); // DB 테이블 연결
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { // 반복문으로 데이터 List를 추출해냄
                    UserInfo userInfo = snapshot.getValue(UserInfo.class); // 만들어뒀던 User 객체에 데이터를 담는다.
                    if (userInfo.getUserName() != null){
                        userUID = userInfo.getUserUID();
                        userName = userInfo.getUserName();
                        userProfile = userInfo.getUserProfile();;
                        userEmail = userInfo.getUserEmail();

                        sqLiteManager.insertUser2(userUID,userName,userProfile,userEmail);


                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 디비를 가져오던중 에러 발생 시
                Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        writeNewUser(userUID,userName,photoUrl,userEmail);
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

        Bundle bundle = new Bundle();
        bundle.putString("text", user.getUid());
        bottomSheetFragment.setArguments(bundle);
        frag3.setArguments(bundle);

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
}
