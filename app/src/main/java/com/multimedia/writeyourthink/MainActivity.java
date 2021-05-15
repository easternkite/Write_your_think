package com.multimedia.writeyourthink;

import android.Manifest;
import android.os.Bundle;
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
}