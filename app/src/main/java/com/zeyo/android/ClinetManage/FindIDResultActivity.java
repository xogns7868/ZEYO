package com.zeyo.android.ClinetManage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.zeyo.android.Drawer.ButtonController;
import com.zeyo.android.R;

import org.json.JSONException;
import org.json.JSONObject;

public class FindIDResultActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private View drawerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id_result);
        TextView title = (TextView)findViewById(R.id.txt_title);
        title.setText("아이디 찾기 결과");


        TextView textViewResult = (TextView)findViewById(R.id.txt_find_id_result);
        Context context = ((FindIDActivity)FindIDActivity.context);
        String name = ((FindIDActivity) context).edtName.getText().toString();
        String year = Integer.toString(((FindIDActivity) context).year);
        String month = Integer.toString(((FindIDActivity) context).month);
        String day = Integer.toString(((FindIDActivity) context).dayOfMonth);
        String gender = ((FindIDActivity) context).gender;

        ButtonController controller = new ButtonController();
        controller.controller((TextView) findViewById(R.id.btn_login),(TextView)findViewById(R.id.txt_before_name),(TextView)findViewById(R.id.txt_after_name),(TextView)findViewById(R.id.txt_login),(Button) findViewById(R.id.btn_question_text),
             (Button) findViewById(R.id.btn_policy_text),(Button) findViewById(R.id.btn_signup),(TextView)findViewById(R.id.txt_log_out),this);

        if(gender=="M"){
            gender="남성";
        }
        else{
            gender="여성";
        }
        textViewResult.setText(name+"/"+year+" "+month+" "+day+"/"+gender+"으로\n"+"찾은 아이디 입니다.");


        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_find_id_result);
        LinearLayout.LayoutParams parmas = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100);
        LinearLayout.LayoutParams empty = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 20);


        for(int i=0;i<((FindIDActivity)FindIDActivity.context).findIdResponse.size();i++){
                TextView textView = new TextView(this);
                textView.setText(((FindIDActivity)FindIDActivity.context).findIdResponse.get(i).getConsumerId());
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                textView.setTextColor(Color.BLACK);
                textView.setBackgroundResource(R.drawable.rectangle_gray);
                textView.setLayoutParams(parmas);
                textView.setGravity(Gravity.CENTER);
                linearLayout.addView(textView);
                textView = new TextView(this);
                textView.setLayoutParams(empty);
                linearLayout.addView(textView);

        }



        ImageButton btnBackArrow = (ImageButton) findViewById(R.id.btn_back_arrow);
        btnBackArrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });

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

        Button buttonFindPW = (Button) findViewById(R.id.btn_find_pw);
        buttonFindPW.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(FindIDResultActivity.this, FindPWActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button buttonLogin = (Button) findViewById(R.id.btn_find_id_result_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
