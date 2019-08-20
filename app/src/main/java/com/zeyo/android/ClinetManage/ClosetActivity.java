package com.zeyo.android.ClinetManage;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.zeyo.android.APIClient;
import com.zeyo.android.APIInterface;
import com.zeyo.android.Drawer.ButtonController;
import com.zeyo.android.LoadingActivity;
import com.zeyo.android.R;
import com.zeyo.android.model.Closet.ClosetResponse;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ClosetActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    APIInterface apiInterface;
    ArrayList<ClosetModel> closetList = new ArrayList<>();
    RecyclerAdapter adapter = new RecyclerAdapter(closetList);
    public static Context context;
    private DrawerLayout drawerLayout;
    private View drawerView;
    int position;
    ClosetResponse closetResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closet);
        TextView textViewTitle = (TextView) findViewById(R.id.txt_title);
        textViewTitle.setText("내 옷장");
        context = this;


        final ProgressDialog progressDialog = new ProgressDialog(ClosetActivity.this);
        progressDialog.setMessage("옷장 세팅중...");
        progressDialog.setCanceledOnTouchOutside(false);
        recyclerView = findViewById(R.id.rec_closet);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        drawerLayout = (DrawerLayout) findViewById(R.id.dra_layout);
        drawerView = (View) findViewById(R.id.drawer);
        ImageButton buttonOpenDrawer = (ImageButton) findViewById(R.id.btn_menu);
        drawerLayout.requestDisallowInterceptTouchEvent(true);
        buttonOpenDrawer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                drawerLayout.openDrawer(drawerView);
               drawerView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                    }
                });
            }
        });
        TextView buttonLogin = findViewById(R.id.btn_login);
        TextView txtLogin = findViewById(R.id.txt_login);
        Button buttonQestion = (Button) findViewById(R.id.btn_question_text);
        Button buttonPolicy = (Button) findViewById(R.id.btn_policy_text);
        Button buttonSignup = (Button) findViewById(R.id.btn_signup);
        TextView textViewBeforeName = (TextView) findViewById(R.id.txt_before_name);
        TextView textAfterName = findViewById(R.id.txt_after_name);
        TextView txtLogOut = findViewById(R.id.txt_log_out);

        ButtonController controller = new ButtonController();
        controller.controller(buttonLogin, textViewBeforeName, textAfterName, txtLogin,  buttonQestion, buttonPolicy, buttonSignup, txtLogOut, this);


        ImageButton imageButtonBackArrow = (ImageButton) findViewById(R.id.btn_back_arrow);
        imageButtonBackArrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });


        apiInterface = APIClient.retrofit().create(APIInterface.class);
        progressDialog.show();

        //로그인해서 얻은 토큰을 보내서 옷장 목록을 받아온다.
        Call<ClosetResponse> call = apiInterface.saveClosetList(((LoadingActivity) LoadingActivity.context).token);
        call.enqueue(new Callback<ClosetResponse>() {

            @Override
            public void onResponse(Call<ClosetResponse> call, Response<ClosetResponse> response) {
                closetResponse = response.body();


                for (int i = 0; i < closetResponse.getSize(); i++) {
                    //System.out.println(jsonArray);
                    closetList.add(new ClosetModel(closetResponse.getWardrobeResponses().get(i).getImage(),
                            closetResponse.getWardrobeResponses().get(i).getName(),
                            closetResponse.getWardrobeResponses().get(i).getCreateDt(), "직접 등록", closetResponse.getWardrobeResponses().get(i).getId()));

                }
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();
            }
            @Override
            public void onFailure(Call<ClosetResponse> call, Throwable t) {
                progressDialog.dismiss();
            }

        });
    }
    public void snackbarPopup(String text){
        View coordiView = findViewById(R.id.coordinator_closet);
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
