package com.arenas.droidfan.profile.profilestatus;

import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.main.hometimeline.HomeTimelineContract;
import com.arenas.droidfan.main.hometimeline.HomeTimelinePresenter;

/**
 * Created by Arenas on 2016/7/20.
 */
public class ProfileStatusPresenter extends HomeTimelinePresenter {

    private String mUserId;

    public ProfileStatusPresenter(FanFouDB fanFouDB , HomeTimelineContract.View view , String userId){
        mView = view;
        mFanFouDB = fanFouDB;
        mApi = AppContext.getApi();

        mUserId = userId;
        mView.setPresenter(this);
    }

    @Override
    public void loadStatus() {
        mFanFouDB.getProfileStatusList(mUserId , this);
    }

    @Override
    public void refresh() {
        mView.showRefreshBar();
        Paging p = new Paging();
        p.sinceId = mFanFouDB.getProfileSinceId(mUserId);
        p.count = 20;
        mView.startService(p);
    }
}
