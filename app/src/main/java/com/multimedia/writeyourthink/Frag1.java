
package com.multimedia.writeyourthink;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static java.lang.String.valueOf;

public class Frag1 extends Fragment implements BottomSheetFragment.BottomSheetListener{
    private SharedViewModel sharedViewModel;
    private View view;
    final AnimationSet set = new AnimationSet(true);
    private Button btn_upload;// 업로드버튼
    private EditText edit_title, edit_contents, edit_upload;       // 입력받을 폼 3개(음식이름, 음식칼로리, 날짜)
    private SQLiteManager dbManager;                        // SQLite Class 관리용 객체
    private ListView listView;                              // DB에 저장된 내용을 보여주기위한 리스트뷰
    public SQLiteManager sqLiteManager;
    private String myFormat = "yyyy-MM-dd";    // 출력형식   2018/11/28
    private SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
    private SimpleDateFormat sdf3 = new SimpleDateFormat("EEEE" , Locale.KOREA);
    private SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
    private Button DateUp;
    private Button DateDown;
    private TextView textView;
    private String time;
    private String addressJin;
    private String weekDay;


    private String date1;
    private String location1;
    private String with1;
    private String profile1;
    private String userUID1;
    private String contents1;
    private String userName;


    /**
     * FireBase Setting
     */
    private ArrayList<Diary> arrayList;
    private FirebaseDatabase database;
    private FirebaseAuth auth; // 파이어 베이스 인증 객체
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;


    private ArrayList<String> idIndicator = new ArrayList<String>();
    private ArrayList<String> matchtitle = new ArrayList<String>();
    private ArrayList<String> matchdate = new ArrayList<String>();
    private ArrayList<String> matchtime = new ArrayList<String>();
    private ArrayList<String> matchProfile = new ArrayList<String>();
    private ArrayList<String> matchContents = new ArrayList<String>();
    private ArrayList<String> matchAddress = new ArrayList<String>();
    private ArrayList<String> matchID = new ArrayList<String>();
    Button button;
    ImageView imageView;
    private String date;
    private static final int REQUEST_CODE = 0;
    Long now = System.currentTimeMillis();
    Date mDate = new Date(now);
    Drawable drawable;
    //리사이클러뷰 등장
    DiaryAdapter diaryAdapter;
    final String[] words = new String[] {"수정","삭제"};

    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            date = year + "/" + month + "/" + dayOfMonth;
            updateLabel();
            updateList();

            LayoutAnimationController controller = new LayoutAnimationController(set, 0.17f);
            recyclerView.setLayoutAnimation(controller);



        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag1, container, false);



        auth = FirebaseAuth.getInstance(); // 파이어베이스 인증 객체 초기화.
        user = auth.getCurrentUser();
        userName = user.getUid();





        new Thread(r).start();

        recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); // User 객체를 담을 어레이 리스트 (어댑터쪽으로)



        diaryAdapter = new DiaryAdapter();
        diaryAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(DiaryAdapter.ViewHolder holder, View view, final int position) {
                new AlertDialog.Builder(getContext()).setItems(words, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                Bundle args = new Bundle();
                                args.putString("where", matchtitle.get(position));
                                args.putString("contents", matchContents.get(position));
                                args.putString("url", matchProfile.get(position));
                                args.putString("date", matchdate.get(position));
                                args.putString("time", matchtime.get(position));
                                args.putString("address", matchAddress.get(position));
                                args.putString("id", matchID.get(position));
                                Log.d("Lee", "\n where : " + matchtitle.get(position) + "\n contents : " + matchContents.get(position)
                                        + "\n date : " + matchdate.get(position)+ "\n time : " + matchtime.get(position)
                                +"\n Address : " + matchAddress.get(position));

                                BottomSheetFragment bottomSheet = new BottomSheetFragment();
                                bottomSheet.setArguments(args);
                                bottomSheet.show(getFragmentManager(), "BS");

                                break;
                            case 1:
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        delete(position);

                                        sqLiteManager.delete(idIndicator.get(position));
                                        Toast.makeText(getActivity().getApplicationContext(), "[" + matchdate.get(position) + "]" + matchtitle.get(position) + " 삭제 완료", Toast.LENGTH_SHORT).show();
                                        updateList();
                                        LayoutAnimationController controller = new LayoutAnimationController(set, 0.17f);
                                        recyclerView.setLayoutAnimation(controller);



                                    }
                                });

                                builder.setCancelable(true);
                                builder.setNegativeButton("아니오", null);
                                builder.setTitle("데이터 삭제");
                                builder.setMessage("[" + matchdate.get(position) + "]" +matchtitle.get(position) + " 데이터를 삭제하시겠습니까?");
                                builder.show();
                                break;

                        }
                    }
                }).show();

            }


        });


        DateUp = view.findViewById(R.id.DateUp);
        DateDown = view.findViewById(R.id.DateDown);

        textView = (TextView) view.findViewById(R.id.tv_date);
        imageView = view.findViewById(R.id.imageView);


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), myDatePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        }); //달력 꺼내기
        updateLabel();

        DateDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dayDown = sdf.format(myCalendar.getTime()).replace("-", "");
                int dayDownint = Integer.parseInt(dayDown);
                dayDownint = dayDownint - 1;
                dayDown = valueOf(dayDownint);

                SimpleDateFormat sdfmt = new SimpleDateFormat("yyyyMMdd");
                try {
                    Date date = sdfmt.parse(dayDown);
                    dayDown = new SimpleDateFormat("yyyy-MM-dd").format(date);
                    myCalendar.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                textView.setText(dayDown);
                updateList();
                LayoutAnimationController controller = new LayoutAnimationController(set, 0.17f);
                recyclerView.setLayoutAnimation(controller);


            }
        });

        DateUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dayUp = sdf.format(myCalendar.getTime()).replace("-", "");
                int dayUpint = Integer.parseInt(dayUp);
                dayUpint = dayUpint + 1;
                dayUp = valueOf(dayUpint);

                SimpleDateFormat sdfmt = new SimpleDateFormat("yyyyMMdd");
                try {
                    Date date = sdfmt.parse(dayUp);
                    dayUp = new SimpleDateFormat("yyyy-MM-dd").format(date);
                    myCalendar.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                textView.setText(dayUp);
                updateList();
                LayoutAnimationController controller = new LayoutAnimationController(set, 0.17f);
                recyclerView.setLayoutAnimation(controller);
            }
        });

        Animation rtl = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, -1,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, -1,
                Animation.RELATIVE_TO_SELF, 0);
        rtl.setDuration(500);
        set.addAnimation(rtl);

        Animation alpha = new AlphaAnimation(0, 1);
        alpha.setDuration(700);
        set.addAnimation(alpha);

        final LayoutAnimationController[] controller = {new LayoutAnimationController(set, 0.17f)};
        recyclerView.setLayoutAnimation(controller[0]);


        /**
         * SQLite 제어 설정
         */
        // SQLite 객체 초기화
        sqLiteManager = new SQLiteManager(getActivity().getApplicationContext(), "writeYourThink.db", null, 1);

        firebaseUpdate();



        return view;
    }

    /**
     * FireBase Data Update
     */
/*
    private void updateList(){
        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference = database.getReference(textView.getText().toString());// DB 테이블 연결
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                // 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                arrayList.clear(); // 기존 배열리스트가 존재하지않게 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { // 반복문으로 데이터 List를 추출해냄
                    Diary diary = snapshot.getValue(Diary.class); // 만들어뒀던 User 객체에 데이터를 담는다.
                    arrayList.add(diary); // 담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비
                    System.out.println(diary);
                }
                adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 디비를 가져오던중 에러 발생 시
                Log.e("MainActivity", valueOf(databaseError.toException())); // 에러문 출력
            }
        });
        adapter = new CustomAdapter(arrayList, getContext());
        recyclerView.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결
    }
 */





    private void updateList() {
        idIndicator.clear();
        matchtitle.clear();
        matchContents.clear();
        matchAddress.clear();
        matchProfile.clear();
        matchdate.clear();
        matchtime.clear();
        matchID.clear();
        diaryAdapter.removeItem();    // ListView 내용 모두 삭제
        ArrayList<JSONObject> array = sqLiteManager.getResult(textView.getText().toString()); // DB의 내용을 배열단위로 모두 가져온다
        try {
            int length = array.size(); // 배열의 길이
            for (int idx = 0; idx < length; idx++) {  // 배열의 길이만큼 반복
                JSONObject object = array.get(idx);// json의 idx번째 object를 가져와서,
                String userName = object.getString("userName");         // object 내용중 id를 가져와 저장.
                String id = object.getString("id");         // object 내용중 id를 가져와 저장.
                String title = object.getString("title");         // object 내용중 id를 가져와 저장.
                String contents = object.getString("contents");     // object 내용중 contents를 가져와 저장.
                String profile = object.getString("profile");     // object 내용중 profile를 가져와 저장.
                String date =  object.getString("date");     // object 내용중 date를 가져와 저장.
                String time = object.getString("time");     // object 내용중 date를 가져와 저장.
                String address = object.getString("address");

                matchID.add(id);
                matchAddress.add(address);
                matchContents.add(contents);
                matchProfile.add(profile);
                matchdate.add(date);
                matchtime.add(time);
                idIndicator.add(id);
                matchtitle.add(title);
                if(Locale.getDefault().getISO3Language().equals("eng")){
                    diaryAdapter.addItem(new Diary(userName, profile,
                            "At a " + title + ", " + address,
                            contents,
                            date.substring(0,4) + "-" + date.substring(5,7) + "-" +
                                    date.substring(8) + "-" +   "("+time+")",""));
                    recyclerView.setAdapter(diaryAdapter);
                }else{
                    // 저장한 내용을 토대로 ListView에 다시 그린다.
                    diaryAdapter.addItem(new Diary(userName, profile,
                            address.equals(" ") || address.equals(null)?  title + "에서..": "의 "+ title + "에서..",
                            contents,
                            date.substring(0,4) + "년 " + date.substring(5,7) + "월 " +
                                    date.substring(8) + "일" +   "("+time+")", address));
                    recyclerView.setAdapter(diaryAdapter);
                }





            }
        } catch (Exception e) {
            Log.i("seo", "error : " + e);

        }
        diaryAdapter.notifyDataSetChanged();
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri uri = data.getData();
                    Glide.with(getActivity().getApplicationContext()).load(valueOf(uri)).into(imageView);
                    edit_upload.setText(valueOf(uri));
                } catch (Exception e) {

                }
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

    private void updateLabel() {


        date = sdf.format(myCalendar.getTime());
        textView.setText(sdf.format(myCalendar.getTime()));

    }

    Runnable r = new Runnable() {
        @Override
        public void run() {

            while (true) {
                try {
                    Thread.sleep(1000);

                } catch (Exception e) {

                }
                if (getActivity() != null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            time = sdf2.format(new Date());
                        }
                    });
                }

            }
        }

    };

    @Override
    public void onButtonClicked(String text) {

    }

    private void delete(int position){
        if (matchtime.size()>0){
            database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
            databaseReference = database.getReference(userName);// DB 테이블 연결
            databaseReference.child(matchdate.get(position) + "(" +matchtime.get(position) + ")").setValue(null);

        }
    }

    private void firebaseUpdate(){
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(getString(R.string.syncData));
        progressDialog.setCancelable(false);
        progressDialog.show();


        sqLiteManager = new SQLiteManager(getActivity(), "writeYourThink.db", null, 1);




        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference = database.getReference(userName); // DB 테이블 연결
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
                        updateList();
                        LayoutAnimationController controller = new LayoutAnimationController(set, 0.17f);
                        recyclerView.setLayoutAnimation(controller);
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