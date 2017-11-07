package com.qingwing.safebox.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.qingwing.safebox.R;
import com.qingwing.safebox.fragment.CheckFeedBackFragment;
import com.qingwing.safebox.fragment.FeedBackFragment;
import com.qingwing.safebox.utils.AcitivityCollector;
import com.qingwing.safebox.view.viewPagerIncator;

import java.util.ArrayList;
import java.util.List;

public class FeedBackActivity extends FragmentActivity {

    private ImageView about_app_exit;
    private viewPagerIncator pagerIncator;
    private ViewPager viewPager;
    private List<String> mtitles = new ArrayList<String>();
    private List<Fragment> contentFragment = new ArrayList<Fragment>();
    private FragmentPagerAdapter mFragmentPagerAdapter;
    private FeedBackFragment mFeedBackFragment;
    private CheckFeedBackFragment mCheckFeedBackFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AcitivityCollector.addActivity(this);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AcitivityCollector.removeActivity(this);
    }

    private void initData() {
        mtitles.add("填写反馈");
        mtitles.add("历史记录");
        contentFragment.add(mFeedBackFragment);
        contentFragment.add(mCheckFeedBackFragment);
        about_app_exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return contentFragment.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return contentFragment.get(arg0);
            }
        };

        pagerIncator.setVisibleTab(contentFragment.size());
        pagerIncator.setTabItemTitle(mtitles);
        viewPager.setAdapter(mFragmentPagerAdapter);
        pagerIncator.setViewPager(viewPager, 0);
    }

    private void initView() {
        setContentView(R.layout.activity_feedback);
        about_app_exit = (ImageView) findViewById(R.id.calback);
        pagerIncator = (viewPagerIncator) findViewById(R.id.viewpagerIncator);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        mFeedBackFragment = new FeedBackFragment();
        mCheckFeedBackFragment = new CheckFeedBackFragment();
    }

}
