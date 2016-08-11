package com.arenas.droidfan.profile.profilestatus;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.data.model.UserModel;
import com.arenas.droidfan.main.hometimeline.HomeTimelineContract;
import com.arenas.droidfan.main.hometimeline.HomeTimelinePresenter;
import com.arenas.droidfan.service.FanFouService;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Arenas on 2016/7/20.
 */
public class ProfileStatusPresenter extends HomeTimelinePresenter {

    private static final String TAG = ProfileStatusPresenter.class.getSimpleName();

    protected String mUserId;
    protected UserModel mUser;

    public ProfileStatusPresenter() {
    }

    public ProfileStatusPresenter(Context context , HomeTimelineContract.View view , String userId){
        mView = view;
        mFanFouDB = FanFouDB.getInstance(context);
        mApi = AppContext.getApi();
        mContext = context;

        mUserId = userId;
        mView.setPresenter(this);
    }

    @Override
    public void loadStatus() {
        mFanFouDB.getProfileStatusList(mUserId , this);
    }

    @Override
    protected void initSinceId() {
        p = new Paging();
        p.count = 20;
        p.sinceId = mFanFouDB.getProfileSinceId(mUserId);
    }

    @Override
    protected void startService() {
        rx.Observable.create(new rx.Observable.OnSubscribe<List<StatusModel>>() {
            @Override
            public void call(Subscriber<? super List<StatusModel>> subscriber) {
                try{
                    Log.d(TAG , "observable thread = " + Thread.currentThread().getId());
                    List<StatusModel> model = AppContext.getApi().getUserTimeline(mUserId , p);
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
                    mFanFouDB.saveProfileStatusList(models);
                    loadStatus();
                }
            }
        });
    }

    @Override
    protected void initMaxId() {
        p = new Paging();
        p.count = 20;
        p.maxId = mFanFouDB.getProfileMaxId(mUserId);
        Log.d(TAG , "maxId = " + p.maxId);
    }
}
