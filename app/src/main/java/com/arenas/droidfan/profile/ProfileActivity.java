package com.arenas.droidfan.profile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.main.HomeTimeline.HomeTimelineFragment;
import com.arenas.droidfan.main.TabFragmentAdapter;
import com.arenas.droidfan.profile.Favorite.FavoriteFragment;
import com.arenas.droidfan.profile.Favorite.FavoritePresenter;
import com.arenas.droidfan.profile.PhotoAlbum.PhotoFragment;
import com.arenas.droidfan.profile.ProfileStatus.ProfileStatusFragment;
import com.arenas.droidfan.profile.ProfileStatus.ProfileStatusPresenter;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.IllegalFormatFlagsException;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements ProfileContract.View{

    public static final String TAG = ProfileActivity.class.getSimpleName();
    public static final String EXTRA_USER_ID = "extra_user_id";

    public static void start(Context context , String userId){
        Intent intent = new Intent(context , ProfileActivity.class);
        intent.putExtra(EXTRA_USER_ID , userId);
        context.startActivity(intent);
    }

    private RoundedImageView mAvatar;
    private TextView mUserId;
    private TextView mStatusCount;
    private TextView mFollowingCount;
    private TextView mFollowerCount;
    private TextView mFavoritesCount;

    private IntentFilter mIntentFilter;
    private LocalBroadcastManager mLocalBroadcastManager;
    private LocalReceiver mLocalReceiver;

    private ProfileContract.Presenter mPresenter;

    private boolean isFirstLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(AppContext.getScreenName());
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = (ViewPager)findViewById(R.id.view_pager);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        List<String> tabList = new ArrayList<>();
        tabList.add(getString(R.string.tab_status));
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

        mAvatar = (RoundedImageView)findViewById(R.id.iv_avatar);
        mFollowingCount = (TextView)findViewById(R.id.following);
        mFollowerCount = (TextView)findViewById(R.id.follower);
        mFavoritesCount = (TextView)findViewById(R.id.favorites);
        mStatusCount = (TextView)findViewById(R.id.status_count);
        mUserId = (TextView)findViewById(R.id.user_id);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(HomeTimelineFragment.FILTER_PROFILETIMELINE);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mLocalReceiver = new LocalReceiver();
        mLocalBroadcastManager.registerReceiver(mLocalReceiver, mIntentFilter);

        mPresenter = new ProfilePresenter(FanFouDB.getInstance(this) , this , getIntent().getStringExtra(EXTRA_USER_ID) , this);

        new ProfileStatusPresenter(FanFouDB.getInstance(this) , (ProfileStatusFragment)fragmentAdapter.getItem(0));
        new FavoritePresenter(FanFouDB.getInstance(this) , (FavoriteFragment)fragmentAdapter.getItem(2));

        isFirstLoad = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirstLoad){
            mPresenter.start();
            isFirstLoad = false;
        }
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mPresenter.onReceive(context , intent);
        }
    }

    @Override
    public void showError() {
        Toast.makeText(this , getString(R.string.error) , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showAvatar(String url) {
        Picasso.with(this).load(url).into(mAvatar);
    }

    @Override
    public void showUserId(String userId) {
        mUserId.setText(userId);
    }

    @Override
    public void showFollowingCount(int count) {
        mFollowingCount.setText("正在关注：" + count);
    }

    @Override
    public void showFollowerCount(int count) {
        mFollowerCount.setText("关注者：" + count);
    }

    @Override
    public void showFavoriteCount(int count) {
        mFavoritesCount.setText("收藏数:" + count);
    }

    @Override
    public void showStatusCount(int count) {
        mStatusCount.setText("消息数：" + count);
    }
}
