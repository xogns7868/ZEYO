package com.zeyo.android.ClinetManage;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.gson.JsonObject;
import com.zeyo.android.APIClient;
import com.zeyo.android.APIInterface;
import com.zeyo.android.Drawer.ButtonController;
import com.zeyo.android.LoadingActivity;
import com.zeyo.android.MainActivity;
import com.zeyo.android.R;
import com.zeyo.android.model.consumer.response.UserInfo;

import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignupSuccessActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private View drawerView;
    APIInterface apiInterface;
    String authorization;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_success);

        apiInterface = APIClient.retrofit().create(APIInterface.class);
        authorization = Credentials.basic("zeyo_user", "iamuser");


        drawerLayout = (DrawerLayout) findViewById(R.id.dra_layout);
        drawerView = (View) findViewById(R.id.drawer);
        ImageButton buttonOpenDrawer = (ImageButton) findViewById(R.id.btn_menu);
        buttonOpenDrawer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                drawerLayout.openDrawer(drawerView);
                drawerView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                    }
                });
            }
        });

        Button buttonStart = (Button)findViewById(R.id.btn_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Call<JsonObject> call1 = apiInterface.saveToken(authorization, ((SignupActivity)SignupActivity.context).editTextID.getText().toString(), ((SignupActivity)SignupActivity.context).editTextPWCheck.getText().toString(), "password");
                call1.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.code() == 400) {
                            //Toast.makeText(context, "존재하지 않는 계정입니다.", Toast.LENGTH_SHORT).show();
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

                                    LoginMaintain.setUserSignInfo(SignupSuccessActivity.this, ((SignupActivity)SignupActivity.context).editTextID.getText().toString(), ((SignupActivity)SignupActivity.context).editTextPWCheck.getText().toString());


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
                finish();
            }
        });


        ButtonController controller = new ButtonController();
        controller.controller((TextView) findViewById(R.id.btn_login),(TextView)findViewById(R.id.txt_before_name),(TextView)findViewById(R.id.txt_after_name),(TextView)findViewById(R.id.txt_login),(Button) findViewById(R.id.btn_question_text),
                (Button) findViewById(R.id.btn_policy_text),(Button) findViewById(R.id.btn_signup),(TextView)findViewById(R.id.txt_log_out),this);


    }
}
