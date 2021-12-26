package com.multimedia.writeyourthink;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationSet;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;
    private static final int REQUEST_CODE = 0;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private GpsTracker gpsTracker; // 위치정보
    private View view;
    private Bundle bundle;
    final AnimationSet set = new AnimationSet(true);
    private Button btn_upload;// 업로드버튼
    private EditText edit_title, edit_contents, edit_upload;       /** 입력받을 폼 3개(음식이름, 음식칼로리, 날짜) */
    private ListView listView;                              /** DB에 저장된 내용을 보여주기위한 리스트뷰 */
    private ArrayAdapter<String> adapter;
    public SQLiteManager sqLiteManager;
    private String myFormat = "yyyy-MM-dd";    // 출력형식   2018/11/28
    private SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
    private SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
    private Button DateUp;
    private Button DateDown;
    private TextView textView;
    private String time;
    private LinearLayout invisibleLayout;
    private String address;

    String where = "";
    String contents = "";
    String photoURL = "";
    String matchDate = "";
    String matchTime = "";
    String matchAddress = "";
    String matchID = "";

    /** FIREBASE 관련 */
    private static final String TAG = "MainActivity";
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private Uri filePath;
    private StorageReference storageRef;
    private String stringUri;
    private String userName="Master";
    private FirebaseAuth auth; // 파이어 베이스 인증 객체
    private FirebaseUser user;


    private ArrayList<String> idIndicator = new ArrayList<String>();
    private ArrayList<String> matchtitle = new ArrayList<String>();
    private ArrayList<String> matchdate = new ArrayList<String>();
    private ImageButton button;
    ImageView imageView;
    private String date;

    Long now = System.currentTimeMillis();
    Date mDate = new Date(now);
    Drawable drawable;
    //리사이클러뷰 등장
    private ArrayList<Diary> arrayList;
    DiaryAdapter diaryAdapter;
    RecyclerView recyclerView;

    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            date = year + "/" + month + "/" + dayOfMonth;
            updateLabel();


        }
    };
    public static BottomSheetFragment newInstance() {
        BottomSheetFragment fragment = new BottomSheetFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public void setupDialog(Dialog dialog, int style) {

        try {
            Bundle mArgs = getArguments();
            where = mArgs.getString("where").equals("null") ? " " : mArgs.getString("where");
            contents = mArgs.getString("contents");
            photoURL = mArgs.getString("url");
            matchDate = mArgs.getString("date");
            matchTime = mArgs.getString("time");
            matchAddress =  mArgs.getString("address");
            matchID = mArgs.getString("id");
        }catch (NullPointerException e){
            where = "";
            contents = "";
            photoURL = "";
            matchDate = "";
            matchTime = "";
            matchAddress ="";
            matchID = "";
        }



        View view = View.inflate(getContext(), R.layout.frag2, null);
        dialog.setContentView(view);
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        auth = FirebaseAuth.getInstance(); // 파이어베이스 인증 객체 초기화.
        user = auth.getCurrentUser();
        userName = user.getUid();


        invisibleLayout = view.findViewById(R.id.invisibleLayout);
        invisibleLayout.setVisibility(View.GONE);


        gpsTracker = new GpsTracker(getActivity());
        double latitude = gpsTracker.getLatitude(); /** 위도 */
        double longitude = gpsTracker.getLongitude(); /** 경도 */
        address = getCurrentAddress(latitude, longitude);

        new Thread(r).start(); /** 현재시간 */





        DateUp = view.findViewById(R.id.DateUp);
        DateDown = view.findViewById(R.id.DateDown);

        textView = (TextView) view.findViewById(R.id.tv_date);
        imageView = view.findViewById(R.id.imageView);
        button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);


            }
        });

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
                dayDown = String.valueOf(dayDownint);

                SimpleDateFormat sdfmt = new SimpleDateFormat("yyyyMMdd");
                try {
                    Date date = sdfmt.parse(dayDown);
                    dayDown = new SimpleDateFormat("yyyy-MM-dd").format(date);
                    myCalendar.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                textView.setText(dayDown);

            }
        });

        DateUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dayUp = sdf.format(myCalendar.getTime()).replace("-", "");
                int dayUpint = Integer.parseInt(dayUp);
                dayUpint = dayUpint + 1;
                dayUp = String.valueOf(dayUpint);

                SimpleDateFormat sdfmt = new SimpleDateFormat("yyyyMMdd");
                try {
                    Date date = sdfmt.parse(dayUp);
                    dayUp = new SimpleDateFormat("yyyy-MM-dd").format(date);
                    myCalendar.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                textView.setText(dayUp);

            }
        });







        /**
         * SQLite 제어 설정
         */
        sqLiteManager = new SQLiteManager(getActivity().getApplicationContext(), "writeYourThink.db", null, 1);



        /** 각 컴포넌트 제어를 위한 아이디할당 (EditText, Button) */
        btn_upload = view.findViewById(R.id.btn_upload);
        edit_title = view.findViewById(R.id.edit_title);
        edit_contents = view.findViewById(R.id.edit_contents);
        edit_upload = view.findViewById(R.id.edit_upload);


        /** 버튼을 눌렀을때 해야할 이벤트 작성 */
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edit_title.getText().toString().equals("") || edit_contents.getText().toString().equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "내용을 입력하십시오.", Toast.LENGTH_SHORT).show();
                } else {
                    address = getCurrentAddress(latitude, longitude);
                    Log.d("Lee", " 주소값,.,?:" + address);

                    if (btn_upload.getText().toString().equals("수정")){ //수정일 때..ㅎ
                        Log.d("Lee", "아니 여기 수정이잖아!!!!!!!");

                        /** SQLite Data Insert */
                        sqLiteManager.update(Integer.parseInt(matchID), userName,
                                edit_title.getText().toString(),
                                edit_contents.getText().toString(),
                                photoURL != null?stringUri != null ? stringUri: photoURL : stringUri != null ? stringUri : " ",                           //edit_upload.getText().toString(),
                                matchDate,
                                matchTime, matchAddress.equals("주소 미발견") || matchAddress.equals(null)?" ":matchAddress);


                        /** FireBase Data Insert */
                        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
                        databaseReference = database.getReference(userName); // DB 테이블 연결
                        writeNewUser(  userName, time,
                                photoURL != null?stringUri != null ? stringUri: photoURL : stringUri != null ? stringUri : " " ,
                                edit_title.getText().toString(),
                                edit_contents.getText().toString() ,
                                matchDate + "(" + matchTime + ")",
                                matchAddress.equals("주소 미발견") || matchAddress.equals(null)?" ":matchAddress);
                    }else{
                        /** SQLite Data Insert */
                        sqLiteManager.insert(userName,
                                edit_title.getText().toString(),
                                edit_contents.getText().toString(),
                                stringUri != null?stringUri:" ",                           //edit_upload.getText().toString(),
                                textView.getText().toString(),
                                time, address.equals("주소 미발견") || address.equals(null)?" ":address.substring(address.indexOf(" ")+1, address.lastIndexOf(" ")));


                        /** FireBase Data Insert */
                        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
                        databaseReference = database.getReference(userName); // DB 테이블 연결
                        writeNewUser(userName, time,
                                stringUri != null?stringUri:" ",
                                edit_title.getText().toString(),
                                edit_contents.getText().toString() ,
                                textView.getText().toString() + "(" + time + ")",
                                address.equals("주소 미발견") || address.equals(null)?" ":address.substring(address.indexOf(" ")+1, address.lastIndexOf(" ")));
                    }




                    Toast.makeText(getActivity().getApplicationContext(), "끄적끄적!", Toast.LENGTH_LONG).show();
                    edit_title.setText(null);
                    edit_contents.setText(null);
                    edit_upload.setText(null);

                    drawable = getResources().getDrawable(R.mipmap.ic_launcher_round);
                    imageView.setImageDrawable(drawable);
                    invisibleLayout.setVisibility(View.GONE);

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }


            }
        });
        textView.setText(date);
        edit_title.setText("");
        edit_contents.setText("");
        btn_upload.setText("업로드");


        if (!where.equals("")) {
            textView.setText(matchDate);
            edit_title.setText(where);
            edit_contents.setText(contents);
            btn_upload.setText("수정");
            textView.setFocusable(false);
            textView.setFocusableInTouchMode(false);
            textView.setEnabled(false);
            DateUp.setVisibility(View.GONE);
            DateDown.setVisibility(View.GONE);

            if (photoURL.length() > 3) {
                invisibleLayout.setVisibility(View.VISIBLE);
            } else {
                invisibleLayout.setVisibility(View.GONE);
            }

            Glide.with(getActivity().getApplicationContext()).load(photoURL).into(imageView);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    filePath = data.getData();
                    uploadFile();

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


    private void tedPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                /** 권한요청성공 */
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {

            }
        };
        TedPermission.with(getActivity())
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_3))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }
    public String getCurrentAddress( double latitude, double longitude) { //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    100);
        } catch (IOException ioException) {
//네트워크 문제
            Toast.makeText(getActivity(), "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            tedPermission();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(getActivity(), "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            tedPermission();
            return "잘못된 GPS 좌표";
        }
        if (addresses == null || addresses.size() == 0) {
            tedPermission();
            return "주소 미발견";
        }
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }




    public interface BottomSheetListener {
        void onButtonClicked(String text);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }

    public void writeNewUser(String userUID, String time , String profile, String where,String contents,String date, String location) {
        Diary diary = new Diary(userUID, profile, where, contents, date, location);

        databaseReference.child(date ).setValue(diary);
    }




    /** upload the file */
    private void uploadFile() {
        /** 업로드할 파일이 있으면 수행 */
        if (filePath != null) {
            /** 업로드 진행 Dialog 보이기 */
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("업로드중...");
            progressDialog.setCancelable(false);
            progressDialog.show();


            storage = FirebaseStorage.getInstance();

            /** Unique한 파일명을 만들자. */
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
            Date now = new Date();
            String filename = formatter.format(now) + ".png";
            /** storage 주소와 폴더 파일명을 지정해 준다. */
            storageRef = storage.getReferenceFromUrl("gs://diary-d5627.appspot.com/").child("images/" +userName+"/"+ filename);
            /** 올라가거라... */
            storageRef.putFile(filePath)
                    /** 성공시 */
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            clickLoad();
                            progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
                            edit_upload.setText(String.valueOf(filePath));
                            invisibleLayout.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    /** 실패시 */
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                     /** 진행중 */
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                            double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }
    public void clickLoad() {

        /** Firebase Storage에 저장되어 있는 이미지 파일 읽어오기 */

        /** 1. Firebase Storeage관리 객체 얻어오기 */
        FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();

        /** 2. 최상위노드 참조 객체 얻어오기 */
        StorageReference rootRef= firebaseStorage.getReference();

        /**
         * 읽어오길 원하는 파일의 참조객체 얻어오기
         * 예제에서는 자식노드 이름은 monkey.png
         */


        /** 하위 폴더가 있다면 폴더명까지 포함 */
        if (storageRef!=null) {
            /** 참조객체로 부터 이미지의 다운로드 URL을 얻어오기 */
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    /** 다운로드 URL이 파라미터로 전달되어 옴. */
                    Glide.with(getActivity().getApplicationContext()).load(String.valueOf(uri)).into(imageView);
                    stringUri = String.valueOf(uri);
                }
            });

        }

    }

}