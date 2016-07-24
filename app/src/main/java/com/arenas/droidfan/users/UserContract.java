package com.arenas.droidfan.users;

import com.arenas.droidfan.BasePresenter;
import com.arenas.droidfan.BaseView;

/**
 * Created by Arenas on 2016/7/23.
 */
public interface UserContract {

    interface Presenter extends BasePresenter{

    }

    interface View extends BaseView{
        void showTitle(String title);
        void showProgressbar();
        void hideProgressbar();
    }
}
