package com.zeyo.android;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.snackbar.Snackbar;
import com.zeyo.android.ClinetManage.ClosetActivity;
import com.zeyo.android.ClinetManage.Privacy;
import com.zeyo.android.Drawer.ButtonController;
import com.zeyo.android.Measure.StartMeasure;

import java.security.MessageDigest;


public class MainActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    private View drawerView;
    public static Context context;
    public static TextView buttonLogin;
    public TextView txtLogin;
    public TextView textAfterName;
    public TextView txtLogOut;
    public Button buttonSignup;
    public TextView textViewBeforeName;
    public String token;
    public float width;
    public float height;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        System.out.println(width);
        System.out.println(height);
        setContentView(R.layout.activity_zeyo_main);
        txtLogin = findViewById(R.id.txt_login);
        textAfterName = findViewById(R.id.txt_after_name);
        txtLogOut = findViewById(R.id.txt_log_out);
        context=this;
        getHashKey(context);
        buttonLogin =  findViewById(R.id.btn_login);
        Button buttonQestion = (Button) findViewById(R.id.btn_question_text);
        Button buttonPolicy = (Button) findViewById(R.id.btn_policy_text) ;
        buttonSignup = (Button) findViewById(R.id.btn_signup);
        textViewBeforeName =(TextView)findViewById(R.id.txt_before_name);
        ButtonController controller = new ButtonController();
        controller.controller(buttonLogin, textViewBeforeName,textAfterName,txtLogin,buttonQestion,buttonPolicy,buttonSignup,txtLogOut,context);
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


        ImageButton imageButtonRuler = (ImageButton) findViewById(R.id.btn_ruler);
        imageButtonRuler.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), StartMeasure.class);
                startActivity(intent);
            }
        });

        ImageButton imageButtonClothes = (ImageButton) findViewById(R.id.btn_clothes);
        imageButtonClothes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                token=((LoadingActivity)LoadingActivity.context).token;
                if(token!=null) {
                    Intent intent = new Intent(getApplicationContext(), ClosetActivity.class);
                    startActivity(intent);
                }
                else{
                    //Toast.makeText(getApplicationContext(),"로그인이 필요합니다.",Toast.LENGTH_SHORT).show();
                    snackbarPopup("로그인이 필요합니다.",R.id.coordinator_main,context);

                }
            }
        });

        TextView textViewPolicy = (TextView)findViewById(R.id.txt_privacy);
        textViewPolicy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this, Privacy.class);
                startActivity(intent);
            }
        });
    }
    @Nullable
    public static String getHashKey(Context context) {

        final String TAG = "KeyHash";

        String keyHash = null;

        try {

            PackageInfo info =

                    context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);



            for (Signature signature : info.signatures) {

                MessageDigest md;

                md = MessageDigest.getInstance("SHA");

                md.update(signature.toByteArray());

                keyHash = new String(Base64.encode(md.digest(), 0));

                Log.d(TAG, keyHash);

            }

        } catch (Exception e) {

            Log.e("name not found", e.toString());

        }



        if (keyHash != null) {

            return keyHash;

        } else {

            return null;

        }

    }
    public void snackbarPopup(String text,int resource, Context context){
        View coordiView = findViewById(resource);
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

