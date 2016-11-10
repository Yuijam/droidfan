package com.arenas.droidfan.profile.photoalbum;

import android.content.Context;
import android.util.Log;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.NetworkUtils;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.main.hometimeline.HomeTimelineContract;
import com.arenas.droidfan.profile.profilestatus.ProfileStatusPresenter;

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
//        p.sinceId = mFanFouDB.getPhotoSinceId(mUserId);
        p.count = 20;
    }

    @Override
    protected void startService() {
        if (!NetworkUtils.isNetworkConnected(mContext)){
            Utils.showToast(mContext , mContext.getString(R.string.network_is_disconnected));
            return;
        }
        rx.Observable.create(new rx.Observable.OnSubscribe<List<StatusModel>>() {
            @Override
            public void call(Subscriber<? super List<StatusModel>> subscriber) {
                try{
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
            }

            @Override
            public void onError(Throwable e) {
                mView.hideProgressBar();
            }

            @Override
            public void onNext(List<StatusModel> models) {
                mView.hideProgressBar();
                if(models.size() > 0){
//                    mFanFouDB.savePhotoTimeline(models);
//                    loadStatus();
                    mView.showStatus(models);
                    mMaxId = models.get(models.size()-1).getId();
                }else {
//                    mView.hideProgressBar();
                    //empty view
                    // TODO: 2016/11/9
                }
            }
        });
    }

    @Override
    public void loadStatus() {
//        mFanFouDB.loadPhotoTimeline(mUserId , this);
    }

    @Override
    protected void initMaxId() {
        p = new Paging();
//        p.maxId = mFanFouDB.getPhotoMaxId(mUserId);
        p.maxId = mMaxId;
        p.count = 20;
    }
}
