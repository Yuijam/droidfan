package com.arenas.droidfan.users;

import com.arenas.droidfan.BasePresenter;
import com.arenas.droidfan.BaseView;
import com.arenas.droidfan.data.model.UserModel;

import java.util.List;

/**
 * Created by Arenas on 2016/7/23.
 */
public interface UserContract {

    interface Presenter extends BasePresenter{
        void refresh();
        void getMore();
    }

    interface View extends BaseView{
        void showProgressbar();
        void hideProgressbar();
        void showUsers(List<UserModel> users);
        void showError(String errorText);
    }
}
