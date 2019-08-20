package com.zeyo.android.ClinetManage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.zeyo.android.R;

import java.util.List;

public class SpinnerCustomAdapter  extends BaseAdapter implements SpinnerAdapter {

    List<String> company;
    Context context;
    String[] colors = {"#13edea","#e20ecd","#15ea0d"};
    String[] colorsback = {"#FF000000","#FFF5F1EC","#ea950d"};

    public SpinnerCustomAdapter(Context context, List<String> company) {
        this.company = company;
        this.context = context;
    }

    @Override
    public int getCount() {
        return company.size();
    }

    @Override
    public Object getItem(int position) {
        return company.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view =  View.inflate(context, R.layout.spinner, null);
        TextView textView = (TextView) view.findViewById(R.id.spinner_text);
        textView.setText(company.get(position));
        return textView;
    }

//    @Override
//    public View getDropDownView(int position, View convertView, ViewGroup parent) {
//
//        View view;
//        view =  View.inflate(context, R.layout.support_simple_spinner_dropdown_item, null);
//
//
//
//        return view;
//    }
}