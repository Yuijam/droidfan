package com.arenas.droidfan.main.message.chat;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.NetworkUtils;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.api.Api;
import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.DirectMessageModel;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.data.model.UserModel;
import com.arenas.droidfan.service.FanFouService;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Arenas on 2016/7/26.
 */
public class ChatPresenter implements ChatContract.Presenter , DataSource.LoadDMCallback{

    private static final String TAG = ChatPresenter.class.getSimpleName();

    private FanFouDB mFanFouDB;
    private ChatContract.View mView;
    private Context mContext;

    private String mUserId;
    private DirectMessageModel model;
    private String mUsername;
    private Api api;

    private String text;
    private Paging paging;

    public ChatPresenter(String userId , String username , ChatContract.View mView, Context mContext) {
        this.mFanFouDB = FanFouDB.getInstance(mContext);
        this.mView = mView;
        this.mContext = mContext;
        mUserId = userId;
        mUsername = username;

        paging = new Paging();
        api = AppContext.getApi();
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        mView.showProgressbar();
        loadDM();
        testFollower();
    }

    private void testFollower(){
        rx.Observable.create(new rx.Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                if (!NetworkUtils.isNetworkConnected(mContext)){
                    Utils.showToast(mContext , mContext.getString(R.string.network_is_disconnected));
                    return;
                }
                try{
                    Log.d(TAG , "testFollower thread = " + Thread.currentThread().getId());
                    boolean isAFollower = api.isFriends(mUserId , AppContext.getAccount());
                    subscriber.onNext(isAFollower);
                    subscriber.onCompleted();
                }catch (ApiException e){
                    subscriber.onError(e);
                }

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new rx.Observer<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Boolean isAFollower) {
                Log.d(TAG , "observer thread = " + Thread.currentThread().getId());
                Log.d(TAG , "isAFollower = " + isAFollower);
                if (isAFollower){
                    mView.showEditMessageLayout();
                }else {
                    mView.showEditInvalidNotice();
                }
            }
        });
    }

    private void loadDM(){
        mFanFouDB.getConversation(mUsername , this);
    }

    @Override
    public void onDMLoaded(List<DirectMessageModel> messageModels) {
        Log.d(TAG , "onDMLoaded");
        model = messageModels.get(0);
        mView.hideProgressbar();
        mView.showChatItems(messageModels);
        mView.scrollTo(messageModels.size());
    }

    @Override
    public void onDataNotAvailable() {
        Log.d(TAG , "onDataNotAvailable");
        getMore();
    }

    private void fetchData(){
        rx.Observable.create(new rx.Observable.OnSubscribe<List<DirectMessageModel>>() {
            @Override
            public void call(Subscriber<? super List<DirectMessageModel>> subscriber) {
                if (!NetworkUtils.isNetworkConnected(mContext)){
                    Utils.showToast(mContext , mContext.getString(R.string.network_is_disconnected));
                    return;
                }
                try{
                    Log.d(TAG , "fetch data observable thread = " + Thread.currentThread().getId());
                    List<DirectMessageModel> directMessageModels = api.getConversation(mUserId , paging);
                    subscriber.onNext(directMessageModels);
                    subscriber.onCompleted();
                }catch (ApiException e){
                    subscriber.onError(e);
                }

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new rx.Observer<List<DirectMessageModel>>() {
            @Override
            public void onCompleted() {
                Log.d(TAG , "onCompleted~~");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<DirectMessageModel> dmms) {
                Log.d(TAG , "observer thread = " + Thread.currentThread().getId());
                mView.refreshComplete();
                mView.loadMoreComplete();
                if (dmms.size() > 0){
                    mFanFouDB.saveDirectMessages(dmms);
                    loadDM();
                }else {
                    mView.hideProgressbar();
                }
            }
        });
    }

    @Override
    public void refresh() {
        paging.maxId = mFanFouDB.getDMMaxId(mUsername);
        paging.sinceId = null;
        fetchData();
    }

    @Override
    public void getMore() {
        paging.count = 60;
        paging.sinceId = mFanFouDB.getDMSinceId(mUsername);
        paging.maxId = null;
        fetchData();
    }

    @Override
    public void send(String text) {
        if (TextUtils.isEmpty(text)){
            mView.showError(mContext.getString(R.string.text_should_not_empty));
            return;
        }
        if (text.length() > 140){
            mView.showError(mContext.getString(R.string.text_too_more));
            return;
        }
        this.text = text;
        sendDM();
        mView.emptyInput();
    }

    private void sendDM(){
        rx.Observable.create(new rx.Observable.OnSubscribe<DirectMessageModel>() {
            @Override
            public void call(Subscriber<? super DirectMessageModel> subscriber) {
                if (!NetworkUtils.isNetworkConnected(mContext)){
                    Utils.showToast(mContext , mContext.getString(R.string.network_is_disconnected));
                    return;
                }
                try{
                    Log.d(TAG , "fetch data observable thread = " + Thread.currentThread().getId());
                    DirectMessageModel model = api.createDirectmessage(mUserId , text , null);
                    subscriber.onNext(model);
                    subscriber.onCompleted();
                }catch (ApiException e){
                    subscriber.onError(e);
                }

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new rx.Observer<DirectMessageModel>() {
            @Override
            public void onCompleted() {
                Log.d(TAG , "onCompleted~~");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(DirectMessageModel model) {
                Log.d(TAG , "observer thread = " + Thread.currentThread().getId());
                mView.refreshComplete();
                mView.loadMoreComplete();
                if (model != null){
                    model.setConversationId(mUsername);
                    mFanFouDB.saveDirectMessage(model);
                    loadDM();
                }else {
                    Utils.showToast(mContext , "发送失败");
                    mView.hideProgressbar();
                }
            }
        });
    }
}
