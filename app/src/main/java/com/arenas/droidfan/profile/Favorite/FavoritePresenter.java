package com.arenas.droidfan.profile.favorite;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.main.hometimeline.HomeTimelineContract;
import com.arenas.droidfan.main.hometimeline.HomeTimelinePresenter;

/**
 * Created by Arenas on 2016/7/21.
 */
public class FavoritePresenter extends HomeTimelinePresenter {

    private static final String TAG = FavoritePresenter.class.getSimpleName();

    private String mUserId;

    public FavoritePresenter(FanFouDB fanFouDB , HomeTimelineContract.View view , String userId){
        mView = view;
        mFanFouDB = fanFouDB;
        mApi = AppContext.getApi();
        mUserId = userId;

        mView.setPresenter(this);
    }

    @Override
    public void loadStatus() {
        mFanFouDB.getFavoritesList(mUserId , this);
    }

    @Override
    public void refresh() {
        mView.showRefreshBar();
        Paging p = new Paging();
        p.sinceId = mFanFouDB.getFavoritesSinceId(mUserId);
        p.count = 20;
        mView.startService(p);
    }
}
