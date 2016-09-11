package com.arenas.droidfan.main.hometimeline;

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
        void hideProgressBar();
        void showProgressBar();
        void removeStatusItem(int position);
        void goToTop();
    }

    interface Presenter extends BasePresenter{
        void loadStatus();
        void refresh();
        void getMore();
    }
}
