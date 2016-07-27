package com.arenas.droidfan.profile.photoalbum;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.main.hometimeline.HomeTimelineContract;
import com.arenas.droidfan.profile.profilestatus.ProfileStatusPresenter;

/**
 * Created by Arenas on 2016/7/27.
 */
public class PhotoPresenter extends ProfileStatusPresenter {

    public PhotoPresenter(FanFouDB fanFouDB , HomeTimelineContract.View view , String userId){
        mView = view;
        mFanFouDB = fanFouDB;
        mApi = AppContext.getApi();
        mUserId = userId;

        mView.setPresenter(this);
    }

    @Override
    public void refresh() {
        mView.showRefreshBar();
        Paging p = new Paging();
        p.sinceId = mFanFouDB.getPhotoSinceId(mUserId);
        p.count = 20;
        mView.startService(p);
    }

    @Override
    protected void getStatusList() {
        mFanFouDB.loadPhotoTimeline(mUserId , this);
    }

}
