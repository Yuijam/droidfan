package com.arenas.droidfan.main.hometimeline;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.api.Api;
import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.photo.PhotoContract;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    public void onDataNotAvailable() {
        if (mIsAllowRefresh){
            refresh();
        }
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
        p.count = 30;
    }

    protected void startService(){
        Log.d(TAG , "startService~~");
        rx.Observable.create(new rx.Observable.OnSubscribe<List<StatusModel>>() {
            @Override
            public void call(Subscriber<? super List<StatusModel>> subscriber) {
                try{
                    Log.d(TAG , "observable thread = " + Thread.currentThread().getId());
                    List<StatusModel> model = AppContext.getApi().getHomeTimeline(p);
                    subscriber.onNext(model);
                    subscriber.onCompleted();
                }catch (ApiException e){
                    subscriber.onError(e);
                }

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new rx.Observer<List<StatusModel>>() {
            @Override
            public void onCompleted() {
                Log.d(TAG , "onCompleted~~");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<StatusModel> models) {
                Log.d(TAG , "observer thread = " + Thread.currentThread().getId());
                mView.hideRefreshBar();
                if(models.size() > 0){
                    mFanFouDB.saveHomeTLStatusList(models);
                    loadStatus();
                }
            }
        });
    }

    @Override
    public void getMore() {
        Log.d(TAG , "getMore");
        initMaxId();
        startService();
    }

    protected void initMaxId(){
        p = new Paging();
        p.count = 10;
        p.maxId = mFanFouDB.getHomeMaxId();
    }
}
