package com.zeyo.android.Measure;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.fragment.app.Fragment;

import com.zeyo.android.R;

import static android.content.Context.MODE_PRIVATE;
/*
측정 시작시 나타나는 가이드의 두번째 화면
 */
public class SecondGuideFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    SharedPreferences pref;
    // newInstance constructor for creating fragment with arguments
    public static SecondGuideFragment newInstance(int page, String title) {
        SecondGuideFragment fragment = new SecondGuideFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");

    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guide_fragment2, container, false);
        View view2 = inflater.inflate(R.layout.guide, container, false);
        Button btn_end = (Button) view.findViewById(R.id.btn_end);
        Button btn_ok = (Button) view.findViewById(R.id.guide_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Activity activity = (Activity)getContext();
                activity.finish();
                ((MeasureActivity)MeasureActivity.mContext).txt_prev.setTextColor(0xFFFFFFFF);
                ((MeasureActivity)MeasureActivity.mContext).txt_fab.setTextColor(0xFFFFFFFF);
                ((MeasureActivity)MeasureActivity.mContext).txt_next.setTextColor(0xFFFFFFFF);
                ((MeasureActivity)MeasureActivity.mContext).txt_delete.setTextColor(0xFFFFFFFF);
                ((MeasureActivity)MeasureActivity.mContext).txt_guide.setTextColor(0xFFFFFFFF);
                ((MeasureActivity)MeasureActivity.mContext).bottom.getBackground().mutate().setColorFilter(0xFFFFFFFF, PorterDuff.Mode.MULTIPLY);
                ((MeasureActivity)MeasureActivity.mContext).showLoadingMessage();
            }
        });
        btn_end.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Activity activity = (Activity)getContext();
                activity.finish();
                ((MeasureActivity)MeasureActivity.mContext).txt_prev.setTextColor(0xFFFFFFFF);
                ((MeasureActivity)MeasureActivity.mContext).txt_fab.setTextColor(0xFFFFFFFF);
                ((MeasureActivity)MeasureActivity.mContext).txt_next.setTextColor(0xFFFFFFFF);
                ((MeasureActivity)MeasureActivity.mContext).txt_delete.setTextColor(0xFFFFFFFF);
                ((MeasureActivity)MeasureActivity.mContext).txt_guide.setTextColor(0xFFFFFFFF);
                ((MeasureActivity)MeasureActivity.mContext).bottom.getBackground().mutate().setColorFilter(0xFFFFFFFF, PorterDuff.Mode.MULTIPLY);
                ((MeasureActivity)MeasureActivity.mContext).showLoadingMessage();
            }
        });
        pref = this.getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        CheckBox checkBox;
        checkBox = view.findViewById(R.id.guide_check);
        checkBox.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("guide",true);
                editor.commit();
            }
        });

        return view;
    }
}
