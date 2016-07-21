package com.arenas.droidfan.profile;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.UserModel;
import com.arenas.droidfan.profile.ProfileStatus.ProfileContract;
import com.arenas.droidfan.service.FanFouService;

/**
 * Created by Arenas on 2016/7/21.
 */
public class ProfilePresenter implements ProfileContract.Presenter , DataSource.GetUserCallback{

    private static final String TAG = ProfilePresenter.class.getSimpleName();

    private FanFouDB mFanFouDB;
    private ProfileContract.View mView;
    private String mUserId;
    private Context mContext;
    private UserModel mUser;

    public ProfilePresenter(FanFouDB mFanFouDB, ProfileContract.View mView , String userId ,Context context) {
        this.mFanFouDB = mFanFouDB;
        this.mView = mView;
        mUserId = userId;
        mContext = context;
    }

    private void fetchUser() {
        Log.d(TAG , "fetchUser()---------->");
        FanFouService.getUser(mContext , mUserId);
    }

    @Override
    public void start() {
        Log.d(TAG , "start()------");
        fetchUser();
    }

    @Override
    public void onUserLoaded(UserModel userModel) {
        mUser = userModel;
        initView(userModel);
    }

    @Override
    public void onDataNotAvailable() {
        mView.showError();
    }

    private void initView(UserModel user){
        mView.showAvatar(user.getProfileImageUrlLarge());
        mView.showUserId(user.getId());
        mView.showFavoriteCount(user.getFavouritesCount());
        mView.showFollowerCount(user.getFollowersCount());
        mView.showFollowingCount(user.getFriendsCount());
        mView.showStatusCount(user.getStatusesCount());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mFanFouDB.getUser(mUserId , this);
    }
}
