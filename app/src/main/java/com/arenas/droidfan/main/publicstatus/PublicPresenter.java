package com.arenas.droidfan.main.publicstatus;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.main.hometimeline.HomeTimelineContract;
import com.arenas.droidfan.main.hometimeline.HomeTimelinePresenter;
import com.arenas.droidfan.service.FanFouService;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Arenas on 2016/7/18.
 */
public class PublicPresenter extends HomeTimelinePresenter {

    private final static String TAG = PublicPresenter.class.getSimpleName();


    public PublicPresenter(Context context , HomeTimelineContract.View view){
        mView = view;
        mFanFouDB = FanFouDB.getInstance(context);
        mContext = context;
        mApi = AppContext.getApi();
        mView.setPresenter(this);
    }

    @Override
    public void loadStatus() {
        mFanFouDB.getPublicStatusList(this);
    }

    @Override
    protected void initSinceId() {

    }

    @Override
    protected void startService() {
        rx.Observable.create(new rx.Observable.OnSubscribe<List<StatusModel>>() {
            @Override
            public void call(Subscriber<? super List<StatusModel>> subscriber) {
                try{
                    Log.d(TAG , "observable thread = " + Thread.currentThread().getId());
                    List<StatusModel> model = AppContext.getApi().getPublicTimeline();
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
                    mFanFouDB.savePublicStatusList(models);
                    loadStatus();
                }
            }
        });
//        FanFouService.getPublicTimeline(mContext);
    }

    @Override
    public void getMore() {
        // TODO: 2016/8/10
    }
}
