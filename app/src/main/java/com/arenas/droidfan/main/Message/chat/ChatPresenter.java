package com.arenas.droidfan.main.message.chat;

import android.content.Context;
import android.content.Intent;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.DirectMessageModel;
import com.arenas.droidfan.service.FanFouService;

import java.util.List;

/**
 * Created by Arenas on 2016/7/26.
 */
public class ChatPresenter implements ChatContract.Presenter , DataSource.LoadDMCallback{

    private FanFouDB mFanFouDB;
    private ChatContract.View mView;
    private Context mContext;

    private String mUserId;
    private DirectMessageModel model;

    private boolean mIsFirstFetch;

    public ChatPresenter(String userId , FanFouDB mFanFouDB, ChatContract.View mView, Context mContext) {
        this.mFanFouDB = mFanFouDB;
        this.mView = mView;
        this.mContext = mContext;
        mUserId = userId;

        mIsFirstFetch = true;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        loadDM();
    }

    private void loadDM(){
        mView.showProgressbar();
        mFanFouDB.getConversation(mUserId , this);
    }

    @Override
    public void onDMLoaded(List<DirectMessageModel> messageModels) {
        model = messageModels.get(0);
        mView.hideProgressbar();
        mView.showChatItems(messageModels);
        mView.showTitle(getTitle());
    }

    private String getTitle(){
        return model.getSenderScreenName().equals(AppContext.getScreenName()) ?
                model.getRecipientScreenName() : model.getSenderScreenName();
    }

    @Override
    public void onDataNotAvailable() {
        if (mIsFirstFetch){
            fetchData();
        }else {
            mView.showError("on chat data not available!");
        }
    }

    private void fetchData(){
        FanFouService.getConversation(mContext , new Paging() , mUserId);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mIsFirstFetch = false;
        loadDM();
    }

    @Override
    public void refresh() {
        fetchData();
    }

    @Override
    public void send(String text) {
        FanFouService.sendDM(mContext , AppContext.getAccount() , "6627741" , text);
    }
}
