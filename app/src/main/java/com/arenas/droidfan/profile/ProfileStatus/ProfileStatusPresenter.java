package com.arenas.droidfan.profile.ProfileStatus;

import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.main.HomeTimeline.HomeTimelineContract;
import com.arenas.droidfan.main.HomeTimeline.HomeTimelinePresenter;

/**
 * Created by Arenas on 2016/7/20.
 */
public class ProfileStatusPresenter extends HomeTimelinePresenter {

    public ProfileStatusPresenter(FanFouDB fanFouDB , HomeTimelineContract.View view){
        mView = view;
        mFanFouDB = fanFouDB;
        mApi = AppContext.getApi();

        mView.setPresenter(this);
    }

    @Override
    public void loadStatus() {
        mFanFouDB.getProfileStatusList(this);
    }

    @Override
    public void refresh() {
        mView.showRefreshBar();
        Paging p = new Paging();
        if (AppContext.isFirstLoad()){
            mView.startService(p);
            AppContext.setFirstLoad(false);
        }else {
            p.sinceId = mFanFouDB.getProfileSinceId();
            mView.startService(p);
        }
    }
}
