package com.zeyo.android.Measure;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.zeyo.android.APIClient;
import com.zeyo.android.APIInterface;
import com.zeyo.android.Drawer.ButtonController;
import com.zeyo.android.R;
import com.zeyo.android.model.MeasureItem.MeasureItemResponse;
import com.zeyo.android.model.subCategory.SubCategoryResponse;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
메인 화면에서 측정 버튼 클릭시 호출되는 클래스.
해당 화면에서 옷장에 추가할 옷의 이름과 사진을 찍고 카테고리를 선택 후 측정을 시작한다.
*/
public class StartMeasure extends AppCompatActivity {
    public ArrayList<String> Measure_item;
    public ArrayList<String> Image_item;
    public ArrayList<String> measureItemId;
    public ArrayList<String> min_scope;
    public ArrayList<String> max_scope;
    public static Context mContext;
    public String sub_id;
    private DrawerLayout drawerLayout;
    private View drawerView;
    private Boolean isPermission = true;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;
    private File tempFile;
    public File file;
    public String Path;
    public String name;
    public ImageButton cloth_button;
    APIInterface apiInterface;
    ArrayAdapter<String> adapter;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_start);
        mContext = this;
        EditText cloth_name = (EditText) findViewById(R.id.cloth_name);
        ButtonController controller = new ButtonController();
        controller.controller((TextView)findViewById(R.id.btn_login), (TextView)findViewById(R.id.txt_before_name),(TextView)findViewById(R.id.txt_after_name), (TextView)findViewById(R.id.txt_login), (Button)findViewById(R.id.btn_question_text),
                 (Button)findViewById(R.id.btn_policy_text), (Button)findViewById(R.id.btn_signup), (TextView)findViewById(R.id.txt_log_out),this);                   //drawer 화면 구성, 버튼 활성화
        drawerLayout = (DrawerLayout) findViewById(R.id.dra_layout);
        drawerView = (View) findViewById(R.id.navi);
        imageView = findViewById(R.id.start_measure_image_body);
        Spinner spinner = findViewById(R.id.spinner_select_category);                   //카테고리 선택 spinner
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });
        final List<String> list = new ArrayList<String>();
        apiInterface = APIClient.retrofit().create(APIInterface.class);
        final Call<List<SubCategoryResponse>> call1 = apiInterface.getCategory();
        call1.enqueue(new Callback<List<SubCategoryResponse>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<List<SubCategoryResponse>> call, Response<List<SubCategoryResponse>> response) {
                List<SubCategoryResponse> subCategoryResponses = response.body();
                List<SubCategoryResponse> subCategoryResponsesId = subCategoryResponses.stream().map(subCategoryResponse -> {
                    SubCategoryResponse subCategoryResponseId = new SubCategoryResponse();
                    subCategoryResponseId.setId(subCategoryResponse.getId());             //response 결과에서 카테고리 ID들을 저장
                    return subCategoryResponseId;
                }).collect(Collectors.toList());
                List<SubCategoryResponse> subCategoryResponsesName = subCategoryResponses.stream().map(subCategoryResponse -> {
                    SubCategoryResponse subCategoryResponseName = new SubCategoryResponse();
                    subCategoryResponseName.setName(subCategoryResponse.getName());       //response 결과에서 카테고리 Name들을 저장
                    return subCategoryResponseName;
                }).collect(Collectors.toList());
                HashMap<String, String> category = new HashMap<>();
                for (int i = 0; i < subCategoryResponses.size(); i++) {
                    list.add(subCategoryResponsesName.get(i).getName());                                                    //해당 리스트는 spinner의 아이템이 된다.
                    category.put(subCategoryResponsesName.get(i).getName(), subCategoryResponsesId.get(i).getId());         //뽑아낸 이름과 ID를 해쉬맵에 넣는다.
                }
                adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {                               //spinner에서 아이템 선택했을 때 이벤트 처리
                    public void onItemSelected(AdapterView<?> parentView,
                                               View selectedItemView, int position, long id) {
                        int pos;
                        pos = spinner.getSelectedItemPosition();
                        String temp_id = subCategoryResponsesId.get(pos).getId();
                       if(temp_id.equals("2"))
                                imageView.setBackgroundResource(R.drawable.img_tshirt);                                 //이미지 추가될 가능성이 상당히 높다. 이미지는 여기다가 추가한다.
                        if(temp_id.equals("3"))
                            imageView.setBackgroundResource(R.drawable.img_cardigan);
                        if(temp_id.equals("5"))
                            imageView.setBackgroundResource(R.drawable.img_jacket);
                        if(temp_id.equals("7"))
                                imageView.setBackgroundResource(R.drawable.img_blouse);
                        if(temp_id.equals("12"))
                                imageView.setBackgroundResource(R.drawable.img_onepiece);
                        if(temp_id.equals("13"))
                                imageView.setBackgroundResource(R.drawable.img_pants);
                        if(temp_id.equals("15"))
                                imageView.setBackgroundResource(R.drawable.img_skirt);

                        sub_id = category.get(list.get(pos));
                        final Call<List<MeasureItemResponse>> call2 = apiInterface.getsubCategory(sub_id);              //선택된 카테고리 ID로 측정 항목 서버에서 받아온다.
                        call2.enqueue(new Callback<List<MeasureItemResponse>>() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onResponse(Call<List<MeasureItemResponse>> call, Response<List<MeasureItemResponse>> response) {
                                List<MeasureItemResponse> measureItemResponses = response.body();
                                Measure_item = new ArrayList<>();                                                         //측정 항목들의 이름이 들어가는 배열
                                Image_item = new ArrayList<>();                                                           //측정 항목들의 이미지들이 들어가는 배열
                                measureItemId = new ArrayList<>();                                                        //측정 항목들의 ID가 들어가는 배열
                                min_scope = new ArrayList<>();                                                             //측정 항목들의 최소 측정 값이 들어가는 배열
                                max_scope = new ArrayList<>();                                                             //측정 항목들의 최대 측정 값이 들어가는 배열
                                //위의 배열들은 MeasureActivity에서 count라는 인덱스로 접근해 측정 항목별로 관리한다.
                                measureItemResponses.forEach(measureItemResponse -> {
                                    Measure_item.add(measureItemResponse.getItemName());
                                    Image_item.add(measureItemResponse.getItemImage());
                                    measureItemId.add(measureItemResponse.getId());
                                    min_scope.add(measureItemResponse.getItemMinScope());
                                    max_scope.add(measureItemResponse.getItemMaxScope());
                                });

                            }
                            @Override
                            public void onFailure(Call<List<MeasureItemResponse>> call, Throwable t) {
                            }
                        });
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
            @Override
            public void onFailure(Call<List<SubCategoryResponse>> call, Throwable t) {
            }
        });
        tedPermission();
        findViewById(R.id.btn_menu).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                drawerLayout.openDrawer(drawerView);
                drawerView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                    }
                });
            }
        });
        findViewById(R.id.start_measure).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if(cloth_name.getText().toString().length() == 0){                          //이름이 비었는지 확인
                    Toast.makeText(getApplicationContext(),"이름을 적어주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(file == null){                                                  //사진이 비었는지 확인
                    Toast.makeText(getApplicationContext(),"옷 사진을 등록해주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }
                name = cloth_name.getText().toString();
                Intent intent = new Intent(getApplicationContext(), MeasureActivity.class);
                StartMeasure.this.finish();
                startActivity(intent);
            }
        });
        cloth_button = findViewById(R.id.add_cloth);
        cloth_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                List<String> pick = new ArrayList<String>();
                pick.add("앨범에서 선택");
                pick.add("카메라 촬영");
                //Create sequence of items
                final CharSequence[] picks = pick.toArray(new String[pick.size()]);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(StartMeasure.this);
                String titleText = "선택";
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.BLUE);
                SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);
                ssBuilder.setSpan(
                        foregroundColorSpan,
                        0,
                        titleText.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                dialogBuilder.setTitle(ssBuilder);
                dialogBuilder.setItems(picks, new DialogInterface.OnClickListener() {                       //앨범과 카메라 중 하나를 선택하는 Dialog 출력
                    public void onClick(DialogInterface dialog, int item) {
                        if(item == 0){
                            if(isPermission) goToAlbum();
                            else Toast.makeText(getApplicationContext(), getResources().getString(R.string.permission_2), Toast.LENGTH_LONG).show();
                        }
                        else if(item == 1){
                            if(isPermission)  takePhoto();
                            else Toast.makeText(getApplicationContext(), getResources().getString(R.string.permission_2), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                AlertDialog alertDialogObject = dialogBuilder.create();
                alertDialogObject.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FROM_ALBUM  && resultCode == RESULT_OK) {
            Uri photoUri = data.getData();
            Cursor cursor = null;
            try {
                String[] proj = { MediaStore.Images.Media.DATA };
                assert photoUri != null;
                cursor = getContentResolver().query(photoUri, proj, null, null, null);
                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                tempFile = new File(cursor.getString(column_index));
                file = tempFile;
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            setImage();
        } else if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK) {
            galleryAddPic();
            setImage();
        }
    }

    //앨범으로 이동하는 함수
    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }
    //카메라로 이동해 사진을 찍는 함수
    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            tempFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (tempFile != null) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Uri photoUri = FileProvider.getUriForFile(this,
                        "com.zeyo.android.provider", tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            } else {
                Uri photoUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            }
        }
    }
    private String cameraFilePath;
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"Camera");
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        cameraFilePath = image.getAbsolutePath();
        file = image;
        Path = cameraFilePath;
        return image;
    }
    //앨범에서 선택하거나, 카메라로 찍은 사진을 등록하는 함수
    private void setImage() {
        cloth_button = findViewById(R.id.add_cloth);
        Glide.with(mContext).load(file.getAbsolutePath()).into(cloth_button);
    }
    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                isPermission = true;

            }
            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                isPermission = false;

            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }
    //카메라에서 촬영한 사진을 앨범에 등록하는 함수
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(cameraFilePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
}