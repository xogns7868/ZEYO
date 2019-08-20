package com.zeyo.android.ClinetManage;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zeyo.android.R;

public class Privacy extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        TextView title = (TextView) findViewById(R.id.txt_title);
        title.setText("서비스 운영 정책");

        ImageButton btnBackArrow = (ImageButton) findViewById(R.id.btn_back_arrow);
        btnBackArrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });

        TextView textView = (TextView)findViewById(R.id.textview_privacy);

        Button buttonPrivacy = (Button)findViewById(R.id.button_privacy);
        Button buttonPolicy = (Button)findViewById(R.id.button_policy);
        buttonPolicy.setPaintFlags(buttonPrivacy.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);

        TextView textViewSubTitle = (TextView)findViewById(R.id.textview_subtitle);

        buttonPrivacy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                textView.setText(R.string.privacy);
                //buttonPrivacy.setPaintFlags(buttonPrivacy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                buttonPrivacy.setTextColor(Color.parseColor("#1048ff"));
                buttonPrivacy.setPaintFlags(buttonPrivacy.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
                buttonPolicy.setTextColor(Color.parseColor("#999999"));
                buttonPolicy.setPaintFlags(0);
                textViewSubTitle.setText("개인정보 취급 방침");
            }
        });

        buttonPolicy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                textView.setText(R.string.usingpolicy);
                //buttonPolicy.setPaintFlags(buttonPrivacy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                buttonPrivacy.setTextColor(Color.parseColor("#999999"));
                buttonPolicy.setTextColor(Color.parseColor("#1048ff"));
                buttonPolicy.setPaintFlags(buttonPolicy.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);

                buttonPrivacy.setPaintFlags(0);
                textViewSubTitle.setText("이용약관");

            }
        });

    }
}
