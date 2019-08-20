package com.zeyo.android.Measure;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.zeyo.android.R;
/*
측정 가이드 세번째 화면
 */
public class InnerThirdGuide extends Fragment {
    // Store instance variables
    private String title;
    private int page;

    // newInstance constructor for creating fragment with arguments
    public static InnerThirdGuide newInstance(int page, String title) {
        InnerThirdGuide fragment = new InnerThirdGuide();
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
        View view = inflater.inflate(R.layout.activity_inner_third, container, false);
        ImageView imageView = view.findViewById(R.id.measure_guide_image);
        Glide.with(this).load(((StartMeasure)StartMeasure.mContext).file.getAbsolutePath()).into(imageView);
        return view;
    }
}
