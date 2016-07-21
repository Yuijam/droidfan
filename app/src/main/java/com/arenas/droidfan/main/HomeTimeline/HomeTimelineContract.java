package com.arenas.droidfan.main.HomeTimeline;

import android.content.Context;
import android.content.Intent;

import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.BasePresenter;
import com.arenas.droidfan.BaseView;
import com.arenas.droidfan.data.model.StatusModel;

import java.util.List;

/**
 * Created by Arenas on 2016/7/9.
 */
public interface HomeTimelineContract {

    interface View extends BaseView{
        void showStatus(List<StatusModel> status);
        void startService(Paging p);
        void showError();
        void showUpdateStatusUi();
        void hideRefreshBar();
        void showRefreshBar();
    }

    interface Presenter extends BasePresenter{
        void loadStatus();
        void onReceive(Context context, Intent intent);
        void newStatus();
        void refresh();
    }
}
