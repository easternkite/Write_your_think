package com.multimedia.writeyourthink;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.service.autofill.Dataset;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.EventDay;
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.String.valueOf;

public class Frag3 extends Fragment {
    public SQLiteManager sqLiteManager;
    private View view;
    CompactCalendarView compactCalendarView;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM-YYYY", Locale.getDefault());
    private SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private String text;

    /**
     * 데이터 관련
     */
    private FirebaseAuth auth; // 파이어 베이스 인증 객체
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String userUID;
    private String userName;
    private String userProfile;
    private String userEmail;
    private final String TAG = "로그";
    SimpleDateFormat sdf;
    TextView tx_date, tx_today;
    LinearLayout ly_detail;
    LinearLayout ly_left, ly_right;
    Calendar myCalendar;
    ImageView im_back;
    Date c;
    SimpleDateFormat df;
    String formattedDate;
    String[] dates = new String[0];
    RecyclerView recyclerView;
    TextView tx_item;
    TextView tv_selDate;
    TextView tv_count;
    private Button btn_logout;
    private CircleImageView iv_profile;
    private TextView tv_nickname;
    private ArrayList<String> selectedDate = new ArrayList<String>();
    private ArrayList<String> countedDate = new ArrayList<String>();


    String[] day={"10","20","21","25","27"};
    String[] month={"10","10","11","11","12"};
    String[] year ={"2018","2018","2018","2018","2018"};


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag3, container, false);


        auth = FirebaseAuth.getInstance(); // 파이어베이스 인증 객체 초기화.
        user = auth.getCurrentUser();
        Intent intent = getActivity().getIntent();
        String nickName = intent.getStringExtra("nickName"); // MainActivity로 부터 닉네임 전달받음.
        String photoUrl = intent.getStringExtra("photoUrl"); // MainActivity로 부터 프로필사진 Url 전달받음.
        userUID = user.getUid();

        tv_nickname = view.findViewById(R.id.tv_nickname);
        iv_profile = view.findViewById(R.id.iv_profile);
        btn_logout = view.findViewById(R.id.btn_logout);

        tv_selDate = view.findViewById(R.id.tv_selDate);
        tv_count= view.findViewById(R.id.tv_count);


        sqLiteManager = new SQLiteManager(getActivity(), "writeYourThink.db", null, 1);



        updateUserInfo();
        updateList();
        List<EventDay> events = new ArrayList<>();

        init();
        calendarlistener();
        Setdate();




        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                LoginManager.getInstance().logOut();
                sqLiteManager.deleteAll();
                Toast.makeText(getActivity(),getString(R.string.logout),Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent1);
                getActivity().finish();
            }
        });

        tx_date.setText(simpleDateFormat.format(myCalendar.getTime()));


        ly_left.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                compactCalendarView.showCalendarWithAnimation();
                compactCalendarView.showNextMonth();
            }
        });

        ly_right.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                compactCalendarView.showCalendarWithAnimation();
                compactCalendarView.showPreviousMonth();
            }
        });

        return view;


    }
    public void init() {
        compactCalendarView = (CompactCalendarView) view.findViewById(R.id.compactcalendar_view);
        tx_date = (TextView) view.findViewById(R.id.text);
        ly_left = (LinearLayout) view.findViewById(R.id.layout_left);
        ly_right = (LinearLayout) view.findViewById(R.id.layout_right);
        im_back = (ImageView) view.findViewById(R.id.image_back);
    }

    public void calendarlistener() {
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override

            public void onDayClick(Date dateClicked) {
                int profile_counts = (int) sqLiteManager.getProfilesCount(DateFormat.format(dateClicked));
                sqLiteManager.close();
                tv_selDate.setText(DateFormat.format(dateClicked));
                if (profile_counts>0){
                    tv_count.setText(getString(R.string.frag3_2 ) + " : " + profile_counts +" "+ getString(R.string.cases));
                }else{
                    tv_count.setText(getString(R.string.frag3_1));
                }



            }

            @Override

            public void onMonthScroll(Date firstDayOfNewMonth) {

                compactCalendarView.removeAllEvents();
                Setdate();
                tx_date.setText(simpleDateFormat.format(firstDayOfNewMonth));

            }
        });
    }

    //get current date


    public void Setdate() {



        c = Calendar.getInstance().getTime();
        df = new SimpleDateFormat("yyyy-MM-dd");
        int profile_counts = (int) sqLiteManager.getProfilesCount(DateFormat.format(c));
        sqLiteManager.close();
        compactCalendarView.setUseThreeLetterAbbreviation(true);

        sdf = new SimpleDateFormat("MMMM yyyy");
        formattedDate = df.format(c);
        tv_selDate.setText(formattedDate);

        if (profile_counts>0) {
            tv_count.setText(getString(R.string.frag3_2 ) + " : " + profile_counts +" "+ getString(R.string.cases));
        }else{
            tv_count.setText(getString(R.string.frag3_1));
        }

        myCalendar = Calendar.getInstance();

        for (int j = 0; j < selectedDate.size(); j++) {
            int mon = Integer.parseInt(selectedDate.get(j).substring(5,7));
            myCalendar.set(Calendar.YEAR, Integer.parseInt(selectedDate.get(j).substring(0,4)));
            myCalendar.set(Calendar.MONTH, mon - 1);
            myCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(selectedDate.get(j).substring(8)));

            Event event = new Event(Color.RED, myCalendar.getTimeInMillis(), "test");
            compactCalendarView.addEvent(event);
        }
    }
    private void updateUserInfo() {
        ArrayList<JSONObject> array = sqLiteManager.getResultUser(); // DB의 내용을 배열단위로 모두 가져온다
        try {




            int length = array.size(); // 배열의 길이
            for (int idx = 0; idx < length; idx++) {  // 배열의 길이만큼 반복
                JSONObject object = array.get(idx);// json의 idx번째 object를 가져와서,
                String id = object.getString("id");         // object 내용중 id를 가져와 저장.
                String userUID = object.getString("userUID");         // object 내용중 uid를 가져와 저장.
                String userName = object.getString("userName");         // object 내용중 name를 가져와 저장.
                String userProfile = object.getString("userProfile");     // object 내용중 profile를 가져와 저장.
                String userEmail = object.getString("userEmail");     // object 내용중 profile를 가져와 저장.

                // 저장한 내용을 토대로 ListView에 다시 그린다.


                tv_nickname.setText(userName);
                Glide.with(getActivity().getApplicationContext()).load(valueOf(userProfile)).into(iv_profile);
            }
        } catch (Exception e) {
            Log.i("seo", "error : " + e);

        }
    }


    private void updateList() {
        selectedDate.clear();
        ArrayList<JSONObject> array = sqLiteManager.getResult2(); // DB의 내용을 배열단위로 모두 가져온다
        try {
            int length = array.size();                                // 배열의 길이
            for (int idx = 0; idx < length; idx++) {                  // 배열의 길이만큼 반복
                JSONObject object = array.get(idx);                   // json의 idx번째 object를 가져와서,
                String id = object.getString("id");             // object 내용중 id를 가져와 저장.
                String title = object.getString("title");       // object 내용중 id를 가져와 저장.
                String contents = object.getString("contents"); // object 내용중 contents를 가져와 저장.
                String profile = object.getString("profile");   // object 내용중 profile를 가져와 저장.
                String date = object.getString("date");         // object 내용중 date를 가져와 저장.
                String time = object.getString("time");         // object 내용중 date를 가져와 저장.
                String address = object.getString("address");

                selectedDate.add(date);
                // 저장한 내용을 토대로 ListView에 다시 그린다.

            }
        } catch (Exception e) {
            Log.i("seo", "error : " + e);

        }
    }


}