package com.zeyo.android.ClinetManage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.zeyo.android.Drawer.ButtonController;
import com.zeyo.android.R;

public class FindPWResultActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private View drawerView;
    public static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw_result);
        context =this;
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

        TextView textViewemail = (TextView)findViewById(R.id.txt_find_pw_email);
        textViewemail.setText(((FindPWActivity)FindPWActivity.context).editTextEmail.getText().toString());


        Button buttonSuccess = (Button) findViewById(R.id.btn_find_pw_success_success);
        buttonSuccess.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ButtonController controller = new ButtonController();
        controller.controller((TextView) findViewById(R.id.btn_login),(TextView)findViewById(R.id.txt_before_name),(TextView)findViewById(R.id.txt_after_name),(TextView)findViewById(R.id.txt_login),(Button) findViewById(R.id.btn_question_text),
                (Button) findViewById(R.id.btn_policy_text),(Button) findViewById(R.id.btn_signup),(TextView)findViewById(R.id.txt_log_out),this);
    }
}
