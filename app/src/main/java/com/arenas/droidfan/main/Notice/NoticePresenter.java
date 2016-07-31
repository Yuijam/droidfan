package com.arenas.droidfan.main.notice;

import android.content.Context;

import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.main.hometimeline.HomeTimelineContract;
import com.arenas.droidfan.main.hometimeline.HomeTimelinePresenter;
import com.arenas.droidfan.service.FanFouService;

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
        FanFouService.getMentions(mContext , p);
    }

    @Override
    protected void initMaxId() {
        p = new Paging();
        p.count = 10;
        p.maxId = mFanFouDB.getNoticeMaxId();
    }
}
