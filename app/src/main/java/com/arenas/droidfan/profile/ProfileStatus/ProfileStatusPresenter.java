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
public class ProfileStatusPresenter extends HomeTimelinePresenter {

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

    @Override
    public void start() {
        loadStatus();
    }

    @Override
    public void loadStatus() {
        mFanFouDB.getProfileStatusList(mUserId , this);
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
}
