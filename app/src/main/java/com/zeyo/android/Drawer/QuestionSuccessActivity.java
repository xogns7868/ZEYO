package com.zeyo.android.Drawer;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.zeyo.android.R;


public class QuestionSuccessActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private View drawerView;
    public static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_success);
        context =this;

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

        Button buttonSuccess = (Button) findViewById(R.id.btn_question_success_success);
        buttonSuccess.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });

        ButtonController controller = new ButtonController();
        controller.controller((TextView) findViewById(R.id.btn_login),(TextView)findViewById(R.id.txt_before_name),(TextView)findViewById(R.id.txt_after_name),(TextView)findViewById(R.id.txt_login), (Button) findViewById(R.id.btn_question_text),
                (Button) findViewById(R.id.btn_policy_text),(Button) findViewById(R.id.btn_signup),(TextView)findViewById(R.id.txt_log_out), this);
    }
}