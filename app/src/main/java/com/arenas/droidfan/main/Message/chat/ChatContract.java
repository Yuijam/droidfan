package com.arenas.droidfan.main.message.chat;

import android.content.Context;
import android.content.Intent;

import com.arenas.droidfan.BasePresenter;
import com.arenas.droidfan.BaseView;
import com.arenas.droidfan.adapter.ConversationListAdapter;
import com.arenas.droidfan.data.model.DirectMessageModel;

import java.util.List;

/**
 * Created by Arenas on 2016/7/26.
 */
public interface ChatContract {
    interface View extends BaseView{
        void showChatItems(List<DirectMessageModel> models);

        void showError(String text);

        void showProgressbar();

        void hideProgressbar();

        void refreshComplete();

        void loadMoreComplete();

        void emptyInput();

        void showEditMessageLayout();

        void showEditInvalidNotice();

        void disableSend();

        void enableSend();

        void scrollTo(int position);
    }

    interface Presenter extends BasePresenter{

        void refresh();

        void getMore();

        void send(String text);
    }
}
