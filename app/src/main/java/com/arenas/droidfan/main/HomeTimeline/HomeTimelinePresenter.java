package com.arenas.droidfan.main.HomeTimeline;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.arenas.droidfan.Api.Api;
import com.arenas.droidfan.Api.Paging;
import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.StatusModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arenas on 2016/7/9.
 */
public class HomeTimelinePresenter implements HomeTimelineContract.Presenter , DataSource.LoadStatusCallback{

    private static final String TAG = HomeTimelinePresenter.class.getSimpleName();

    private final HomeTimelineContract.View mView;
    private final FanFouDB mFanFouDB;
    private final Api mApi;


    public HomeTimelinePresenter(FanFouDB fanFouDB , HomeTimelineContract.View view){
        mView = view;
        mFanFouDB = fanFouDB;
        mApi = AppContext.getApi();
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        refresh();
    }

    @Override
    public void loadStatus() {
        mFanFouDB.getStatusList(this);
    }


    @Override
    public void onStatusLoaded(List<StatusModel> status) {
        Log.d(TAG , "status size = " + status.size());
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
