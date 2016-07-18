package com.arenas.droidfan.main.Notice;

import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.main.StatusContract;
import com.arenas.droidfan.main.StatusPresenter;

/**
 * Created by Arenas on 2016/7/18.
 */
public class NoticePresenter extends StatusPresenter {

    private static final String TAG = NoticePresenter.class.getSimpleName();

    public NoticePresenter(FanFouDB mFanFouDB , StatusContract.View mView ) {
        this.mView = mView;
        this.mFanFouDB = mFanFouDB;
    }

    @Override
    public void loadStatus() {

    }
}
