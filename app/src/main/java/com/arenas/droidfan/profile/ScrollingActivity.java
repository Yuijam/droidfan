package com.arenas.droidfan.profile;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.arenas.droidfan.R;
import com.arenas.droidfan.main.HomeTimeline.HomeTimelineFragment;
import com.arenas.droidfan.main.Message.MessageFragment;
import com.arenas.droidfan.main.Notice.NoticeFragment;
import com.arenas.droidfan.main.TabFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = (ViewPager)findViewById(R.id.view_pager);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        List<String> tabList = new ArrayList<>();
        tabList.add(getString(R.string.home_page));
        tabList.add(getString(R.string.photo_album));
        tabList.add(getString(R.string.favorite));

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new ProfileStatusFragment());
        fragments.add(new PhotoFragment());
        fragments.add(new FavoriteFragment());

        tabLayout.addTab(tabLayout.newTab().setText(tabList.get(0)));//添加tab
        tabLayout.addTab(tabLayout.newTab().setText(tabList.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(tabList.get(2)));
        TabFragmentAdapter fragmentAdapter = new TabFragmentAdapter(getSupportFragmentManager(), tabList ,fragments);
        viewPager.setAdapter(fragmentAdapter);//给ViewPager设置适配器
        tabLayout.setupWithViewPager(viewPager);//将TabLayout和ViewPager关联起来。
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }
}
