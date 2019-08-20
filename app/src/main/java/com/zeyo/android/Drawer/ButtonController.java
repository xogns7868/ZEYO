package com.zeyo.android.Drawer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zeyo.android.ClinetManage.LoginActivity;
import com.zeyo.android.ClinetManage.LoginMaintain;
import com.zeyo.android.ClinetManage.SignupActivity;
import com.zeyo.android.LoadingActivity;
import com.zeyo.android.MainActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class ButtonController extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // TODO Auto-generated method stub

        super.onCreate(savedInstanceState);


    }


    public void controller(TextView buttonLogin, TextView textViewBeforeName, TextView textAfterName, TextView txtLogin,  Button buttonQuestionText, Button buttonPolicyText, Button buttonSignup, TextView logOut, final Context context){
        if (((LoadingActivity)LoadingActivity.context).token != null) {
            buttonLogin.setText("");
            txtLogin.setText("반갑습니다!");
            txtLogin.setTextSize(14);
            textViewBeforeName.setText("");
            logOut.setText("\n로그아웃");
            textAfterName.setText(((LoadingActivity) LoadingActivity.context).userInfo.getName()+"님!");
            buttonSignup.setText("\n" + ((LoadingActivity) LoadingActivity.context).userInfo.getEmail());
        }
        logOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (logOut.getText().length() != 0)
                    logoutDialog(context);
                else
                    return;
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if(((LoadingActivity)LoadingActivity.context).token==null) {
                    context.startActivity(new Intent(context, LoginActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK));
                    if(context.getClass().getSimpleName().equals("MainActivity")){

                        //drawer 닫기
                        ((MainActivity)MainActivity.context).drawerLayout.closeDrawers();
                    }
                    else{
                        ((Activity) context).finish();
                    }
                }

            }
        });

        buttonQuestionText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                context.startActivity(new Intent(context, QuestionActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK));
                if(context.getClass().getSimpleName().equals("MainActivity")){
                    ((MainActivity)MainActivity.context).drawerLayout.closeDrawers();
                }

                else{
                    ((Activity) context).finish();
                }
            }
        });
        //공지사항 버튼


        buttonPolicyText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                context.startActivity(new Intent(context, PolicyActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK));
                if(context.getClass().getSimpleName().equals("MainActivity")){
                    ((MainActivity)MainActivity.context).drawerLayout.closeDrawers();
                }
                else{
                    ((Activity) context).finish();
                }
            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if(!buttonSignup.getText().equals("회원가입하기 >")){
                }
                else if(((MainActivity)MainActivity.context).token==null) {
                    context.startActivity(new Intent(context, SignupActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK));
                }
                else if(context.getClass().getSimpleName().equals("MainActivity")){
                    ((MainActivity)MainActivity.context).drawerLayout.closeDrawers();
                }
                else{
                    ((Activity) context).finish();
                }
            }
        });
    }
    void logoutDialog(Context context)
    {
        //로그아웃하면 모든 액티비티를 종료하고 새로 메인화면을 열음.
        //토큰을 초기화해서 로그아웃처리함.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("로그아웃");
        builder.setMessage("로그아웃하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context,"로그아웃.",Toast.LENGTH_LONG).show();
                        ((MainActivity)MainActivity.context).token=null;
                        ((LoadingActivity)LoadingActivity.context).token=null;
                        LoginMaintain.clearUserSignInfo(context);
                        context.startActivity(new Intent(context,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context,"로그아웃취소.",Toast.LENGTH_LONG).show();
                    }
                });
        builder.setCancelable(false).show();
    }
}
