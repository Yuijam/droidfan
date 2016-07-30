package com.arenas.droidfan.main.message.chat;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;
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

    private static final String TAG = ChatPresenter.class.getSimpleName();

    private FanFouDB mFanFouDB;
    private ChatContract.View mView;
    private Context mContext;

    private String mUserId;
    private DirectMessageModel model;
    private String mUsername;

    private boolean mIsFirstFetch;

    public ChatPresenter(String userId , String username , ChatContract.View mView, Context mContext) {
        this.mFanFouDB = FanFouDB.getInstance(mContext);
        this.mView = mView;
        this.mContext = mContext;
        mUserId = userId;
        mUsername = username;
        Log.d(TAG , "userId = " + userId);

        mIsFirstFetch = true;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        loadDM();
    }

    private void loadDM(){
        mView.showProgressbar();
        mFanFouDB.getConversation(mUsername , this);
    }

    @Override
    public void onDMLoaded(List<DirectMessageModel> messageModels) {
        model = messageModels.get(0);
        mView.hideProgressbar();
        mView.showChatItems(messageModels);
    }

    @Override
    public void onDataNotAvailable() {
        if (mIsFirstFetch){
            fetchData();
        }
    }

    private void fetchData(){
        FanFouService.getConversation(mContext , new Paging() , mUserId);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mIsFirstFetch = false;
        loadDM();
        mView.emptyInput();
    }

    @Override
    public void refresh() {
        fetchData();
    }

    @Override
    public void send(String text) {
        if (TextUtils.isEmpty(text)){
            mView.showError(mContext.getString(R.string.text_should_not_empty));
            return;
        }
        if (text.length() > 140){
            mView.showError(mContext.getString(R.string.text_too_more));
            return;
        }
        FanFouService.sendDM(mContext , mUserId , null , text);
    }

}
