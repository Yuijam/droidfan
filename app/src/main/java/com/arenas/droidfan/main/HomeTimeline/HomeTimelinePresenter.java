package com.arenas.droidfan.main.hometimeline;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.arenas.droidfan.api.Api;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.service.FanFouService;

import java.util.List;

/**
 * Created by Arenas on 2016/7/9.
 */
public class HomeTimelinePresenter implements HomeTimelineContract.Presenter , DataSource.LoadStatusCallback{

    private static final String TAG = HomeTimelinePresenter.class.getSimpleName();

    protected  HomeTimelineContract.View mView;
    protected  FanFouDB mFanFouDB;
    protected  Api mApi;

    public HomeTimelinePresenter(){

    }

    public HomeTimelinePresenter(FanFouDB fanFouDB , HomeTimelineContract.View view){
        mView = view;
        mFanFouDB = fanFouDB;
        mApi = AppContext.getApi();
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        loadStatus();
    }

    @Override
    public void loadStatus() {
        mFanFouDB.getHomeTLStatusList(this);
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
        p.sinceId = mFanFouDB.getHomeTLSinceId();
        mView.startService(p);
    }

    @Override
    public void newStatus() {
        mView.showUpdateStatusUi();
    }

    public void onDataNotAvailable() {
        refresh();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean hasNewData = intent.getBooleanExtra(FanFouService.EXTRA_HAS_NEW , false);
        if (hasNewData){
            loadStatus();
        }else {
            mView.showError("hometimeline something wrong!");
        }
        mView.hideRefreshBar();
    }
}
