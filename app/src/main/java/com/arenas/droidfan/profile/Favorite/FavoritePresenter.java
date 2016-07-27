package com.arenas.droidfan.profile.favorite;

import android.content.Context;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.UserModel;
import com.arenas.droidfan.main.hometimeline.HomeTimelineContract;
import com.arenas.droidfan.main.hometimeline.HomeTimelinePresenter;
import com.arenas.droidfan.profile.ProfilePresenter;
import com.arenas.droidfan.profile.profilestatus.ProfileStatusPresenter;
import com.arenas.droidfan.service.FanFouService;

/**
 * Created by Arenas on 2016/7/21.
 */
public class FavoritePresenter extends ProfileStatusPresenter {

    private static final String TAG = FavoritePresenter.class.getSimpleName();

    public FavoritePresenter(){

    }

    public FavoritePresenter(Context context, HomeTimelineContract.View view , String userId){
        mView = view;
        mFanFouDB = FanFouDB.getInstance(context);
        mContext = context;
        mApi = AppContext.getApi();
        mUserId = userId;

        mView.setPresenter(this);
    }

    @Override
    protected void initPaging() {
        p = new Paging();
        p.sinceId = mFanFouDB.getFavoritesSinceId(mUserId);
        p.count = 20;
    }

    @Override
    protected void startService() {
        FanFouService.getFavoritesList(mContext , mUserId , p);
    }

    @Override
    protected void getStatusList() {
        mFanFouDB.getFavoritesList(mUserId , this);
    }
}
