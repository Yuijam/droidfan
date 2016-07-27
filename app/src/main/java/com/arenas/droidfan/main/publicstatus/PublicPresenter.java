package com.arenas.droidfan.main.publicstatus;

import android.content.Context;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.main.hometimeline.HomeTimelineContract;
import com.arenas.droidfan.main.hometimeline.HomeTimelinePresenter;
import com.arenas.droidfan.service.FanFouService;

/**
 * Created by Arenas on 2016/7/18.
 */
public class PublicPresenter extends HomeTimelinePresenter {

    public PublicPresenter(Context context , HomeTimelineContract.View view){
        mView = view;
        mFanFouDB = FanFouDB.getInstance(context);
        mContext = context;
        mApi = AppContext.getApi();
        mView.setPresenter(this);
    }

    @Override
    public void loadStatus() {
        mFanFouDB.getPublicStatusList(this);
    }

    @Override
    protected void startService() {
        FanFouService.getPublicTimeline(mContext);
    }
}
