package com.arenas.droidfan.users;

import android.content.Context;
import android.content.Intent;
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
import com.arenas.droidfan.data.model.UserModel;
import com.arenas.droidfan.service.FanFouService;

import java.util.List;
import java.util.concurrent.Exchanger;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Arenas on 2016/7/23.
 */
public class UserPresenter implements UserContract.Presenter , DataSource.LoadUserCallback{

    private static final String TAG = UserPresenter.class.getSimpleName();

    private String mUserId;
    private UserContract.View mView;
    private Context mContext;
    private int mType;
    private FanFouDB mFanFouDB;
    private boolean startComplete;
    private Api api;
    private Paging paging;
    private int page;
    private boolean isRefreshing;

    public UserPresenter(Context context , String userId , UserContract.View view , int usersType){
        mContext = context;
        mUserId = userId;
        mView = view;
        mType = usersType;
        api = AppContext.getApi();

        mFanFouDB = FanFouDB.getInstance(context);
        paging = new Paging();
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        if (!startComplete){
            mView.showProgressbar();
            loadUsers();
            fetchUsers();
            startComplete = true;
        }
    }

    private void fetchUsers(){
        rx.Observable.create(new rx.Observable.OnSubscribe<List<UserModel>>() {
            @Override
            public void call(Subscriber<? super List<UserModel>> subscriber) {
                if (!NetworkUtils.isNetworkConnected(mContext)){
                    Utils.showToast(mContext , mContext.getString(R.string.network_is_disconnected));
                    return;
                }
                try{
                    List<UserModel> users;
                    if (mType == UserListActivity.TYPE_FOLLOWERS){
                        users = api.getFollowers(mUserId , paging);
                    }else {
                        users = api.getFriends(mUserId , paging);
                    }
                    subscriber.onNext(users);
                    subscriber.onCompleted();
                }catch (Exception e){
                    subscriber.onError(e);
                }

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new rx.Observer<List<UserModel>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<UserModel> users) {
                mView.hideProgressbar();
                if (users.size() > 0){
                    if (mType == UserListActivity.TYPE_FOLLOWERS){
                        if (isRefreshing){
                            mFanFouDB.deleteFollowers(mUserId);
                        }
                        mFanFouDB.saveFollowers(users , mUserId);
                    }else {
                        if (isRefreshing){
                            mFanFouDB.deleteFollowing(mUserId);
                        }
                        mFanFouDB.saveFollowing(users , mUserId);
                    }
                    isRefreshing = false;
                    loadUsers();
                }else {
                    // TODO: 2016/11/9 emptyView
                }
            }
        });
    }

    private void loadUsers(){
        switch (mType){
            case UserListActivity.TYPE_FOLLOWERS:
                mFanFouDB.getFollowers(mUserId , this);
                break;
            case UserListActivity.TYPE_FOLLOWING:
                mFanFouDB.getFollowing(mUserId , this);
                break;
        }
    }

    @Override
    public void onUsersLoaded(List<UserModel> userModelList) {
        mView.hideProgressbar();
        mView.showUsers(userModelList);
    }

    @Override
    public void onUsersNotAvailable() {
        fetchUsers();
    }

    @Override
    public void refresh() {
        isRefreshing = true;
        page = 1;
        paging.page = page;
        fetchUsers();
    }

    @Override
    public void getMore() {
        page++;
        paging.page = page;
        fetchUsers();
    }
}
