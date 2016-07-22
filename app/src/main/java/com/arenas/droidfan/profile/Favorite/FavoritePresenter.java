package com.arenas.droidfan.profile.favorite;

import android.util.Log;

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

    public FavoritePresenter(FanFouDB fanFouDB , HomeTimelineContract.View view){
        mView = view;
        mFanFouDB = fanFouDB;
        mApi = AppContext.getApi();

        mView.setPresenter(this);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void loadStatus() {
        mFanFouDB.getFavoritesList(this);
    }

    @Override
    public void refresh() {
        mView.showRefreshBar();
        Paging p = new Paging();
        p.sinceId = mFanFouDB.getFavoritesSinceId();
        mView.startService(p);
    }
}
