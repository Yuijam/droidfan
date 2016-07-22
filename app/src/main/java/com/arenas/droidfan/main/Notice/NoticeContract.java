package com.arenas.droidfan.main.notice;

import com.arenas.droidfan.BasePresenter;
import com.arenas.droidfan.BaseView;

/**
 * Created by Arenas on 2016/7/18.
 */
public interface NoticeContract {

    interface View extends BaseView{

    }

    interface Presenter extends BasePresenter{
        void refresh();
    }
}
