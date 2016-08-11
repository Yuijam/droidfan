package com.arenas.droidfan.profile.photoalbum;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.main.TabFragmentAdapter;
import com.arenas.droidfan.main.hometimeline.HomeTimelineContract;
import com.arenas.droidfan.profile.profilestatus.ProfileStatusPresenter;
import com.arenas.droidfan.service.FanFouService;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Arenas on 2016/7/27.
 */
public class PhotoAlbumPresenter extends ProfileStatusPresenter {

    private static final String TAG = PhotoAlbumPresenter.class.getSimpleName();

    public PhotoAlbumPresenter(Context context , HomeTimelineContract.View view , String userId){
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
        p.sinceId = mFanFouDB.getPhotoSinceId(mUserId);
        p.count = 20;
    }

    @Override
    protected void startService() {
        rx.Observable.create(new rx.Observable.OnSubscribe<List<StatusModel>>() {
            @Override
            public void call(Subscriber<? super List<StatusModel>> subscriber) {
                try{
                    Log.d(TAG , "observable thread = " + Thread.currentThread().getId());
                    List<StatusModel> model = AppContext.getApi().getPhotosTimeline(mUserId , p);
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
                    mFanFouDB.savePhotoTimeline(models);
                    loadStatus();
                }
            }
        });
    }

    @Override
    public void loadStatus() {
        mFanFouDB.loadPhotoTimeline(mUserId , this);
    }

    @Override
    protected void initMaxId() {
        p = new Paging();
        p.maxId = mFanFouDB.getPhotoMaxId(mUserId);
        p.count = 20;
    }
}
