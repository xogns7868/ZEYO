package com.zeyo.android.Measure;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.zeyo.android.R;
/*
측정 시작시 나타나는 가이드의 첫번째 화면
 */
public class FirstGuideFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;

    // newInstance constructor for creating fragment with arguments
    public static FirstGuideFragment newInstance(int page, String title) {
        FirstGuideFragment fragment = new FirstGuideFragment();
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
        View view = inflater.inflate(R.layout.guide_fragment1, container, false);
        ImageView imageView = (ImageView)view.findViewById(R.id.combined);
        Animation ani = AnimationUtils.loadAnimation(getContext(), R.anim.blink_anim);
        imageView.startAnimation(ani);
        Button btn_end = (Button) view.findViewById(R.id.btn_end);
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
            }
        });
       return view;
    }
}