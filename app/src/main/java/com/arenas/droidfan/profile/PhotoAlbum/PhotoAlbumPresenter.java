package com.arenas.droidfan.profile.photoalbum;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.main.TabFragmentAdapter;
import com.arenas.droidfan.main.hometimeline.HomeTimelineContract;
import com.arenas.droidfan.profile.profilestatus.ProfileStatusPresenter;
import com.arenas.droidfan.service.FanFouService;

/**
 * Created by Arenas on 2016/7/27.
 */
public class PhotoAlbumPresenter extends ProfileStatusPresenter {

    private static final String TAG = PhotoAlbumPresenter.class.getSimpleName();

    public PhotoAlbumPresenter(Context context , HomeTimelineContract.View view , String userId){
        mView = view;
        mFanFouDB = FanFouDB.getInstance(context);
        mContext = context;
        mApi = AppContext.getApi();
        mUserId = userId;

        mView.setPresenter(this);
    }

    @Override
    protected void initSinceId() {
        p = new Paging();
        p.sinceId = mFanFouDB.getPhotoSinceId(mUserId);
//        p.count = 20;
    }

    @Override
    protected void startService() {
        FanFouService.getPhotoTimeline(mContext , mUserId , p);
    }

    @Override
    public void loadStatus() {
        mFanFouDB.loadPhotoTimeline(mUserId , this);
    }

    @Override
    protected void initMaxId() {
//        p = new Paging();
//        p.maxId = mFanFouDB.getPhotoMaxId(mUserId);
//        p.count = 20;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        loadStatus();
//        super.onReceive(context, intent);
        Log.d(TAG , "onReceive");
    }
}
