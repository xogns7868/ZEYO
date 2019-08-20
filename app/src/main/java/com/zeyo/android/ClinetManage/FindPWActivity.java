package com.zeyo.android.ClinetManage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.zeyo.android.APIClient;
import com.zeyo.android.APIInterface;
import com.zeyo.android.Drawer.ButtonController;
import com.zeyo.android.R;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindPWActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private View drawerView;
    boolean check=false;
    public static Context context;
    EditText editTextEmail;
    APIInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);
        context=this;
        TextView title = (TextView)findViewById(R.id.txt_title);
        title.setText("비밀번호 찾기");

        final ProgressDialog progressDialog = new ProgressDialog(FindPWActivity.this);
        progressDialog.setMessage("메시지 전송중");
        progressDialog.setCanceledOnTouchOutside(false);

        drawerLayout = (DrawerLayout) findViewById(R.id.dra_layout);
        drawerView = (View) findViewById(R.id.drawer);
        ImageButton buttonOpenDrawer = (ImageButton) findViewById(R.id.btn_menu);

        ButtonController controller = new ButtonController();
        controller.controller((TextView) findViewById(R.id.btn_login),(TextView)findViewById(R.id.txt_before_name),(TextView)findViewById(R.id.txt_after_name),(TextView)findViewById(R.id.txt_login),(Button) findViewById(R.id.btn_question_text),
                (Button) findViewById(R.id.btn_policy_text),(Button) findViewById(R.id.btn_signup),(TextView)findViewById(R.id.txt_log_out),this);

        buttonOpenDrawer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                drawerLayout.openDrawer(drawerView);
                drawerView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                    }
                });
            }
        });


        ImageButton btnBackArrow = (ImageButton) findViewById(R.id.btn_back_arrow);
        btnBackArrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });

        editTextEmail = (EditText)findViewById(R.id.edt_find_pw_email);
        final EditText editTextID = (EditText)findViewById(R.id.edt_find_pw_id);
        Button buttonFindPW = (Button)findViewById(R.id.btn_find_pw_success);
        buttonFindPW.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                apiInterface= APIClient.retrofit().create(APIInterface.class);
                progressDialog.show();
                Call<String> call1 = apiInterface.savePW(editTextEmail.getText().toString(),editTextID.getText().toString());
                call1.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        // CommonResponse commonResponse = response.body();
                        System.out.println(response.code());
                        if (response.code() == 200) {
                            if (Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-z]{2,6}$", editTextEmail.getText()) &&
                                    Pattern.matches("^[A-Za-z0-9]{4,12}$", editTextID.getText())) {
                                check = true;
                            } else {
                                check = false;
                            }
                            if (check) {
                                //SystemClock.sleep(500);

                                startActivity(new Intent(FindPWActivity.this, FindPWResultActivity.class));
                                progressDialog.dismiss();
                                finish();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "등록되지 않은 정보입니다.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        System.out.println(call);
                    }

                });

            }

        });

    }
}
