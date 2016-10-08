package com.arenas.droidfan.main.message;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.NetworkUtils;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.DirectMessageModel;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.service.FanFouService;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Arenas on 2016/7/26.
 */
public class MessagePresenter implements MessageContract.Presenter , DataSource.LoadDMCallback{

    private String TAG = MessagePresenter.class.getSimpleName();

    private FanFouDB mFanFouDB;
    private MessageContract.View mView;
    private Context mContext;

    private List<DirectMessageModel> mDMList;

    private boolean mIsFirstFetch;

    private Paging paging;

    public MessagePresenter(Context context, MessageContract.View mView ) {
        this.mFanFouDB = FanFouDB.getInstance(context);
        mContext = context;
        this.mView = mView;
        mIsFirstFetch = true;

        paging = new Paging();
        paging.count = 60;

        mView.setPresenter(this);
    }

    @Override
    public void start() {
        loadConversationList();
    }

    private void loadConversationList(){
        mFanFouDB.getConversationList(this);
    }

    @Override
    public void onDMLoaded(List<DirectMessageModel> messageModels) {
        mView.hideProgressbar();
        mDMList = messageModels;
        mView.showList(messageModels);
    }

    @Override
    public void onDataNotAvailable() {
        if (mIsFirstFetch){
            mView.showProgressbar();
            fetchData();
        }
        mView.hideProgressbar();
    }

    private void fetchData(){
        if (!NetworkUtils.isNetworkConnected(mContext)){
            Utils.showToast(mContext , mContext.getString(R.string.network_is_disconnected));
            mView.hideProgressbar();
            return;
        }
        rx.Observable.create(new rx.Observable.OnSubscribe<List<DirectMessageModel>>() {
            @Override
            public void call(Subscriber<? super List<DirectMessageModel>> subscriber) {
                try{
                    List<DirectMessageModel> model = AppContext.getApi().getConversationList(paging);
                    subscriber.onNext(model);
                    subscriber.onCompleted();
                }catch (ApiException e){
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new rx.Observer<List<DirectMessageModel>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Utils.showToast(mContext , mContext.getString(R.string.failed_refresh));
                mView.hideProgressbar();
            }

            @Override
            public void onNext(List<DirectMessageModel> models) {
                mView.hideProgressbar();
                mFanFouDB.saveConversationList(models);
                loadConversationList();
            }
        });
    }

    @Override
    public void refresh() {
        fetchData();
        mView.goToTop();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mIsFirstFetch = false;
        loadConversationList();
    }

    @Override
    public void getMore() {

    }
}
