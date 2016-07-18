package com.arenas.droidfan.main;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.arenas.droidfan.Api.Api;
import com.arenas.droidfan.Api.Paging;
import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.main.HomeTimeline.HomeTimelineContract;

import java.util.List;

/**
 * Created by Arenas on 2016/7/18.
 */
public abstract class StatusPresenter implements HomeTimelineContract.Presenter , DataSource.LoadStatusCallback{

    protected StatusContract.View mView;
    protected FanFouDB mFanFouDB;
    protected Api mApi;

    @Override
    public void start() {
        refresh();
    }

    @Override
    public abstract void loadStatus();

    @Override
    public void onStatusLoaded(List<StatusModel> status) {
        mView.showStatus(status);
    }

    @Override
    public void refresh() {
        mView.showRefreshBar();
        Paging p = new Paging();
        if (AppContext.isFirstLoad()){
            mView.startService(p);
            AppContext.setFirstLoad(false);
        }else {
            p.count = 5;
            p.sinceId = mFanFouDB.getSinceId();
            mView.startService(p);
        }
    }

    @Override
    public void newStatus() {
        mView.showUpdateStatusUi();
    }

    @Override
    public void onDataNotAvailable() {
        mView.showError();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mView.hideRefreshBar();
        loadStatus();
    }
}
