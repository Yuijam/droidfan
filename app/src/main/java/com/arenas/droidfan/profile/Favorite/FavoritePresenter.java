package com.arenas.droidfan.profile.favorite;

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
 * Created by Arenas on 2016/7/21.
 */
public class FavoritePresenter extends ProfileStatusPresenter {

    private static final String TAG = FavoritePresenter.class.getSimpleName();

    private int page;
    private boolean isRefresh;

    public FavoritePresenter(){
    }

    public FavoritePresenter(Context context, HomeTimelineContract.View view , String userId){
        mView = view;
        mFanFouDB = FanFouDB.getInstance(context);
        mContext = context;
        mApi = AppContext.getApi();
        mUserId = userId;

        page = 1;
        p = new Paging();
        mView.setPresenter(this);
    }

    @Override
    protected void initSinceId() {//这个api不能用sinceId
        isRefresh = true;
    }

    @Override
    protected void startService() {
        rx.Observable.create(new rx.Observable.OnSubscribe<List<StatusModel>>() {
            @Override
            public void call(Subscriber<? super List<StatusModel>> subscriber) {
                if (!NetworkUtils.isNetworkConnected(mContext)){
                    Utils.showToast(mContext , mContext.getString(R.string.network_is_disconnected));
                    return;
                }
                try{
                    Log.d(TAG , "observable thread = " + Thread.currentThread().getId());
                    Log.d(TAG , "p.maxid = " + p.maxId);
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
                Log.d(TAG , "favorite model.size = " + models.size());
                if(models.size() > 0){
                    if (isRefresh){
                        mFanFouDB.deleteFavorites(mUserId);
                    }
                    mFanFouDB.saveFavoritesList(models);
                    loadStatus();
                }else {
                    mView.hideProgressBar();
                }
            }
        });
    }

    @Override
    public void loadStatus() {
        mFanFouDB.getFavoritesList(mUserId , this);
    }

    @Override
    protected void initMaxId() {
        page++;
        p.page = page;
    }
}
