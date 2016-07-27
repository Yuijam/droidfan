package com.arenas.droidfan.profile.photoalbum;

import android.content.Context;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.main.hometimeline.HomeTimelineContract;
import com.arenas.droidfan.profile.profilestatus.ProfileStatusPresenter;
import com.arenas.droidfan.service.FanFouService;

/**
 * Created by Arenas on 2016/7/27.
 */
public class PhotoPresenter extends ProfileStatusPresenter {

    public PhotoPresenter(Context context , HomeTimelineContract.View view , String userId){
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
        p.sinceId = mFanFouDB.getPhotoSinceId(mUserId);
        p.count = 20;
    }

    @Override
    protected void startService() {
        FanFouService.getPhotoTimeline(mContext , mUserId , p);
    }

    @Override
    public void loadStatus() {
        mFanFouDB.loadPhotoTimeline(mUserId , this);
    }
}
