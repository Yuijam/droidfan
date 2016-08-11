package com.arenas.droidfan.main.notice;

import android.content.Context;
import android.util.Log;

import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.main.TabFragmentAdapter;
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
public class NoticePresenter extends HomeTimelinePresenter {

    private static final String TAG = NoticePresenter.class.getSimpleName();

    public NoticePresenter(Context context , HomeTimelineContract.View view){
        mView = view;
        mFanFouDB = FanFouDB.getInstance(context);
        mContext = context;

        mApi = AppContext.getApi();
        mView.setPresenter(this);
    }

    @Override
    public void loadStatus() {
        mFanFouDB.getNoticeStatusList(this);
    }

    @Override
    protected void initSinceId() {
        p = new Paging();
        p.count = 10;
        p.sinceId = mFanFouDB.getNoticeSinceId();
    }

    @Override
    protected void startService() {
//        FanFouService.getMentions(mContext , p);
        Log.d(TAG , "startService~~");

        rx.Observable.create(new rx.Observable.OnSubscribe<List<StatusModel>>() {
            @Override
            public void call(Subscriber<? super List<StatusModel>> subscriber) {
                try{
                    Log.d(TAG , "observable thread = " + Thread.currentThread().getId());
                    List<StatusModel> model = AppContext.getApi().getMentions(p);
                    Log.d(TAG , "p.sinceId = " + p.sinceId + " , p.maxId = " + p.maxId);
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
                mView.hideRefreshBar();
                if (models.size() > 0 ){
                    mFanFouDB.saveNoticeStatusList(models);
                    loadStatus();
                }
            }
        });
    }

    @Override
    protected void initMaxId() {
        p = new Paging();
        p.count = 10;
        p.maxId = mFanFouDB.getNoticeMaxId();
    }
}
