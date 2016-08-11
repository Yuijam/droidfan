package com.arenas.droidfan.profile.favorite;

import android.content.Context;
import android.util.Log;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.data.model.UserModel;
import com.arenas.droidfan.main.hometimeline.HomeTimelineContract;
import com.arenas.droidfan.main.hometimeline.HomeTimelinePresenter;
import com.arenas.droidfan.profile.ProfilePresenter;
import com.arenas.droidfan.profile.profilestatus.ProfileStatusPresenter;
import com.arenas.droidfan.service.FanFouService;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Arenas on 2016/7/21.
 */
public class FavoritePresenter extends ProfileStatusPresenter {

    private static final String TAG = FavoritePresenter.class.getSimpleName();

    public FavoritePresenter(){

    }

    public FavoritePresenter(Context context, HomeTimelineContract.View view , String userId){
        mView = view;
        mFanFouDB = FanFouDB.getInstance(context);
        mContext = context;
        mApi = AppContext.getApi();
        mUserId = userId;

        mView.setPresenter(this);
    }

    @Override
    protected void initSinceId() {
        p = new Paging();
        p.sinceId = mFanFouDB.getFavoritesSinceId(mUserId);
        p.count = 20;
    }

    @Override
    protected void startService() {
        rx.Observable.create(new rx.Observable.OnSubscribe<List<StatusModel>>() {
            @Override
            public void call(Subscriber<? super List<StatusModel>> subscriber) {
                try{
                    Log.d(TAG , "observable thread = " + Thread.currentThread().getId());
                    List<StatusModel> model = AppContext.getApi().getFavorites(mUserId , p);
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
                    mFanFouDB.saveFavoritesList(models);
                    loadStatus();
                }
            }
        });
//        FanFouService.getFavoritesList(mContext , mUserId , p);
    }

    @Override
    public void loadStatus() {
        mFanFouDB.getFavoritesList(mUserId , this);
    }

    @Override
    protected void initMaxId() {
        p = new Paging();
        p.maxId = mFanFouDB.getFavoritesMaxid(mUserId);
        p.count = 20;
    }
}
