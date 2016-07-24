package com.arenas.droidfan.users;

import android.content.Context;
import android.content.Intent;

import com.arenas.droidfan.BasePresenter;
import com.arenas.droidfan.BaseView;
import com.arenas.droidfan.api.rest.UsersMethods;
import com.arenas.droidfan.data.model.UserModel;

import java.util.List;

/**
 * Created by Arenas on 2016/7/23.
 */
public interface UserContract {

    interface Presenter extends BasePresenter{
        void onReceive(Context context , Intent intent);
    }

    interface View extends BaseView{
        void showTitle(String title);
        void showProgressbar();
        void hideProgressbar();
        void showUsers(List<UserModel> users);
        void showError(String errorText);
    }
}
