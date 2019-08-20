package com.zeyo.android.ClinetManage;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.zeyo.android.APIClient;
import com.zeyo.android.APIInterface;
import com.zeyo.android.MainActivity;
import com.zeyo.android.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//zeplin보고 오해해서 만들었음. 사용안함.
public class DialogCustom {

    Context context;

    public DialogCustom(Context context) {
        this.context = context;


    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction(String type) {
        float x = ((MainActivity)MainActivity.context).width;
        float y = ((MainActivity)MainActivity.context).height;
        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        if(type.equals("delete")) {
            dlg.setContentView(R.layout.activity_popup);
            WindowManager.LayoutParams params = dlg.getWindow().getAttributes();
            params.width = (int) x;
            params.height = (int) y;
            dlg.getWindow().setAttributes(params);


            // 커스텀 다이얼로그를 노출한다.
            dlg.show();

            // 커스텀 다이얼로그의 각 위젯들을 정의한다.

            final Button btn_cancle = (Button) dlg.findViewById(R.id.btn_cancle_popup);
            final Button btn_delete = (Button) dlg.findViewById(R.id.btn_delete_popup);

            btn_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // '확인' 버튼 클릭시 메인 액티비티에서 설정한 main_label에
                    // 커스텀 다이얼로그에서 입력한 메시지를 대입한다.

                    // 커스텀 다이얼로그를 종료한다.
                    dlg.dismiss();
                }
            });

            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View layout = inflater.inflate(R.layout.activity_toast, null);
                    Toast toast = new Toast(context);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                    //System.out.println(((ClosetActivity)ClosetActivity.context).closetList.get(RecyclerAdapter.itemPosition).id);

                    APIInterface apiInterface = APIClient.retrofit().create(APIInterface.class);

                    Call<String> call = apiInterface.deleteCloset(((MainActivity)MainActivity.context).token,
                            ((ClosetActivity)ClosetActivity.context).closetList.get(RecyclerAdapter.itemPosition).id);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            //System.out.println(((ClosetActivity)ClosetActivity.context).closetList.get(RecyclerAdapter.itemPosition).id);

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            //System.out.println(((ClosetActivity)ClosetActivity.context).closetList.get(RecyclerAdapter.itemPosition).id);

                        }
                    });

                    ((ClosetActivity)ClosetActivity.context).closetList.remove(RecyclerAdapter.itemPosition);
                    ((ClosetActivity)ClosetActivity.context).adapter.notifyItemRemoved(RecyclerAdapter.itemPosition);
                    ((ClosetActivity)ClosetActivity.context).adapter.notifyItemRangeChanged(RecyclerAdapter.itemPosition,((ClosetActivity)ClosetActivity.context).closetList.size());
                    // 커스텀 다이얼로그를 종료한다.
                    dlg.dismiss();
                }
            });
        }

    }
}