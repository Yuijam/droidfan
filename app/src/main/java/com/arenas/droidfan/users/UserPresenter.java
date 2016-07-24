package com.arenas.droidfan.users;

import android.content.Context;
import android.content.res.TypedArray;

import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.service.FanFouService;

/**
 * Created by Arenas on 2016/7/23.
 */
public class UserPresenter implements UserContract.Presenter {

    private String mUserId;
    private UserContract.View mView;
    private Context mContext;
    private int mType;
    private FanFouDB mFanFouDB;

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
}
