package com.arenas.droidfan.main.hometimeline;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.arenas.droidfan.R;
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
    protected  Context mContext;
    protected boolean mIsAllowRefresh;
    protected Paging p;

    public HomeTimelinePresenter(){

    }

    public HomeTimelinePresenter(Context context , HomeTimelineContract.View view){
        mView = view;
        mFanFouDB = FanFouDB.getInstance(context);
        mApi = AppContext.getApi();
        mContext = context;

        mView.setPresenter(this);
    }

    @Override
    public void start() {
        mIsAllowRefresh = true;
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
        mIsAllowRefresh = true;
        mView.showRefreshBar();
        initSinceId();
        startService();
    }

    protected void initSinceId(){
        p = new Paging();
        p.sinceId = mFanFouDB.getHomeTLSinceId();
        p.count = 10;
    }

    protected void startService(){
        FanFouService.getHomeTimeline(mContext , p);
    }

    @Override
    public void newStatus() {
        mView.showUpdateStatusUi();
    }

    public void onDataNotAvailable() {
        if (mIsAllowRefresh){
            refresh();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        boolean hasNewData = intent.getBooleanExtra(FanFouService.EXTRA_HAS_NEW , false);
//        if (hasNewData){
        mIsAllowRefresh = false;
        mView.hideRefreshBar();
            loadStatus();
//        }else {
//            Log.d(TAG , "hasNewData is false--------");
//            mView.showError(context.getString(R.string.no_new_status));
//        }

    }

    @Override
    public void getMore(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
        initMaxId();
        startService();
    }

    protected void initMaxId(){
        p = new Paging();
        p.count = 10;
        p.maxId = mFanFouDB.getHomeMaxId();
    }
}
