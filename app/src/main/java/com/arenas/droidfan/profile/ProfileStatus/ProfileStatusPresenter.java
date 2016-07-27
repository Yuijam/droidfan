package com.arenas.droidfan.profile.profilestatus;

import android.content.Context;

import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.UserModel;
import com.arenas.droidfan.main.hometimeline.HomeTimelineContract;
import com.arenas.droidfan.main.hometimeline.HomeTimelinePresenter;
import com.arenas.droidfan.service.FanFouService;

/**
 * Created by Arenas on 2016/7/20.
 */
public class ProfileStatusPresenter extends HomeTimelinePresenter implements DataSource.GetUserCallback{

    protected String mUserId;
    protected UserModel mUser;

    public ProfileStatusPresenter() {
    }

    public ProfileStatusPresenter(Context context , HomeTimelineContract.View view , String userId){
        mView = view;
        mFanFouDB = FanFouDB.getInstance(context);
        mApi = AppContext.getApi();
        mContext = context;

        mUserId = userId;
        mView.setPresenter(this);
    }

    protected void loadUser(){
        mFanFouDB.getUserById(mUserId , this);
    }

    @Override
    public void loadStatus() {
        loadUser();
    }

    @Override
    protected void initPaging() {
        p = new Paging();
        p.sinceId = mFanFouDB.getProfileSinceId(mUserId);
        p.count = 20;
    }

    @Override
    protected void startService() {
        FanFouService.getProfileTimeline(mContext , p , mUserId);
    }

    @Override
    public void onUserLoaded(UserModel userModel) {
        mUser = userModel;
        if (!isStatusAvailable()){
            mView.showError("只向关注TA的人公开消息");
            return;
        }
        getStatusList();
    }

    protected void getStatusList(){
        mFanFouDB.getProfileStatusList(mUserId , this);
    }

    protected boolean isFollowing(){
        return mUser.getFollowing() == 1;
    }

    protected boolean isProtected(){
        return mUser.getProtect() == 1 ;
    }

    private boolean isStatusAvailable(){
        return mUserId.equals(AppContext.getAccount()) || isFollowing() || !isProtected();
    }
}
