package com.arenas.droidfan.main.publicstatus;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.main.hometimeline.HomeTimelineContract;
import com.arenas.droidfan.main.hometimeline.HomeTimelinePresenter;

/**
 * Created by Arenas on 2016/7/18.
 */
public class PublicPresenter extends HomeTimelinePresenter {

    public PublicPresenter(FanFouDB fanFouDB , HomeTimelineContract.View view){
        mView = view;
        mFanFouDB = fanFouDB;
        mApi = AppContext.getApi();
        mView.setPresenter(this);
    }

    @Override
    public void loadStatus() {
        mFanFouDB.getPublicStatusList(this);
    }

    @Override
    public void refresh() {
        mView.startService(null);
    }
}
