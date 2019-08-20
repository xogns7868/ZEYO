package com.zeyo.android.ClinetManage;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.zeyo.android.APIClient;
import com.zeyo.android.APIInterface;
import com.zeyo.android.R;

import java.io.IOException;
import java.util.Calendar;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignupActivity extends AppCompatActivity {
    APIInterface apiInterface;
    boolean require=false;
    private String gender;
    private String advertisement="N";

    DatePickerDialog datePickerDialog;
    int year = 0;
    int month = 0;
    int dayOfMonth = 0;
    Calendar calendar;

    public EditText editTextPWCheck;
    public EditText editTextID;
    public static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);

        context=this;

        apiInterface = APIClient.retrofit().create(APIInterface.class);

        TextView title = (TextView) findViewById(R.id.txt_title);
        title.setText("회원가입");

        ImageButton btnBackArrow = (ImageButton) findViewById(R.id.btn_back_arrow);
        btnBackArrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });

        final EditText editTextPW = (EditText) findViewById(R.id.edt_pw);
        editTextPWCheck = (EditText) findViewById(R.id.edt_pw_check);
        editTextID = (EditText) findViewById(R.id.edt_id);
        final EditText editTextEmail = (EditText) findViewById(R.id.edt_email);
        final EditText editTextName = (EditText) findViewById(R.id.edt_name);
        final TextView textViewPW = (TextView) findViewById(R.id.txt_pw);
        final TextView textViewPWCheck = (TextView) findViewById(R.id.txt_pw_check);
        final TextView textViewIDCheck = (TextView) findViewById(R.id.txt_id_check);
        final TextView textViewEmailCheck = (TextView) findViewById(R.id.txt_email_check);
        final TextView textViewNameCheck = (TextView) findViewById(R.id.txt_name_check);
        final TextView textViewBirthCheck = (TextView) findViewById(R.id.txt_birth_check);
        final TextView textViewGenderCheck = (TextView) findViewById(R.id.txt_gender_check);
        final Button buttonWoman = (Button) findViewById(R.id.btn_gender_woman);
        final Button buttonMan = (Button) findViewById(R.id.btn_gender_man);
        final ImageButton buttonRequire = (ImageButton) findViewById(R.id.btn_require);//체크 필수사항
        final ImageButton buttonOption = (ImageButton) findViewById(R.id.btn_option);//체크 선택사항

        TextView editTextdate = findViewById(R.id.date_picker);
        editTextdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) { calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(SignupActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year1, int month1, int day1) {
                                editTextdate.setText(year1 + "년 " + (month1 + 1) + "월 " + day1 + "일");
                                textViewBirthCheck.setText("생년월일이 입력되었습니다.");
                                textViewBirthCheck.setTextColor(Color.parseColor("#1048ff"));
                                year = year1;
                                month = month1 + 1;
                                dayOfMonth = day1;
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
        ImageButton btn_signupBack = (ImageButton) findViewById(R.id.btn_back_arrow);
        btn_signupBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });


        //입력 조건은 EditText에 글자를 타이핑할때마다 체크함.
        editTextPW.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.equals("")) {
                    if (Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$", editTextPW.getText())) {
                        textViewPW.setText("올바른 비밀번호입니다.");
                        textViewPW.setTextColor(Color.parseColor("#1048ff"));
                    } else {
                        textViewPW.setText("8~20자의 영문 소문자, 숫자와 특수기호를 혼합해야 합니다.");
                        textViewPW.setTextColor(Color.parseColor("#ff0000"));
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        editTextPWCheck.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.equals("")) {
                    if (editTextPW.getText().toString().equals(editTextPWCheck.getText().toString()) && !editTextPW.getText().toString().equals("")) {
                        textViewPWCheck.setText("비밀번호가 일치합니다.");
                        textViewPWCheck.setTextColor(Color.parseColor("#1048ff"));

                    } else {
                        textViewPWCheck.setText("비밀번호가 불일치합니다.");
                        textViewPWCheck.setTextColor(Color.parseColor("#ff0000"));

                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        editTextID.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.equals("")) {
                    if (Pattern.matches("^[A-Za-z0-9]{4,12}$", editTextID.getText())) {
                        textViewIDCheck.setText("올바른 아이디입니다.");
                        textViewIDCheck.setTextColor(Color.parseColor("#1048ff"));

                    } else {
                        textViewIDCheck.setText("4~12자의 영문 소문자,숫자를 혼합해서 사용 가능합니다.");
                        textViewIDCheck.setTextColor(Color.parseColor("#ff0000"));

                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        editTextEmail.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.equals("")) {
                    if (Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-z]{2,6}$", editTextEmail.getText())) {
                        textViewEmailCheck.setText("올바른 이메일입니다.");
                        textViewEmailCheck.setTextColor(Color.parseColor("#1048ff"));

                    } else {
                        textViewEmailCheck.setText("올바르지 못한 이메일입니다.");
                        textViewEmailCheck.setTextColor(Color.parseColor("#ff0000"));

                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        editTextName.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.equals("")) {
                    if (Pattern.matches("^[ㄱ-ㅎ가-힣]{2,10}|[a-zA-Z]{2,20}$", editTextName.getText())) {
                        textViewNameCheck.setText("올바른 이름입니다.");
                        textViewNameCheck.setTextColor(Color.parseColor("#1048ff"));

                    } else {
                        textViewNameCheck.setText("올바르지 못한 이름입니다.");
                        textViewNameCheck.setTextColor(Color.parseColor("#ff0000"));

                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        buttonWoman.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                buttonWoman.setBackgroundResource(R.drawable.rounded_rectangle_blue);
                buttonWoman.setTextColor(Color.parseColor("#1048ff"));
                buttonMan.setBackgroundResource(R.drawable.rounded_rectangle_gray);
                buttonMan.setTextColor(Color.parseColor("#777777"));
                gender = "W";
                textViewGenderCheck.setTextColor(Color.parseColor("#1048ff"));
                textViewGenderCheck.setText("성별이 입력되었습니다.");
            }
        });
        buttonMan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                buttonMan.setBackgroundResource(R.drawable.rounded_rectangle_blue);
                buttonMan.setTextColor(Color.parseColor("#1048ff"));
                buttonWoman.setBackgroundResource(R.drawable.rounded_rectangle_gray);
                buttonWoman.setTextColor(Color.parseColor("#777777"));
                gender = "M";
                textViewGenderCheck.setTextColor(Color.parseColor("#1048ff"));
                textViewGenderCheck.setText("성별이 입력되었습니다.");
            }
        });

        buttonRequire.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (!require) {
                    buttonRequire.setImageResource(R.drawable.ic_checked);
                    require = true;
                } else {
                    buttonRequire.setImageResource(R.drawable.ic_unchecked);
                    require = false;
                }
            }
        });
        buttonOption.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (advertisement == "N") {
                    buttonOption.setImageResource(R.drawable.ic_checked);
                    advertisement = "Y";
                } else {
                    buttonOption.setImageResource(R.drawable.ic_unchecked);
                    advertisement = "N";
                }
            }
        });
        TextView txt_privacy = findViewById(R.id.txt_privacy_sign_up);
        txt_privacy.setPaintFlags(txt_privacy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txt_privacy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Intent intent = new Intent(SignupActivity.this, Privacy.class);
                startActivity(intent);
            }
        });
        TextView txt_terms_of_service = findViewById(R.id.txt_terms_of_service);
        txt_terms_of_service.setPaintFlags(txt_terms_of_service.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txt_terms_of_service.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Intent intent = new Intent(SignupActivity.this, Privacy.class);
                startActivity(intent);
            }
        });
        Button buttonSignupSuccess = (Button) findViewById(R.id.btn_signup_success);
        buttonSignupSuccess.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (year!=0 && month!=0 && dayOfMonth!=0&&require) {
                    //가입조건을 다 채우면 전송함.
                    Call<ResponseBody> call1 = apiInterface.saveConsumer(editTextID.getText().toString(), editTextEmail.getText().toString(), editTextName.getText().toString(),
                            editTextPW.getText().toString(), gender, advertisement,
                            Integer.toString(year),
                            Integer.toString(month),
                            Integer.toString(dayOfMonth));
                    call1.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            //CommonResponse commonResponse = response.body();

                            try {
                                if(!response.isSuccessful()) {
                                    String str = response.errorBody().string();
                                    System.out.println(str);
                                    if (response.code() == 204) {

                                    } else if (str.substring(1, str.length() - 1).equals("값 데이터 비어 있습니다.")) {
                                        //Toast.makeText(getApplicationContext(), "다 채우세요.", Toast.LENGTH_SHORT).show();
                                        snackbarPopup("다 채워주세요.");
                                    } else if (str.substring(1, 17).equals("이미 존재하는 아이디 입니다.")) {
                                        //Toast.makeText(getApplicationContext(), "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
                                        snackbarPopup("이미 존재하는 아이디 입니다.");
                                    }
                                }
                                //finish();

                                else if (Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$", editTextPW.getText()) &&
                                        editTextPW.getText().toString().equals(editTextPWCheck.getText().toString()) &&
                                        Pattern.matches("^[A-Za-z0-9]{4,12}$", editTextID.getText()) &&
                                        Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-z]{2,6}$", editTextEmail.getText()) &&
                                        Pattern.matches("^[ㄱ-ㅎ가-힣]{2,10}|[a-zA-Z]{2,20}$", editTextName.getText()) &&
                                        require && (year!=0) && month!=0 && dayOfMonth!=0
                                ) {
                                    Intent intent = new Intent(SignupActivity.this, SignupSuccessActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });


                }
                else{
                    //Toast.makeText(getApplicationContext(), "다 채우세요.", Toast.LENGTH_SHORT).show();
                    snackbarPopup("다 채워주세요.");
                }
            }

        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public void snackbarPopup(String text){
        View coordiView = findViewById(R.id.coordinator_signup);
        Snackbar snackbar = Snackbar.make(coordiView, text, Snackbar.LENGTH_INDEFINITE);

        View view1 = snackbar.getView();
        view1.setBackgroundColor(0x000000);

        Animation animation = AnimationUtils.loadAnimation(SignupActivity.this,R.anim.snackbar);
        view1.setAnimation(animation);


        TextView textView = (TextView) view1.findViewById(R.id.snackbar_text);
        textView.setBackgroundResource(R.drawable.toast_custom_background);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        snackbar.setDuration(5000);


        snackbar.show();

    }
}