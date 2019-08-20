package com.zeyo.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonObject;
import com.zeyo.android.ClinetManage.LoginMaintain;
import com.zeyo.android.ClinetManage.TutorialViewPager;
import com.zeyo.android.model.consumer.response.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadingActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    Boolean check;
    APIInterface apiInterface;
    String authorization = Credentials.basic("zeyo_user", "iamuser");
    public String token=null;
    public static Context context;
    public UserInfo  userInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_loading);
        context = this;
        apiInterface = APIClient.retrofit().create(APIInterface.class);


        //어플 첫 실행시에만 튜토리얼 화면으로 이동함.
        sharedPreferences = getSharedPreferences("isFirst", Activity.MODE_PRIVATE);
        boolean first = sharedPreferences.getBoolean("isFirst", true);
        check=first;
        if(first) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirst",false);
            editor.commit();
        }
        try {
            startLoading();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void startLoading() throws JSONException {

        //로그인할때 저장해논 아이디와 비밀번호를 가져옴.
        JSONObject jsonObject = LoginMaintain.getUserSignInfo(LoadingActivity.this);
        //System.out.println(jsonObject.get("ID"));
        //System.out.println(jsonObject.get("PW"));
        if(jsonObject.get("ID").toString().length() != 0) {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    apiInterface = APIClient.retrofit().create(APIInterface.class);
                    Call<JsonObject> call = null;
                    try {
                        call = apiInterface.saveToken(authorization, jsonObject.get("ID").toString(), jsonObject.get("PW").toString(), "password");
                        //저장해놨던 아이디와 비밀번호를 전송함.
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            //System.out.println(response.isSuccessful());
                            if(response.isSuccessful()) {
                                token = response.body().get("access_token").toString();
                                token = "Bearer " + token.substring(1, token.length() - 1);
                                apiInterface = APIClient.retrofit().create(APIInterface.class);
                                Call<UserInfo> call2 = apiInterface.saveInfo(token);
                                call2.enqueue(new Callback<UserInfo>() {
                                    @Override
                                    public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {

                                        //로그인한 토큰으로 유저정보를 받아옴
                                        userInfo = response.body();
                                        finish();
                                        ((MainActivity) MainActivity.context).buttonLogin.setText("");
                                        ((MainActivity) MainActivity.context).txtLogin.setText("반갑습니다!");
                                        ((MainActivity) MainActivity.context).txtLogin.setTextSize(14);
                                        ((MainActivity) MainActivity.context).textViewBeforeName.setText("");
                                        ((MainActivity) MainActivity.context).textAfterName.setText(userInfo.getName() + "님!");
                                        ((MainActivity) MainActivity.context).buttonSignup.setText("\n" +userInfo.getEmail());
                                        ((MainActivity) MainActivity.context).txtLogOut.setText("\n로그아웃");
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


                    if (check) {
                        Intent intent = new Intent(LoadingActivity.this, TutorialViewPager.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }, 2000);
        }
        else{
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (check) {
                        Intent intent = new Intent(LoadingActivity.this, TutorialViewPager.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }, 2000);


        }
    }
}