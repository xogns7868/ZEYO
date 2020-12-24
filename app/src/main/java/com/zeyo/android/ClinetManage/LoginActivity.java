package com.zeyo.android.ClinetManage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;
import com.kakao.auth.AuthType;
import com.kakao.auth.Session;
import com.zeyo.android.APIClient;
import com.zeyo.android.APIInterface;
import com.zeyo.android.KakaoLogin.SessionCallback;
import com.zeyo.android.LoadingActivity;
import com.zeyo.android.MainActivity;
import com.zeyo.android.R;
import com.zeyo.android.model.consumer.response.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    APIInterface apiInterface;
    String authorization;
    public static Context context;
    private LinearLayout buttonGoogle;
    private static final int RC_SIGN_IN = 1000;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    String CLIENT_SECRET_FILE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
//        CLIENT_SECRET_FILE = "C:/Users/Administrator/workspace/gitlab/app/client_secret.json";
        // Configure Google Sign In

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("로그인...");
        progressDialog.setCanceledOnTouchOutside(false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestServerAuthCode("######.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mAuth = FirebaseAuth.getInstance();
        apiInterface = APIClient.retrofit().create(APIInterface.class);
        context = this;
        authorization = Credentials.basic("zeyo_user", "iamuser");
        ImageButton imageButtonX = (ImageButton) findViewById(R.id.btn_x);
        imageButtonX.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });

        LinearLayout buttonKakaoLogin = (LinearLayout) findViewById(R.id.login_kakao);
        buttonKakaoLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Session session = Session.getCurrentSession();
                session.addCallback(new SessionCallback());
                session.open(AuthType.KAKAO_LOGIN_ALL, LoginActivity.this);
            }
        });

        Button buttonFindID = (Button) findViewById(R.id.btn_find_id);
        buttonFindID.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                startActivity(new Intent(LoginActivity.this, FindIDActivity.class));
                finish();
            }
        });

        Button buttonFindPW = (Button) findViewById(R.id.btn_find_pw_login);
        buttonFindPW.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                startActivity(new Intent(LoginActivity.this, FindPWActivity.class));
                finish();
            }
        });

        Button buttonSignup = (Button) findViewById(R.id.btn_signup_login);
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                finish();
            }
        });

        LinearLayout naverLogin = findViewById(R.id.naver_login);
        naverLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                startActivity(new Intent(LoginActivity.this, NaverLogin.class));
//                finish();
            }
        });

        buttonGoogle = findViewById(R.id.google_login);
        buttonGoogle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        final EditText editTextID = (EditText) findViewById(R.id.edt_login_id);
        final EditText editTextPW = (EditText) findViewById(R.id.edt_login_pw);

        Button buttonLogin = (Button) findViewById(R.id.btn_login_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //아이디랑 비밀번호를 전송하고 저장함.
                //저장한 아이디와 비밀번호는 자동로그인때 사용.
                Call<JsonObject> call1 = apiInterface.saveToken(authorization, editTextID.getText().toString(), editTextPW.getText().toString(), "password");
                call1.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.code() == 400) {
                            //Toast.makeText(context, "존재하지 않는 계정입니다.", Toast.LENGTH_SHORT).show();
                            snackbarPopup("존재하지 않는 계정입니다.");
                        } else if (response.code() == 200) {
                            ((MainActivity) MainActivity.context).token = response.body().get("access_token").toString();
                            ((MainActivity) MainActivity.context).token = "Bearer " + ((MainActivity) MainActivity.context).token.substring(1, ((MainActivity) MainActivity.context).token.length() - 1);
                            ((LoadingActivity) LoadingActivity.context).token = ((MainActivity) MainActivity.context).token;

                            apiInterface = APIClient.retrofit().create(APIInterface.class);

                            //로그인한 토큰으로 유저정보를 불러옴.
                            Call<UserInfo> call2 = apiInterface.saveInfo(((MainActivity) MainActivity.context).token);
                            call2.enqueue(new Callback<UserInfo>() {
                                @Override
                                public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                                    //System.out.println(response.body().get("consumerId").toString());
                                    UserInfo userInfo = response.body();
                                    ((LoadingActivity)LoadingActivity.context).userInfo=userInfo;

                                    finish();
                                    ((MainActivity) MainActivity.context).buttonLogin.setText("");
                                    ((MainActivity) MainActivity.context).txtLogin.setText("반갑습니다!");
                                    ((MainActivity) MainActivity.context).txtLogin.setTextSize(14);
                                    ((MainActivity) MainActivity.context).textViewBeforeName.setText("");
                                    ((MainActivity) MainActivity.context).textAfterName.setText(userInfo.getName() + "님!");
                                    ((MainActivity) MainActivity.context).buttonSignup.setText("\n" +userInfo.getEmail());
                                    ((MainActivity) MainActivity.context).txtLogOut.setText("\n로그아웃");

                                    LoginMaintain.setUserSignInfo(LoginActivity.this, editTextID.getText().toString(), editTextPW.getText().toString());


                                }

                                @Override
                                public void onFailure(Call<UserInfo> call, Throwable t) {
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String authCode = account.getServerAuthCode();
                System.out.println("dfadfsfadfasfsaf" + account.getIdToken());
                System.out.println(authCode);
                OkHttpClient client = new OkHttpClient();
                FormBody.Builder formBuilder = new FormBody.Builder()
                        .add("grant_type", "authorization_code");
                formBuilder.add("client_id", "#######.apps.googleusercontent.com")
                        .add("client_secret", "######")
                        .add("redirect_uri", "")
                        .add("code", authCode);
                RequestBody requestBody = formBuilder.build();
                final Request request = new Request.Builder()
                        .url("https://oauth2.googleapis.com/token")
                        .post(requestBody)
                        .build();
                new Thread() {
                    public void run() {
                        try (okhttp3.Response response = client.newCall(request).execute()) {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            final String message = jsonObject.getString("access_token");
                            System.out.println(message);
                            apiInterface = APIClient.retrofit().create(APIInterface.class);
                            Call<JsonObject> call1 = apiInterface.saveTokenGoogle(message);
                            call1.enqueue(new retrofit2.Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    //CommonResponse commonResponse = response.body();
                                    System.out.println(response.code());
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            } catch (ApiException e) {
            }
        }
    }

    //    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
//        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(!task.isSuccessful()){
//                            Toast.makeText(LoginActivity.this, "인증 실패", Toast.LENGTH_SHORT).show();
//                        }else{
//                            System.out.println(acct.getServerAuthCode());
//
//                            Toast.makeText(LoginActivity.this, "구글 로그인 인증 성공", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
    public void call() {
        //카카오로그인으로 받아온 토큰을 전송함.
        //아직 api가 덜구현된것같음.(리스폰이 걍 []만 날라옴)
        Call<JsonObject> call = apiInterface.postKakaoLogin(((LoadingActivity) LoadingActivity.context).token, "#####");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                System.out.println(response.isSuccessful());
                System.out.println(response.body().toString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void snackbarPopup(String text){
        View coordiView = findViewById(R.id.coordinator_login);
        Snackbar snackbar = Snackbar.make(coordiView, text, Snackbar.LENGTH_INDEFINITE);

        View view1 = snackbar.getView();
        view1.setBackgroundColor(0x000000);

        Animation animation = AnimationUtils.loadAnimation(context,R.anim.snackbar);
        view1.setAnimation(animation);


        TextView textView = (TextView) view1.findViewById(R.id.snackbar_text);
        textView.setBackgroundResource(R.drawable.toast_custom_background);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        snackbar.setDuration(5000);


        snackbar.show();

    }
}
