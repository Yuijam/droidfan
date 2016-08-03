package com.arenas.droidfan.main.message.chat;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;
import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.DirectMessageModel;
import com.arenas.droidfan.data.model.UserModel;
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
    private boolean mIsAFollower;
    private boolean mTestAvailable;
    private boolean mIsRefresh;

//    private boolean mIsFirstFetch;

    public ChatPresenter(String userId , String username , ChatContract.View mView, Context mContext) {
        this.mFanFouDB = FanFouDB.getInstance(mContext);
        this.mView = mView;
        this.mContext = mContext;
        mUserId = userId;
        mUsername = username;
        Log.d(TAG , "userId = " + userId);
        mTestAvailable = true;
//        mIsFirstFetch = true;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        fetchData();
        testFollower();
    }

    private void testFollower(){
        FanFouService.testChatAvailable(mContext , mUserId);
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
        if (!mIsRefresh){
            mView.scrollTo(messageModels.size());
        }
    }

    @Override
    public void onDataNotAvailable() {
//        if (mIsFirstFetch){
//            fetchData();
//        }
        mView.showError("Oops ~ 未获取到数据");
    }

    private void fetchData(){
        mView.showProgressbar();
        Paging paging = new Paging();
        paging.count = 60;
        paging.sinceId = mFanFouDB.getDMSinceId(mUsername);
        FanFouService.getConversation(mContext , paging , mUserId);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        mIsFirstFetch = false;
        if (mTestAvailable){
            mIsAFollower = intent.getBooleanExtra(FanFouService.EXTRA_IS_FRIEND , false);
            if (mIsAFollower){
                mView.showEditMessageLayout();
            }else {
                mView.showEditInvalidNotice();
            }
            mTestAvailable = false;
        }
        loadDM();
    }

    @Override
    public void refresh() {
        mIsRefresh = true;
        getMore();
    }

    private void getMore(){
        Paging paging = new Paging();
        paging.maxId = mFanFouDB.getDMMaxId(mUsername);
        FanFouService.getConversation(mContext , paging , mUserId);
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
        mView.emptyInput();
    }
}
