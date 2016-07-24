package com.arenas.droidfan.users;

import android.content.Context;
import android.content.Intent;

import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.UserModel;
import com.arenas.droidfan.service.FanFouService;

import java.util.List;

/**
 * Created by Arenas on 2016/7/23.
 */
public class UserPresenter implements UserContract.Presenter , DataSource.LoadUserCallback{

    private String mUserId;
    private UserContract.View mView;
    private Context mContext;
    private int mType;
    private FanFouDB mFanFouDB;
    private List<String> mIds;

    public UserPresenter(Context context , String userId , UserContract.View view , int usersType){
        mContext = context;
        mUserId = userId;
        mView = view;
        mType = usersType;

        mFanFouDB = FanFouDB.getInstance(context);
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        mView.showProgressbar();
        fetchUsers();
    }

    private void fetchUsers(){
        switch (mType){
            case UserListActivity.TYPE_FOLLOWERS:
                FanFouService.getFollowers(mContext , mUserId);
                break;
            case UserListActivity.TYPE_FOLLOWING:
                FanFouService.getFollowing(mContext , mUserId);
                break;
        }
    }

    private void fetchUserIds(){
        switch (mType){
            case UserListActivity.TYPE_FOLLOWERS:
                FanFouService.getFollowersIds(mContext , mUserId , new Paging());
                break;
            case UserListActivity.TYPE_FOLLOWING:
                FanFouService.getFollowingIds(mContext , mUserId , new Paging());
                break;
        }
    }

    private void loadUsers(){
        mFanFouDB.getUsers(mIds , this);
    }

    @Override
    public void onUsersLoaded(List<UserModel> userModelList) {
        mView.showUsers(userModelList);
    }

    @Override
    public void onDataNotAvailable() {
        mView.showError("oh on !!! no user to show !");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
         mIds = intent.getStringArrayListExtra(FanFouService.EXTRA_IDS);
        if (mIds.isEmpty()){
            fetchUserIds();
        }else {
            loadUsers();
        }
    }
}
