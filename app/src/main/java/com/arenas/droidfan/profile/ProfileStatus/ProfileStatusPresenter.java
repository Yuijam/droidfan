package com.arenas.droidfan.profile.profilestatus;

import android.content.Context;
import android.util.Log;

import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.NetworkUtils;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.main.hometimeline.HomeTimelineContract;
import com.arenas.droidfan.main.hometimeline.HomeTimelinePresenter;
import com.arenas.droidfan.profile.ProfileEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
    public void start() {
        if (!startComplete){
            Log.d(TAG , "start ~");
            mView.showProgressBar();
            EventBus.getDefault().register(this);
            startComplete = true;
        }
    }

    @Subscribe(sticky = true)
    public void onLoadDataEvent(ProfileEvent event){
        Log.d(TAG , "even.isLoad = " + event.isLoad() + "event.isRefresh" + event.isRefresh());
        if (event.isLoad()){
            loadStatus();
        }else if (!event.isRefresh()){
            mView.hideProgressBar();
        }

        if (event.isRefresh()){
            refresh();
        }
    }

    @Override
    public void refresh() {
        Log.d(TAG , "refresh");
        initSinceId();
        startService();
    }

    @Override
    public void loadStatus() {
        mFanFouDB.getProfileStatusList(mUserId , this);
    }

    @Override
    public void onStatusLoaded(List<StatusModel> status) {
        mView.hideProgressBar();
        mView.showStatus(status);
    }

    @Override
    protected void initSinceId() {
        p = new Paging();
        p.count = 20;
        p.sinceId = mFanFouDB.getProfileSinceId(mUserId);
    }

    @Override
    protected void startService() {
        Log.d(TAG , "startService~");
        rx.Observable.create(new rx.Observable.OnSubscribe<List<StatusModel>>() {
            @Override
            public void call(Subscriber<? super List<StatusModel>> subscriber) {
                if (!NetworkUtils.isNetworkConnected(mContext)){
                    Utils.showToast(mContext , mContext.getString(R.string.network_is_disconnected));
                    return;
                }
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
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<StatusModel> models) {
                Log.d(TAG , "observer thread = " + Thread.currentThread().getId());
                if(models.size() > 0){
                    mFanFouDB.saveProfileStatusList(models);
                    loadStatus();
                }else {
                    mView.hideProgressBar();
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
