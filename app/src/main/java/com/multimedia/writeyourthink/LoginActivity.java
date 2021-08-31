package com.multimedia.writeyourthink;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shobhitpuri.custombuttons.GoogleSignInButton;

import java.util.ArrayList;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private GoogleSignInButton btn_google; // 구글 로그인 버튼
    private ConstraintLayout loginView;
    private FirebaseAuth auth; // 파이어 베이스 인증 객체
    private FirebaseUser user;
    // 페이스북 콜백 매니저
    private CallbackManager callbackManager;
    // 파이어베이스 인증 객체 생성
    private LoginButton btn_facebook;
    private GoogleApiClient googleApiClient; // 구글 API 클라이언트 객체
    private static final int REQ_SIGN_GOOGLE = 100; // 구글 로그인 결과 코드
    private static final int REQ_SIGN_FACEBOOK = 200; // 구글 로그인 결과 코드
    private String accessToken;


    /**
     * FireBase Setting
     */
    private ArrayList<Diary> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    //SQL Setting
    private String date;
    private String location1;
    private String with1;
    private String profile1;
    private String userUID1;
    private String contents1;
    private String userName;

    public SQLiteManager sqLiteManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();


        loginView = findViewById(R.id.loginView);
        Intent intent2 = getIntent();
        int viewNum = intent2.getIntExtra("viewNum",1);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (viewNum == 1){
            loginView.setBackground(getDrawable(R.drawable.write_your_think_1));
        }else if(viewNum == 2){
            loginView.setBackground(getDrawable(R.drawable.write_your_think_2));
        }

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();



        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        auth = FirebaseAuth.getInstance(); // 파이어베이스 인증 객체 초기화.
        user = auth.getCurrentUser();
        btn_google = findViewById(R.id.btn_google);

        animation.setFillEnabled(false);    //애니메이션 이 끝난곳에 고정할지 아닐지
        btn_google.startAnimation(animation);    //애니메이션 시작

        btn_google.setOnClickListener(new View.OnClickListener() { // 구글 로그인 버튼을 클릭했을 때 이곳을 수행.
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, REQ_SIGN_GOOGLE);
            }
        });

        if (user != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("nickName",user.getDisplayName());
            intent.putExtra("photoUrl",String.valueOf(user.getPhotoUrl()));
            intent.putExtra("accessToken",accessToken);
            intent.putExtra("fbLogin",1);
            startActivity(intent);

            finish();
        }


        btn_facebook=findViewById(R.id.btn_facebook);
        btn_facebook.startAnimation(animation);
        btn_facebook.setReadPermissions("email", "public_profile");
        btn_facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                accessToken = loginResult.getAccessToken().getToken();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // 구글 로그인 인증을 요청 했을 때 결과 값을 되돌려 받는 곳.
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_SIGN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()) { // 인증결과가 성공적이면..
                GoogleSignInAccount account = result.getSignInAccount(); // account 라는 데이터는 구글로그인 정보를 담고있습니다. (닉네임,프로필사진Url,이메일주소...등)
                resultLogin(account); // 로그인 결과 값 출력 수행하라는 메소드
            }
        }else if (requestCode == REQ_SIGN_FACEBOOK) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // 구글 로그인 성공
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //firebaseAuthWithGoogle(account);
            } catch (ApiException e) {

            }
        }

    }

    private void resultLogin(final GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) { // 로그인이 성공했으면...
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("nickName",account.getGivenName());
                            intent.putExtra("photoUrl",String.valueOf(account.getPhotoUrl())); // String.valueOf() 특정 자료형을 String 형태로 변환.
                            intent.putExtra("fbLogin",2);
                            Bundle args = new Bundle();
                            args.putInt("fbLogin",2);

                            startActivity(intent);
                            finish();
                        } else { // 로그인이 실패했으면..
                            Toast.makeText(LoginActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    // 페이스북 로그인 이벤트
// 사용자가 정상적으로 로그인한 후 페이스북 로그인 버튼의 onSuccess 콜백 메소드에서 로그인한 사용자의
// 액세스 토큰을 가져와서 Firebase 사용자 인증 정보로 교환하고,
// Firebase 사용자 인증 정보를 사용해 Firebase에 인증.
    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("loginNum",2);
                            intent.putExtra("accessToken",accessToken);
                            intent.putExtra("fbLogin",2);
                            startActivity(intent);
                            finish();
                        } else {
                            // 로그인 실패
                            Toast.makeText(LoginActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }


}
