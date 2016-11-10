package com.arenas.droidfan.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.detail.DetailActivity;
import com.arenas.droidfan.main.TabFragmentAdapter;
import com.arenas.droidfan.main.hometimeline.HomeTimelineContract;
import com.arenas.droidfan.profile.favorite.FavoriteFragment;
import com.arenas.droidfan.profile.favorite.FavoritePresenter;
import com.arenas.droidfan.profile.photoalbum.PhotoAlbumFragment;
import com.arenas.droidfan.profile.photoalbum.PhotoAlbumPresenter;
import com.arenas.droidfan.profile.profilestatus.ProfileStatusFragment;
import com.arenas.droidfan.profile.profilestatus.ProfileStatusPresenter;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity implements ProfileContract.View
        , View.OnClickListener , SwipeRefreshLayout.OnRefreshListener , AppBarLayout.OnOffsetChangedListener{

    public static final String TAG = ProfileActivity.class.getSimpleName();
    public static final String EXTRA_USER_ID = "extra_user_id";

    public static void start(Context activity , String userId){
        Intent intent = new Intent(activity , ProfileActivity.class);
        intent.putExtra(EXTRA_USER_ID , userId);
        activity.startActivity(intent);
    }

    @BindView(R.id.iv_avatar) RoundedImageView mAvatar;
    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.tab_layout) TabLayout tabLayout;
//    @BindView(R.id.swipe_container) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progressbar) ProgressBar progressBar;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.collapse_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.app_bar) AppBarLayout appBarLayout;
    @BindView(R.id.profile_content) RelativeLayout profileContent;
    @BindView(R.id.send_dm) ImageView sendDM;
    @BindView(R.id.follower_count) TextView followerCount;
    @BindView(R.id.following_count) TextView followingCount;
    @BindView(R.id.status_count) TextView statusCount;
    @BindView(R.id.favorites_count) TextView favoritesCount;
//    @BindView(R.id.fo_and_unfo) Button follow;
    @BindView(R.id.fo_and_unfo) ImageView follow;
    @BindView(R.id.description) TextView description;
    @BindView(R.id.status) TextView status;
    @BindView(R.id.following) TextView following;
    @BindView(R.id.follower) TextView follower;
    @BindView(R.id.favorites) TextView favorites;

    private ProfileContract.Presenter mPresenter;

    private HomeTimelineContract.View profileStatusView;
    private HomeTimelineContract.View favoriteStatusView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();

        List<String> tabList = new ArrayList<>();
        tabList.add(getString(R.string.tab_status));
        tabList.add(getString(R.string.favorite));
        tabList.add(getString(R.string.photo_album));

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new ProfileStatusFragment());
        fragments.add(new FavoriteFragment());
        fragments.add(new PhotoAlbumFragment());

        tabLayout.addTab(tabLayout.newTab().setText(tabList.get(0)));//添加tab
        tabLayout.addTab(tabLayout.newTab().setText(tabList.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(tabList.get(2)));
        TabFragmentAdapter fragmentAdapter = new TabFragmentAdapter(getSupportFragmentManager(), tabList ,fragments);
        viewPager.setAdapter(fragmentAdapter);//给ViewPager设置适配器
        tabLayout.setupWithViewPager(viewPager);//将TabLayout和ViewPager关联起来。
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        String userId = null;

        if (getIntent().hasExtra(EXTRA_USER_ID)){
            userId = getIntent().getStringExtra(EXTRA_USER_ID);
        }else if (getIntent().getData() != null){
            userId = getIntent().getData().getPathSegments().get(0);
        }

        mPresenter = new ProfilePresenter(this , userId , this);

        profileStatusView = (ProfileStatusFragment)fragmentAdapter.getItem(0);
        favoriteStatusView = (FavoriteFragment)fragmentAdapter.getItem(1);

        new ProfileStatusPresenter(this , profileStatusView , userId);
        new FavoritePresenter(this , favoriteStatusView , userId);
        new PhotoAlbumPresenter(this , (PhotoAlbumFragment)fragmentAdapter.getItem(2) , userId);
    }

    private void initView(){

        followingCount.setOnClickListener(this);
        followerCount.setOnClickListener(this);
        favoritesCount.setOnClickListener(this);
        statusCount.setOnClickListener(this);
        follow.setOnClickListener(this);

        status.setOnClickListener(this);
        following.setOnClickListener(this);
        follower.setOnClickListener(this);
        favorites.setOnClickListener(this);

        sendDM.setOnClickListener(this);

        collapsingToolbarLayout.setTitleEnabled(false);
        appBarLayout.addOnOffsetChangedListener(this);
        int toolbar_height = Utils.getToolbarHeight(this) * 2;
        CollapsingToolbarLayout.LayoutParams params = (CollapsingToolbarLayout.LayoutParams) mToolbar.getLayoutParams();
        params.height = toolbar_height;
        mToolbar.setLayoutParams(params);

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//        Log.d(TAG , "verticalOffset = " + verticalOffset);
//        Log.d(TAG , "appbarLayout.getTotalScrollRange = " + appBarLayout.getTotalScrollRange());
        float alpha = (float) Math.abs(verticalOffset) / (float) appBarLayout.getTotalScrollRange() * 1.0f;
        profileContent.setAlpha(1.0f-alpha);
    }

    @Override
    public void onRefresh() {
        mPresenter.refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
        appBarLayout.addOnOffsetChangedListener(this);
//        Log.d(TAG , "onResume!!!!");
    }

    @Override
    protected void onPause() {
        super.onPause();
        appBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fo_and_unfo:
                mPresenter.follow();
                break;
            case R.id.follower_count:
            case R.id.follower:
                mPresenter.openFollower();
                break;
            case R.id.following_count:
            case R.id.following:
                mPresenter.openFollowing();
                break;
            case R.id.status_count:
            case R.id.status:
                viewPager.setCurrentItem(0);
                break;
            case R.id.favorites_count:
            case R.id.favorites:
                viewPager.setCurrentItem(1);
                break;
            case R.id.send_dm:
                mPresenter.openChatView();
                break;
        }
    }

    @Override
    public void showError(String text) {
        Toast.makeText(this , text , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showAvatar(String url) {
        Picasso.with(this).load(url).into(mAvatar);
    }

    @Override
    public void showUserId(String userId) {
        mToolbar.setSubtitle(userId);
    }

    @Override
    public void showFollowingCount(int count) {
        followingCount.setText(""+count);
    }

    @Override
    public void showFollowerCount(int count) {
        followerCount.setText(""+count);
    }

    @Override
    public void showFavoriteCount(int count) {
        favoritesCount.setText(""+count);
    }

    @Override
    public void showStatusCount(int count) {
        statusCount.setText( ""+count);
    }

    @Override
    public void showTitle(String username) {
        mToolbar.setTitle(username);
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
//        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showFoButton(int resId) {
        follow.setVisibility(View.VISIBLE);
        follow.setImageResource(resId);
//        follow.setText(text);
    }

    @Override
    public void showDescription(String text) {
        description.setText(text);
    }

    @Override
    public void showDMView() {
        sendDM.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DetailActivity.REQUEST_DETAIL){
            if (resultCode == DetailActivity.RESULT_DELETE){
                int type = data.getIntExtra(DetailActivity.EXTRA_STATUS_TYPE , -1);
                int position = data.getIntExtra(DetailActivity.EXTRA_POSITION , -1);
                switch (type){
                    case DetailActivity.TYPE_FAVORITES:
                        favoriteStatusView.removeStatusItem(position);
                        break;
                    case DetailActivity.TYPE_PROFILE:
                        profileStatusView.removeStatusItem(position);
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
