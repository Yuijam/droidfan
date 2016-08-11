package com.arenas.droidfan.main.message;

import android.content.Context;
import android.content.Intent;

import com.arenas.droidfan.BasePresenter;
import com.arenas.droidfan.BaseView;
import com.arenas.droidfan.data.model.DirectMessageModel;

import java.util.List;

/**
 * Created by Arenas on 2016/7/26.
 */
public interface MessageContract {
    interface View extends BaseView{
        void showProgressbar();
        void hideProgressbar();
        void showError(String text);
        void showList(List<DirectMessageModel> models);
    }

    interface Presenter extends BasePresenter{
        void refresh();
        void onReceive(Context context , Intent intent);
        void getMore();
//        void openConversation( )
    }
}
