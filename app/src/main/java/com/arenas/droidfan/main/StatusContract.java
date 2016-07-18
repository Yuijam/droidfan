package com.arenas.droidfan.main;

import android.content.Context;
import android.content.Intent;

import com.arenas.droidfan.Api.Paging;
import com.arenas.droidfan.BasePresenter;
import com.arenas.droidfan.BaseView;
import com.arenas.droidfan.data.model.StatusModel;

import java.util.List;

/**
 * Created by Arenas on 2016/7/18.
 */
public interface StatusContract {
    interface View extends BaseView {
        void showStatus(List<StatusModel> status);
        void startService(Paging p);
        void showError();
        void showUpdateStatusUi();
        void hideRefreshBar();
        void showRefreshBar();
    }

    interface Presenter extends BasePresenter {
        void loadStatus();
        void onReceive(Context context, Intent intent);
        void newStatus();
        void refresh();
    }
}
