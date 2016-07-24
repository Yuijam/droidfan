package com.arenas.droidfan.profile;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.UserModel;
import com.arenas.droidfan.service.FanFouService;

/**
 * Created by Arenas on 2016/7/22.
 */
public class ProfilePresenter implements ProfileContract.Presenter , DataSource.GetUserCallback{

    private static final String TAG = ProfilePresenter.class.getSimpleName();

    private FanFouDB mFanFouDB;
    private ProfileContract.View mView;
    private String mUserId;
    private Context mContext;
    private UserModel mUser;
    private boolean mIsFriend;
    private boolean mSuccess;

    public ProfilePresenter(FanFouDB mFanFouDB, ProfileContract.View mView , String userId ,Context context) {
        this.mFanFouDB = mFanFouDB;
        this.mView = mView;
        mUserId = userId;
        mContext = context;
    }

    private void fetchUser() {
        FanFouService.getUser(mContext , mUserId);
    }

    private void testFriend(){
        FanFouService.isFriend(mContext , mUserId , AppContext.getAccount());
    }

    @Override
    public void start() {
        mView.showProgress();
        if (!isMe()){
            testFriend();
        }
        loadUser();
    }

    private boolean isMe(){
        return mUserId.equals(AppContext.getAccount());
    }

    @Override
    public void onUserLoaded(UserModel userModel) {
        mUser = userModel;
        initView(userModel);
        mView.hideProgress();
    }

    private void loadUser(){
        mFanFouDB.getUser(mUserId , this);
    }

    @Override
    public void onDataNotAvailable() {
        fetchUser();
    }

    private void initView(UserModel user){
        mView.showAvatar(user.getProfileImageUrlLarge());
        mView.showUserId("@"+user.getId());
        mView.showFavoriteCount(user.getFavouritesCount());
        mView.showFollowerCount(user.getFollowersCount());
        mView.showFollowingCount(user.getFriendsCount());
        mView.showStatusCount(user.getStatusesCount());
        mView.showTitle(user.getScreenName());
        mView.showLocation(getLocation());
        mView.showBirthday(getBirthday());
        if (!isMe()){
            mView.showFollowMe(isFollowMe());
            mView.showFoButton();
            mView.showIsFollowing(followText());
        }
    }

    private String followText(){
        return isFollowing() ? "正在关注" : "添加关注";
    }

    private boolean isFollowing(){
        return mUser.getFollowing() == 1;
    }

    private String getLocation(){
        return TextUtils.isEmpty(mUser.getLocation()) ? "未知" : mUser.getLocation();
    }

    private String getBirthday(){
        return TextUtils.isEmpty(mUser.getBirthday()) ? "未知" : mUser.getBirthday();
    }

    private String isFollowMe(){
        return mIsFriend ? "正在关注你" : "没有关注你";
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mIsFriend = intent.getBooleanExtra(FanFouService.EXTRA_IS_FRIEND , false);
//        mSuccess = intent.getBooleanExtra(FanFouService.EXTRA_SUCCESS , false);
        Log.d(TAG , "profile presenter onReceive-------");
        loadUser();
    }

    @Override
    public void follow() {
        if (isFollowing()){
            FanFouService.unfollow(mContext , mUserId);
        }else {
            FanFouService.follow(mContext , mUserId);
        }
    }

    @Override
    public void showFollower() {

    }

    @Override
    public void showFollowing() {

    }
}