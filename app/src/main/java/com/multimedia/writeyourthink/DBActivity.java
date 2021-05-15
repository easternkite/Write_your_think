package com.multimedia.writeyourthink;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DBActivity extends AppCompatActivity {


    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String date;
    private String location;
    private String with;
    private String profile;
    private String userUID;
    private String contents;
    private String userName;
    public SQLiteManager sqLiteManager;
    private FirebaseAuth auth; // 파이어 베이스 인증 객체
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbactivity);

        auth = FirebaseAuth.getInstance(); // 파이어베이스 인증 객체 초기화.
        user = auth.getCurrentUser();
        userName = user.getUid();


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


                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 디비를 가져오던중 에러 발생 시
                Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });



    }public void writeNewUser(String userId, String prifile, String title,String contents,String date, String location) {
        Diary diary = new Diary("Master", prifile, title, contents, date, location);

        databaseReference.child(userId).setValue(diary);
    }
}
