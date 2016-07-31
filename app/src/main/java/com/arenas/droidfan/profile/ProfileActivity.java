package com.arenas.droidfan.profile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arenas.droidfan.R;
import com.arenas.droidfan.service.FanFouService;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity implements ProfileContract.View
        , View.OnClickListener , SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = ProfileActivity.class.getSimpleName();
    public static final String EXTRA_USER_ID = "extra_user_id";

    public static void start(Context context , String userId){
        Intent intent = new Intent(context , ProfileActivity.class);
        intent.putExtra(EXTRA_USER_ID , userId);
        context.startActivity(intent);
    }

    private RoundedImageView mAvatar;
    private TextView mStatusCount;
    private TextView mFollowingCount;
    private TextView mFollowerCount;
    private TextView mFavoritesCount;
    private Button mFollow;
    private TextView mFollowMe;
    private TextView mDescription;
    private SwipeRefreshLayout mSwipeRefresh;
    private ImageView mLock;

    private IntentFilter mIntentFilter;
    private LocalBroadcastManager mLocalBroadcastManager;
    private LocalReceiver mLocalReceiver;

    private ProfileContract.Presenter mPresenter;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(FanFouService.FILTER_PROFILE);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mLocalReceiver = new LocalReceiver();
        mLocalBroadcastManager.registerReceiver(mLocalReceiver, mIntentFilter);

        String userId = null;

        if (getIntent().hasExtra(EXTRA_USER_ID)){
            userId = getIntent().getStringExtra(EXTRA_USER_ID);
        }else if (getIntent().getData() != null){
            userId = getIntent().getData().getPathSegments().get(0);
        }

        mPresenter = new ProfilePresenter(this , userId , this);
    }

    private void initView(){
        mAvatar = (RoundedImageView)findViewById(R.id.iv_avatar);
        mFollowingCount = (TextView)findViewById(R.id.following);
        mFollowingCount.setOnClickListener(this);
        mFollowerCount = (TextView)findViewById(R.id.follower);
        mFollowerCount.setOnClickListener(this);

        mFavoritesCount = (TextView)findViewById(R.id.favorites);
        mFavoritesCount.setOnClickListener(this);
        mStatusCount = (TextView)findViewById(R.id.status_count);
        mStatusCount.setOnClickListener(this);
        mFollow = (Button)findViewById(R.id.fo_and_unfo);
        mFollowMe = (TextView)findViewById(R.id.follow_me);
        mFollow.setOnClickListener(this);
        mDescription = (TextView)findViewById(R.id.description);

        mSwipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        mLock = (ImageView)findViewById(R.id.lock);
    }

    @Override
    public void onRefresh() {
        mPresenter.refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
        Log.d(TAG , "onResume!!!!1");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fo_and_unfo:
                mPresenter.follow();
                break;
            case R.id.follower:
                mPresenter.openFollower();
                break;
            case R.id.following:
                mPresenter.openFollowing();
                break;
            case R.id.status_count:
                mPresenter.openStatus();
                break;
            case R.id.favorites:
                mPresenter.openFavorites();
                break;
        }
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mPresenter.onReceive(context , intent);
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
        mFollowingCount.setText(""+count);
    }

    @Override
    public void showFollowerCount(int count) {
        mFollowerCount.setText(""+count);
    }

    @Override
    public void showFavoriteCount(int count) {
        mFavoritesCount.setText(""+count);
    }

    @Override
    public void showStatusCount(int count) {
        mStatusCount.setText( ""+count);
    }

    @Override
    public void showTitle(String username) {
        mToolbar.setTitle(username);
    }

    @Override
    public void showFollowState(String text) {
        mFollowMe.setVisibility(View.VISIBLE);
        mFollowMe.setText(text);
    }

    @Override
    public void showProgress() {
        mSwipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        mSwipeRefresh.setRefreshing(false);
    }

    @Override
    public void showFoButton() {
        mFollow.setVisibility(View.VISIBLE);
    }

    @Override
    public void showIsFollowing(String text) {
        mFollow.setText(text);
    }

    @Override
    public void showDescription(String text) {
        mDescription.setText(text);
    }

    @Override
    public void showLock() {
        mLock.setVisibility(View.VISIBLE);
    }
}
