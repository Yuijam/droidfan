package com.arenas.droidfan.main.notice;

import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.main.hometimeline.HomeTimelineContract;
import com.arenas.droidfan.main.hometimeline.HomeTimelinePresenter;

/**
 * Created by Arenas on 2016/7/18.
 */
public class NoticePresenter extends HomeTimelinePresenter {

    private static final String TAG = NoticePresenter.class.getSimpleName();

    public NoticePresenter(FanFouDB fanFouDB , HomeTimelineContract.View view){
        mView = view;
        mFanFouDB = fanFouDB;
        mApi = AppContext.getApi();
        mView.setPresenter(this);
    }

    @Override
    public void loadStatus() {
        mFanFouDB.getNoticeStatusList(this);
    }

    @Override
    public void refresh() {
        mView.showRefreshBar();
        Paging p = new Paging();
        p.sinceId = mFanFouDB.getNoticeSinceId();
        mView.startService(p);
    }
}
