package com.arenas.droidfan.main.message;

import android.content.Context;
import android.content.Intent;

import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.DirectMessageModel;
import com.arenas.droidfan.service.FanFouService;

import java.util.List;

/**
 * Created by Arenas on 2016/7/26.
 */
public class MessagePresenter implements MessageContract.Presenter , DataSource.LoadDMCallback{

    private String TAG = MessagePresenter.class.getSimpleName();

    private FanFouDB mFanFouDB;
    private MessageContract.View mView;
    private Context mContext;

    private List<DirectMessageModel> mDMList;

    private boolean mIsFirstFetch;

    public MessagePresenter(Context context, MessageContract.View mView ) {
        this.mFanFouDB = FanFouDB.getInstance(context);
        mContext = context;
        this.mView = mView;
        mIsFirstFetch = true;

        mView.setPresenter(this);
    }

    @Override
    public void start() {
        loadConversationList();
    }

    private void loadConversationList(){
        mView.showProgressbar();
        mFanFouDB.getConversationList(this);
    }

    @Override
    public void onDMLoaded(List<DirectMessageModel> messageModels) {
        mView.hideProgressbar();
        mDMList = messageModels;
        mView.showList(messageModels);
    }

    @Override
    public void onDataNotAvailable() {
        if (mIsFirstFetch){
            fetchData();
        }
        mView.hideProgressbar();
    }

    private void fetchData(){
        mView.showProgressbar();
        Paging paging = new Paging();
        paging.count = 60;
        FanFouService.getConversationList(mContext , new Paging());
    }

    @Override
    public void refresh() {
        fetchData();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mIsFirstFetch = false;
        loadConversationList();
    }

    @Override
    public void getMore(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {

    }
}
