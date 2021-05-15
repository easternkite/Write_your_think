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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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


public class MainActivity extends AppCompatActivity implements BottomSheetFragment.BottomSheetListener{



    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private BottomSheetFragment bottomSheetFragment;
    private Frag1 frag1;
    private Frag3 frag3;
    private String date;
    private String location;
    private String with;
    private String profile;
    private String userUID;
    private String contents;
    private String userName;
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

        auth = FirebaseAuth.getInstance(); // 파이어베이스 인증 객체 초기화.
        user = auth.getCurrentUser();
        userName = user.getUid();

        firebaseUpdate();
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

        button= findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetFragment bottomSheet = new BottomSheetFragment();
                bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
            }
        });

        Bundle bundle =  new Bundle();
        bundle.putString("text", user.getUid());
        bottomSheetFragment.setArguments(bundle);
        frag3.setArguments(bundle);

    }

    private void setFrag(int n){
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n){
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
    private void firebaseUpdate(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("데이터 동기화중...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        sqLiteManager = new SQLiteManager(getApplicationContext(), "writeYourThink123.db", null, 1);




        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference = database.getReference(userName); // DB 테이블 연결
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sqLiteManager.deleteAll();
                // 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { // 반복문으로 데이터 List를 추출해냄
                    Diary diary = snapshot.getValue(Diary.class); // 만들어뒀던 User 객체에 데이터를 담는다.
                    if (diary.getDate() != null){
                        date = diary.getDate();
                        location = diary.getLocation();
                        with = diary.getWith();
                        contents = diary.getContents();
                        profile= diary.getProfile();
                        userUID= diary.getUserUID();


                        sqLiteManager.insert2(userUID,
                                with,
                                contents,
                                profile,
                                date.substring(0,10),
                                date.substring(11,19), location.equals(" ") || location.equals(null)?" ":location);
                        Log.d("Lee", date);
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