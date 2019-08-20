package com.zeyo.android.ClinetManage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zeyo.android.APIClient;
import com.zeyo.android.APIInterface;
import com.zeyo.android.MainActivity;
import com.zeyo.android.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    static int itemPosition;
    APIInterface apiInterface = APIClient.retrofit().create(APIInterface.class);
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewClothes;
        TextView textViewTitle;
        TextView textViewDate;
        TextView textViewSignup;
        ImageButton imageButtonDelete;
        String id;

        MyViewHolder(View view) {
            super(view);
            imageViewClothes = view.findViewById(R.id.btn_myclothes);
            textViewTitle = view.findViewById(R.id.txt_mytitle);
            textViewDate = view.findViewById(R.id.txt_mydate);
            textViewSignup = view.findViewById(R.id.txt_mysignup);
            imageButtonDelete = view.findViewById(R.id.btn_delete_clothes);
        }
    }

    static ArrayList<ClosetModel> closetList;

    RecyclerAdapter(ArrayList<ClosetModel> closetList) {
        this.closetList = closetList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);

        return new MyViewHolder(view);
    }





    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        MyViewHolder myViewHolder = (MyViewHolder) holder;
        Glide.with(myViewHolder.imageViewClothes.getContext()).load(closetList.get(position).drawableId).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.ic_sample).error(R.drawable.ic_sample).override(100,100).into(myViewHolder.imageViewClothes);//캐시관리추가함
        myViewHolder.textViewTitle.setText(closetList.get(position).title);
        myViewHolder.textViewDate.setText(closetList.get(position).date);
        myViewHolder.textViewSignup.setText(closetList.get(position).signup);
        myViewHolder.id=closetList.get(position).id;


        myViewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Context context = v.getContext();
                ((ClosetActivity)ClosetActivity.context).position=position;
                Intent intent = new Intent(context,ClosetDetailActivity.class);
                ((Activity)context).startActivity(intent);


            }
        });

        myViewHolder.imageButtonDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Context context = view.getContext();

                //DialogCustom customDialog = new DialogCustom(context);

                //customDialog.callFunction("delete");

                //리사이클러 아이템에서 딜리트버튼을 누르면 다이얼로그가 뜸.
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("해당 옷을 삭제하시겠습니까?");
                builder.setMessage("삭제한 옷은 다시 복구할 수 없습니다.");
                builder.setPositiveButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                //((ClosetActivity)ClosetActivity.context).snackbarPopup(closetList.get(position).id);
                                ((ClosetActivity)ClosetActivity.context).snackbarPopup("취소");

                            }
                        });
                builder.setNegativeButton("삭제하기",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                //토큰과 아이템 아이디를 전송해서 아이템을 삭제함.
                                Call<String> call = apiInterface.deleteCloset(((MainActivity)MainActivity.context).token,
                                        ((ClosetActivity)ClosetActivity.context).closetList.get(RecyclerAdapter.itemPosition).id);
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        //System.out.println(((ClosetActivity)ClosetActivity.context).closetList.get(RecyclerAdapter.itemPosition).id);
                                        System.out.println(response.isSuccessful());

                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        //System.out.println(((ClosetActivity)ClosetActivity.context).closetList.get(RecyclerAdapter.itemPosition).id);

                                    }
                                });

                                //이게 있어야 앱에서 실시간으로 이쁘게 지워짐.
                                ((ClosetActivity)ClosetActivity.context).closetList.remove(RecyclerAdapter.itemPosition);
                                ((ClosetActivity)ClosetActivity.context).adapter.notifyItemRemoved(RecyclerAdapter.itemPosition);
                                ((ClosetActivity)ClosetActivity.context).adapter.notifyItemRangeChanged(RecyclerAdapter.itemPosition,((ClosetActivity)ClosetActivity.context).closetList.size());
                                ((ClosetActivity)ClosetActivity.context).snackbarPopup("옷 삭제가 완료되었습니다!.");
                            }
                        });
                builder.setCancelable(false).show();
                itemPosition=position;

            }
        });

    }

    @Override
    public int getItemCount() {
        return closetList.size();
    }

}