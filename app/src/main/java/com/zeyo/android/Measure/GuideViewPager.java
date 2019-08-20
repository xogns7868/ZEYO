package com.zeyo.android.Measure;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.pm10.library.CircleIndicator;
import com.zeyo.android.R;

/*
측정 시작 눌렀을 때 나오는 가이드 화면의 viewPager
슬라이드 효과를 얻을 수 있으며, indicator로 페이지를 관리한다.
*/
public class GuideViewPager extends AppCompatActivity {
    FragmentPagerAdapter adapterViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        CircleIndicator circleIndicator = (CircleIndicator) findViewById(R.id.circle_indicator);
        circleIndicator.setupWithViewPager(vpPager);
        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {                                  //화면 전환 이벤트 처리
               if(position == 0){                                                         //첫 번째 화면일 때
                   ((MeasureActivity)MeasureActivity.mContext).bottom.getBackground().mutate().setColorFilter(0xFFFFFFFF, PorterDuff.Mode.MULTIPLY);
                   ((MeasureActivity)MeasureActivity.mContext).txt_prev.setTextColor(0xFFFFFFFF);
                   ((MeasureActivity)MeasureActivity.mContext).txt_fab.setTextColor(0xFFFFFFFF);
                   ((MeasureActivity)MeasureActivity.mContext).txt_next.setTextColor(0xFFFFFFFF);
                   ((MeasureActivity)MeasureActivity.mContext).txt_delete.setTextColor(0xFFFFFFFF);
                   ((MeasureActivity)MeasureActivity.mContext).txt_guide.setTextColor(0xFFFFFFFF);
               }
               else{                                                                       //두 번째 화면일 때
                   ((MeasureActivity)MeasureActivity.mContext).bottom.getBackground().mutate().setColorFilter(0x7F000000, PorterDuff.Mode.MULTIPLY);
                   ((MeasureActivity)MeasureActivity.mContext).txt_prev.setTextColor(0x1d1d1d);
                   ((MeasureActivity)MeasureActivity.mContext).txt_fab.setTextColor(0x1d1d1d);
                   ((MeasureActivity)MeasureActivity.mContext).txt_next.setTextColor(0x1d1d1d);
                   ((MeasureActivity)MeasureActivity.mContext).txt_delete.setTextColor(0x1d1d1d);
                   ((MeasureActivity)MeasureActivity.mContext).txt_guide.setTextColor(0x1d1d1d);
               }
            }
        });
    }
    @Override
    public void onBackPressed()
    {
        //뒤로가기 버튼 비활성화
    }
    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return FirstGuideFragment.newInstance(0, "Page # 1");
                case 1:
                    return SecondGuideFragment.newInstance(1, "Page # 2");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }
}