package com.zeyo.android.Drawer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.gson.JsonObject;
import com.zeyo.android.APIClient;
import com.zeyo.android.APIInterface;
import com.zeyo.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private View drawerView;
    boolean check=false;
    public static Context context;
    APIInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        final ProgressDialog progressDialog = new ProgressDialog(QuestionActivity.this);
        progressDialog.setMessage("메시지 전송중");
        progressDialog.setCanceledOnTouchOutside(false);

        context=this;
        TextView title = (TextView)findViewById(R.id.txt_title);
        title.setText("1:1 문의");


        drawerLayout = (DrawerLayout) findViewById(R.id.dra_layout);
        drawerView = (View) findViewById(R.id.drawer);
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

        ImageButton buttonBackArrow = (ImageButton) findViewById(R.id.btn_back_arrow);
        buttonBackArrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });

        final EditText editTextEmail=(EditText)findViewById(R.id.edt_question_email);
        final EditText editTextPhone=(EditText)findViewById(R.id.edt_question_phone);
        final EditText editTextTitle=(EditText)findViewById(R.id.edt_title);
        final EditText editTextContent=(EditText)findViewById(R.id.edt_content);

        Button buttonQuestionSuccess = (Button) findViewById(R.id.btn_question_success);
        buttonQuestionSuccess.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if( Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-z]{2,6}$",editTextEmail.getText())&&
                    Pattern.matches("^[0-9]{11}",editTextPhone.getText())){
                    check=true;
                }
                else{
                    check=false;
                }
                if(check) {

                    progressDialog.show();
                    apiInterface = APIClient.retrofit().create(APIInterface.class);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("email",editTextEmail.getText().toString());
                        jsonObject.put("phone",editTextPhone.getText().toString());
                        jsonObject.put("title",editTextTitle.getText().toString());
                        jsonObject.put("content",editTextContent.getText().toString());
                        //System.out.println(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

                    Call<JsonObject> call = apiInterface.postQuestion(bodyRequest);
                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            // CommonResponse commonResponse = response.body();
                            //System.out.println(response.raw().code());
//                            System.out.println(response.body().toString());
                            //SystemClock.sleep(500);
                            Intent intent = new Intent(QuestionActivity.this, QuestionSuccessActivity.class);
                            startActivity(intent);
                            ((Activity)context).finish();
                            progressDialog.dismiss();

                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            //System.out.println(call);
                            progressDialog.dismiss();

                        }

                    });

                }
            }
        });

        TextView btn_login = (TextView) findViewById(R.id.btn_login);
        TextView txtLogin = (TextView)findViewById(R.id.txt_login);
        Button btn_questionText = (Button) findViewById(R.id.btn_question_text);
        Button btn_policyText = (Button) findViewById(R.id.btn_policy_text) ;
        Button btn_signup = (Button) findViewById(R.id.btn_signup);
        TextView txt_before_name=(TextView)findViewById(R.id.txt_before_name);
        TextView txt_after_name=(TextView)findViewById(R.id.txt_after_name);
        TextView txtLogOut = findViewById(R.id.txt_log_out);
        ButtonController controller = new ButtonController();
        controller.controller(btn_login,txt_before_name,txt_after_name, txtLogin,btn_questionText,btn_policyText,btn_signup,txtLogOut,context);
    }
}