package com.arenas.droidfan.main.hometimeline;

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
        void showError(String error);
        void showUpdateStatusUi();
        void hideRefreshBar();
        void showRefreshBar();
    }

    interface Presenter extends BasePresenter{
        void loadStatus();
        void onReceive(Context context, Intent intent);
        void newStatus();
        void refresh();
        void getMore(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition);
    }
}
