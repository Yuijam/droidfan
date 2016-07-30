package com.arenas.droidfan.users;

import android.content.Context;
import android.content.Intent;

import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.UserModel;
import com.arenas.droidfan.service.FanFouService;

import java.util.List;

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
    private boolean mFirstEnter;

    public UserPresenter(Context context , String userId , UserContract.View view , int usersType){
        mContext = context;
        mUserId = userId;
        mView = view;
        mType = usersType;
        mFirstEnter = true;

        mFanFouDB = FanFouDB.getInstance(context);
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        mView.showProgressbar();
        loadUsers();
    }

    private void fetchUsers(){
        mView.showProgressbar();
        switch (mType){
            case UserListActivity.TYPE_FOLLOWERS:
                FanFouService.getFollowers(mContext , mUserId);
                break;
            case UserListActivity.TYPE_FOLLOWING:
                FanFouService.getFollowing(mContext , mUserId);
                break;
        }
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
        if (mFirstEnter){
            fetchUsers();
        }else {
            mView.hideProgressbar();
            mView.showError("no users to show");
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mFirstEnter = false;
        loadUsers();
    }

    @Override
    public void refresh() {
        fetchUsers();
    }
}
