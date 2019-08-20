package com.zeyo.android.ClinetManage;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.zeyo.android.model.consumer.response.FindIdResponse;

import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindIDActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private View drawerView;
    boolean check=false;
    String gender;
    public static Context context;
    DatePickerDialog datePickerDialog;
    int year=0;
    int month=0;
    int dayOfMonth=0;
    Calendar calendar;
    EditText edtName;

    APIInterface apiInterface;
    List<FindIdResponse> findIdResponse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);

        final ProgressDialog progressDialog = new ProgressDialog(FindIDActivity.this);
        progressDialog.setMessage("검색중...");
        progressDialog.setCanceledOnTouchOutside(false);

        context=this;

        ButtonController controller = new ButtonController();
        controller.controller((TextView) findViewById(R.id.btn_login),(TextView)findViewById(R.id.txt_before_name),(TextView)findViewById(R.id.txt_after_name),(TextView)findViewById(R.id.txt_login), (Button) findViewById(R.id.btn_question_text),
                (Button) findViewById(R.id.btn_policy_text),(Button) findViewById(R.id.btn_signup),(TextView)findViewById(R.id.txt_log_out),this);

        edtName=(EditText)findViewById(R.id.edt_find_id_name);

        TextView title = (TextView)findViewById(R.id.txt_title);
        title.setText("아이디 찾기");
        drawerLayout = (DrawerLayout) findViewById(R.id.dra_layout);
        drawerView = (View) findViewById(R.id.drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.dra_layout);
        drawerView = (View) findViewById(R.id.drawer);

        TextView editTextdate = findViewById(R.id.date_picker_find_id);
        editTextdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) { calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(FindIDActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year1, int month1, int day1) {
                                editTextdate.setText(year1 + "년 " + (month1 + 1) + "월 " + day1 + "일");
                                year = year1;
                                month = month1 + 1;
                                dayOfMonth = day1;
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        Button btnWoman=(Button)findViewById(R.id.btn_gender_woman_find_id);
        Button btnMan = (Button)findViewById(R.id.btn_gender_man_find_id);

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

        ImageButton btnBackArrow = (ImageButton) findViewById(R.id.btn_back_arrow);
        btnBackArrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });



        btnWoman.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                btnWoman.setBackgroundResource(R.drawable.rounded_rectangle_blue);
                btnWoman.setTextColor(Color.parseColor("#1048ff"));
                btnMan.setBackgroundResource(R.drawable.rounded_rectangle_gray);
                btnMan.setTextColor(Color.parseColor("#777777"));
                gender="W";
            }
        });

        btnMan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                btnMan.setBackgroundResource(R.drawable.rounded_rectangle_blue);
                btnMan.setTextColor(Color.parseColor("#1048ff"));
                btnWoman.setBackgroundResource(R.drawable.rounded_rectangle_gray);
                btnWoman.setTextColor(Color.parseColor("#777777"));
                gender="M";
            }
        });

        Button btn_findIDResult = (Button) findViewById(R.id.btn_find_id_result);
        btn_findIDResult.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if(Pattern.matches("^[ㄱ-ㅎ가-힣]{2,10}|[a-zA-Z]{2,20}$",edtName.getText())){
                    check=true;
                }
                else{
                    check=false;
                }
                if(check && gender!=null && year>0
                        && month>0 && dayOfMonth>0) {

                    apiInterface= APIClient.retrofit().create(APIInterface.class);

                    progressDialog.show();

                    //이름, 성별, 출생 년월일을 전송함.
                    //스피너에 아이템이 스트링으로 ~년 이런식으로 돼있기 때문에 마지막 한글자씩 잘라줘야함.
                    Call<List<FindIdResponse>> call1 = apiInterface.saveID(edtName.getText().toString(),gender,Integer.toString(year),
                            Integer.toString(month),
                            Integer.toString(dayOfMonth));
                    call1.enqueue(new Callback<List<FindIdResponse>>() {
                        @Override
                        public void onResponse(Call<List<FindIdResponse>> call, Response<List<FindIdResponse>> response) {
                            System.out.println(response.body().get(0).toString());
                            findIdResponse = response.body();
                            Intent intent = new Intent(FindIDActivity.this, FindIDResultActivity.class);
                            //SystemClock.sleep(500);
                            startActivity(intent);
                            progressDialog.dismiss();
                            finish();

                        }

                        @Override
                        public void onFailure(Call<List<FindIdResponse>> call, Throwable t) {
                            //System.out.println(call);
                            //System.out.println("test2");
                            progressDialog.dismiss();

                        }
                    });



                }
                else{
                    Toast.makeText(FindIDActivity.this,"모두 입력해 주세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

}
