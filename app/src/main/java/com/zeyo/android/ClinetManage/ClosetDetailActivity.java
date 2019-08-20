package com.zeyo.android.ClinetManage;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.zeyo.android.APIClient;
import com.zeyo.android.APIInterface;
import com.zeyo.android.Drawer.ButtonController;
import com.zeyo.android.LoadingActivity;
import com.zeyo.android.R;

import com.zeyo.android.model.Closet.ClosetDetailResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClosetDetailActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private View drawerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closet_detail);
        TextView textViewTitle = (TextView)findViewById(R.id.txt_title);
        textViewTitle.setText("내 옷 상세보기");

        final ProgressDialog progressDialog = new ProgressDialog(ClosetDetailActivity.this);
        progressDialog.setMessage("옷 정보 불러오는중...");
        progressDialog.setCanceledOnTouchOutside(false);

        LinearLayout linearLayoutName = (LinearLayout)findViewById(R.id.layout_part_name);
        linearLayoutName.setOrientation(LinearLayout.VERTICAL);
        LinearLayout linearLayoutValue = (LinearLayout)findViewById(R.id.layout_part_value);
        linearLayoutValue.setOrientation(LinearLayout.VERTICAL);
        LinearLayout linearLayoutCm = (LinearLayout)findViewById(R.id.layout_part_cm);
        linearLayoutCm.setOrientation(LinearLayout.VERTICAL);


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

        TextView closetItemName = (TextView)findViewById(R.id.closetDetail_name);
        TextView closetItemCategory = (TextView)findViewById(R.id.closetDetail_category) ;


        APIInterface apiInterface = APIClient.retrofit().create(APIInterface.class);

        ButtonController controller = new ButtonController();
        controller.controller((TextView) findViewById(R.id.btn_login),(TextView)findViewById(R.id.txt_before_name),(TextView)findViewById(R.id.txt_after_name), (TextView)findViewById(R.id.txt_login), (Button) findViewById(R.id.btn_question_text),
                (Button) findViewById(R.id.btn_policy_text),(Button) findViewById(R.id.btn_signup),(TextView) findViewById(R.id.txt_log_out),this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        //옷장목록에서 아이템을 선택하면 아이템의 position을 이용해 id를 가져와서 토큰과 함께 전송한다.
        Call<ClosetDetailResponse> call = apiInterface.getClothDetail(((LoadingActivity)LoadingActivity.context).token,((ClosetActivity)ClosetActivity.context).closetList.get(((ClosetActivity)ClosetActivity.context).position).id);
        progressDialog.show();
        call.enqueue(new Callback<ClosetDetailResponse>() {
            @Override
            public void onResponse(Call<ClosetDetailResponse> call, Response<ClosetDetailResponse> response) {

                    ClosetDetailResponse closetDetailResponse = response.body();
                    //이미지를 빠르게 표현하기 위해서 Glide를 사용함.
                    Glide.with(getApplicationContext()).load(closetDetailResponse.getImage()).into((ImageView)findViewById(R.id.closetDetail_image));
                    closetItemName.setText(closetDetailResponse.getName());
                    closetItemCategory.setText(closetDetailResponse.getSubCategoryName());

                    //치수항목이 많은 옷이 생길 경우 다 표현하지 못할 것 같아서 항목이 표시되는 레이아웃은 스크롤뷰로 해놨음.
                    //받아온 항목들을 동적으로 생성해서 넣어줌.
                    for (int i=0; i<closetDetailResponse.getwardrobeScmmValueResponses().size();i++){
                        //add할때 자꾸 에러가나서 같은걸 세번 생성해줬음. 추후 수정요망.
                        System.out.println(i);
                        LinearLayout.LayoutParams layoutEmpty = new LinearLayout.LayoutParams(1,40);
                        LinearLayout linearLayoutEmpty = new LinearLayout(ClosetDetailActivity.this);
                        linearLayoutEmpty.setLayoutParams(layoutEmpty);
                        linearLayoutName.addView(linearLayoutEmpty);

                        layoutEmpty = new LinearLayout.LayoutParams(1,40);
                        linearLayoutEmpty = new LinearLayout(ClosetDetailActivity.this);
                        linearLayoutEmpty.setLayoutParams(layoutEmpty);
                        linearLayoutValue.addView(linearLayoutEmpty);

                        layoutEmpty = new LinearLayout.LayoutParams(1,40);
                        linearLayoutEmpty = new LinearLayout(ClosetDetailActivity.this);
                        linearLayoutEmpty.setLayoutParams(layoutEmpty);
                        linearLayoutCm.addView(linearLayoutEmpty);

                        TextView name = new TextView(ClosetDetailActivity.this);
                        name.setText(closetDetailResponse.getwardrobeScmmValueResponses().get(i).getMeasureItemName());
                        name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                        name.setTextColor(Color.parseColor("#1d1d1d"));
                        name.setLayoutParams(layoutParams);
                        linearLayoutName.addView(name);

                        TextView value = new TextView(ClosetDetailActivity.this);
                        value.setText(closetDetailResponse.getwardrobeScmmValueResponses().get(i).getValue());
                        value.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                        value.setTextColor(Color.parseColor("#1048ff"));
                        value.setLayoutParams(layoutParams);
                        linearLayoutValue.addView(value);

                        TextView cm = new TextView(ClosetDetailActivity.this);
                        cm.setText("cm");
                        cm.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                        cm.setTextColor(Color.parseColor("#777777"));
                        cm.setLayoutParams(layoutParams);
                        linearLayoutCm.addView(cm);

                    }
                    progressDialog.dismiss();
                }
                //SystemClock.sleep(500)
            @Override
            public void onFailure(Call<ClosetDetailResponse> call, Throwable t) {
                progressDialog.dismiss();
            }

        });

        ImageButton imageButtonBackArrow = (ImageButton) findViewById(R.id.btn_back_arrow);
        imageButtonBackArrow.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                finish();
            }
        });
    }

}