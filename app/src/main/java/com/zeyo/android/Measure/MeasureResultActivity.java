package com.zeyo.android.Measure;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.zeyo.android.APIClient;
import com.zeyo.android.APIInterface;
import com.zeyo.android.ClinetManage.ClosetActivity;
import com.zeyo.android.LoadingActivity;
import com.zeyo.android.MainActivity;
import com.zeyo.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
측정 결과가 출력되는 클래스. 재촬영버튼과 저장 버튼이 존재한다. 재촬영버튼 클릭 시 MeasureActivity가 다시 호출된다.
저장 버튼 클릭 시 옷장에 옷이 저장되며 메인화면, 옷장화면으로 이동할 수 있는 Dialog가 출력된다.
옷장에 옷이 저장될 때 인터넷 환경에 따라 지체될 가능성이 존재해 ProgressDialog를 출력시켜 로딩 메시지를 보여준다.
 */
public class MeasureResultActivity extends AppCompatActivity {
    LinearLayout Res;
    LinearLayout Res_category;
    LinearLayout Res_cm;
    APIInterface apiInterface;
    Intent get_intent;
    int item_size;
    int result[];
    ArrayList<String> Measure_item;
    ImageView imageView;
    String temp_file;
    String name;
    Button button_save;
    Button button_again;
    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceSate) {
        super.onCreate(savedInstanceSate);
        setContentView(R.layout.measure_result);
        get_intent = getIntent();
        name = ((StartMeasure)StartMeasure.mContext).name;
        Measure_item = ((StartMeasure) StartMeasure.mContext).Measure_item;
        item_size = Measure_item.size();
        result = new int[item_size];
        result = get_intent.getIntArrayExtra("text");                                 //측정 항목당 결과값 저장
        Res = (LinearLayout) findViewById(R.id.dynamicArea);                                   //측정 항목 출력되는 화면
        Res_category = (LinearLayout) findViewById(R.id.measure_category_dynamic);         //측정 항목당 결과 출력 화면
        button_again = findViewById(R.id.measure_again);                                     //재촬영 버튼
        button_save = findViewById(R.id.save_measure_result);                               //저장 버튼
        Res_cm = findViewById(R.id.cm_area);                                                   //cm 단위 출력 화면
        imageView = findViewById(R.id.measure_result_image);                                //측정 시작 당시 촬영한 옷 사진 출력
        Glide.with(this).load(((StartMeasure)StartMeasure.mContext).file.getAbsolutePath()).into(imageView);
        TextView res[] = new TextView[item_size];
        TextView res_category[] = new TextView[item_size];
        TextView res_cm[] = new TextView[item_size];
        for (int i = 0; i < item_size; i++) {                                                  //동적으로 내용이 추가되면서 텍스트 출력, 측정 항목, 결과, cm(단위) 출력
            int temp = result[i];
            res[i] = new TextView(MeasureResultActivity.this);
            res_category[i] = new TextView(MeasureResultActivity.this);
            res_cm[i] = new TextView(MeasureResultActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            res_category[i].setLayoutParams(lp);
            res_category[i].setBackgroundColor(Color.parseColor("#FFFFFF"));
            res_category[i].setTextSize(16);
            res_category[i].setText(Measure_item.get(i) + "\n");
            res_category[i].setTextColor(Color.parseColor("#1d1d1d"));
            res[i].setLayoutParams(lp);
            res[i].setBackgroundColor(Color.parseColor("#FFFFFF"));
            res[i].setTextSize(16);
            res[i].setText(Integer.toString(temp) + "\n");
            res[i].setTextColor(Color.parseColor("#3464ff"));
            res_cm[i].setLayoutParams(lp);
            res_cm[i].setBackgroundColor(Color.parseColor("#FFFFFF"));
            res_cm[i].setTextSize(16);
            res_cm[i].setText("cm\n");
            res_cm[i].setTextColor(Color.parseColor("#777777"));
            Res.addView(res[i]);
            Res_category.addView(res_category[i]);
            Res_cm.addView(res_cm[i]);
        }
        button_again.setOnClickListener(new View.OnClickListener() {                //재촬영 버튼 클릭 이벤트
            public void onClick(View arg0) {
                finish();
                Intent intent = new Intent(getApplicationContext(), MeasureActivity.class);
                startActivity(intent);
            }
        });
        final ProgressDialog progressDialog = new ProgressDialog(MeasureResultActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("옷장 저장 중입니다...");
        button_save.setOnClickListener(new View.OnClickListener() {                 //저장 버튼 클릭 이벤트
            public void onClick(View arg0) {
                if(((MainActivity) MainActivity.context).token!=null || ((LoadingActivity)LoadingActivity.context).token != null) {
                    progressDialog.show();
                    apiInterface = APIClient.retrofit().create(APIInterface.class);
                    File compressedImageFile = null;
                    try {
                        compressedImageFile = new Compressor(getApplicationContext()).compressToFile( ((StartMeasure) StartMeasure.mContext).file);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), compressedImageFile);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("file", ((StartMeasure) StartMeasure.mContext).file.getName(), requestFile);
                    final Call<String> call1 = apiInterface.uploadFile(((MainActivity) MainActivity.context).token, body);                  //사진 업로드 temp/upload와 연결 되어있다.
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            try {
                                Response<String> list = call1.execute();
                                temp_file = list.body();
                            } catch (IOException e) {
                            }
                            return null;
                        }
                        //temp/upload에 정상적으로 사진이 추가되면 JsonObject를 생성해 옷장 추가에 필요한 body를 생성한다.
                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("id", 0);
                                jsonObject.put("name", ((StartMeasure)StartMeasure.mContext).name);
                                jsonObject.put("subCategoryId", ((StartMeasure) StartMeasure.mContext).sub_id);
                                jsonObject.put("registerType", "D");
                                JSONObject file_object = new JSONObject();
                                file_object.put("temp_name", temp_file);
                                file_object.put("real_name", ((StartMeasure) StartMeasure.mContext).file.getName());
                                JSONArray attach_File = new JSONArray();
                                attach_File.put(file_object);
                                jsonObject.put("attachFile", attach_File);
                                JSONObject measure;
                                JSONArray wardrobe = new JSONArray();
                                for (int i = 0; i < ((StartMeasure) StartMeasure.mContext).measureItemId.size(); i++) {
                                    measure = new JSONObject();
                                    measure.put("measureItemId", ((StartMeasure)StartMeasure.mContext).measureItemId.get(i));
                                    measure.put("value",Math.round(result[i]));
                                    wardrobe.put(measure);
                                }
                                jsonObject.put("wardrobeScmmValueRequests", wardrobe);                  //jsonObject을 생성한다.
                                RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                                apiInterface = APIClient.retrofit().create(APIInterface.class);
                                Call<JSONObject> Call2 = apiInterface.addCloth(((LoadingActivity)LoadingActivity.context).token, bodyRequest);       //생성한 jsonObject를 body로 하는 post
                                Call2.enqueue(new Callback<JSONObject>() {
                                    @Override
                                    public void onResponse(Call<JSONObject> call, Response<JSONObject> response2) {
                                        // CommonResponse commonResponse = response.body();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(MeasureResultActivity.this);
                                        progressDialog.dismiss();
                                        builder.setCancelable(false);
                                        builder.setMessage("내 옷장에 저장되었습니다.");
                                        builder.setNegativeButton("메인",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        MeasureResultActivity.this.finish();
                                                    }
                                                });
                                        builder.setPositiveButton("옷장으로",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        MeasureResultActivity.this.finish();
                                                        MeasureResultActivity.this.startActivity(new Intent(MeasureResultActivity.this, ClosetActivity.class));
                                                    }
                                                });
                                        builder.show();
                                    }

                                    @Override
                                    public void onFailure(Call<JSONObject> call, Throwable t) {
                                        Toast.makeText(getApplicationContext(),"로그인 정보가 초기화되었습니다. 다시 로그인 해주세요.",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }.execute();
                }
                else{
                    Toast.makeText(getApplicationContext(),"로그인이 필요합니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}