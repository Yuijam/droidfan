package com.arenas.droidfan.profile;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.arenas.droidfan.R;
import com.arenas.droidfan.main.TabFragmentAdapter;
import com.arenas.droidfan.profile.favorite.FavoriteFragment;
import com.arenas.droidfan.profile.favorite.FavoritePresenter;
import com.arenas.droidfan.profile.photoalbum.PhotoAlbumFragment;
import com.arenas.droidfan.profile.photoalbum.PhotoAlbumPresenter;
import com.arenas.droidfan.profile.profilestatus.ProfileStatusFragment;
import com.arenas.droidfan.profile.profilestatus.ProfileStatusPresenter;

import java.util.ArrayList;
import java.util.List;

public class ProfileDetailActivity extends AppCompatActivity {

    private static final String TAG = ProfileDetailActivity.class.getSimpleName();

    public static final String EXTRA_USER_ID = "extra_user_id";
    public static final String EXTRA_PAGE = "extra_page";
    public static final String EXTRA_USERNAME = "extra_username";

    public static void start(Context context , String userId , String username , int page){
        Intent intent = new Intent(context , ProfileDetailActivity.class);
        intent.putExtra(EXTRA_USER_ID , userId);
        intent.putExtra(EXTRA_USERNAME , username);
        intent.putExtra(EXTRA_PAGE , page);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);
        Log.d(TAG , "onCreate()--------");

        String userId = getIntent().getStringExtra(EXTRA_USER_ID);
        int page = getIntent().getIntExtra(EXTRA_PAGE , -1);
        String username = getIntent().getStringExtra(EXTRA_USERNAME);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(username);
        toolbar.setSubtitle("@"+userId);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ViewPager viewPager = (ViewPager)findViewById(R.id.view_pager);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        List<String> tabList = new ArrayList<>();
        tabList.add(getString(R.string.tab_status));
        tabList.add(getString(R.string.photo_album));
        tabList.add(getString(R.string.favorite));

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new ProfileStatusFragment());
        fragments.add(new PhotoAlbumFragment());
        fragments.add(new FavoriteFragment());

        tabLayout.addTab(tabLayout.newTab().setText(tabList.get(0)));//添加tab
        tabLayout.addTab(tabLayout.newTab().setText(tabList.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(tabList.get(2)));
        TabFragmentAdapter fragmentAdapter = new TabFragmentAdapter(getSupportFragmentManager(), tabList ,fragments);
        viewPager.setAdapter(fragmentAdapter);//给ViewPager设置适配器
        tabLayout.setupWithViewPager(viewPager);//将TabLayout和ViewPager关联起来。
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        viewPager.setCurrentItem(page , true);

        new ProfileStatusPresenter(this , (ProfileStatusFragment)fragmentAdapter.getItem(0) , userId);
        new PhotoAlbumPresenter(this , (PhotoAlbumFragment)fragmentAdapter.getItem(1) , userId);
        new FavoritePresenter(this , (FavoriteFragment)fragmentAdapter.getItem(2) , userId);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
