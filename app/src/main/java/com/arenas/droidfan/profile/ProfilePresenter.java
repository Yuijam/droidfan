package com.arenas.droidfan.profile;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.NetworkUtils;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.UserModel;
import com.arenas.droidfan.service.FanFouService;
import com.arenas.droidfan.users.UserListActivity;

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

    public ProfilePresenter(ProfileContract.View mView , String userId ,Context context) {
        this.mFanFouDB = FanFouDB.getInstance(context);
        this.mView = mView;
        mUserId = userId;
        mContext = context;
    }

    private void fetchUser() {
        FanFouService.getUser(mContext , mUserId);
        Log.d(TAG , "fetchUser!!!");
    }

    private void testFriend(){
        Log.d(TAG , "testFriend!!!!!!!");
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
        mView.hideProgress();
        mUser = userModel;
        initView(userModel);
        Log.d(TAG , "onUserLoaded------->");
    }

    private void loadUser(){
        mFanFouDB.getUserById(mUserId , this);
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
            mView.showFollowState(getFollowState());
            mView.showFoButton();
            mView.showIsFollowing(followText());
        }
    }

    private String getFollowState(){
        if (isFollowing() && mIsFriend){
            return "互相关注中";
        }else if (mIsFriend){
            return "正在关注你";
        }else {
            return "没有关注你";
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

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG , "onReceive!!!!!!1");
        mIsFriend = intent.getBooleanExtra(FanFouService.EXTRA_IS_FRIEND , false);
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

    private boolean isProtected(){
        return mUser.getProtect() == 1;
    }

    @Override
    public void showFollower() {
        if (isProtected() && !isFollowing()){
            mView.showError("只向关注TA的人公开");
        }else {
            UserListActivity.start(mContext , mUserId , UserListActivity.TYPE_FOLLOWERS);
        }
    }

    @Override
    public void showFollowing() {
        if (isProtected() && !isFollowing()){
            mView.showError("只向关注TA的人公开");
        }else {
            UserListActivity.start(mContext, mUserId, UserListActivity.TYPE_FOLLOWING);
        }
    }
}