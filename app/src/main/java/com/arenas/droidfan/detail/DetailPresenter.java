package com.arenas.droidfan.detail;

import android.content.Context;
import android.database.CharArrayBuffer;
import android.widget.Toast;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.DateTimeUtils;
import com.arenas.droidfan.data.FavoritesColumns;
import com.arenas.droidfan.data.StatusColumns;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.main.message.MessageContract;
import com.arenas.droidfan.main.message.chat.ChatActivity;
import com.arenas.droidfan.profile.ProfileActivity;
import com.arenas.droidfan.service.FanFouService;
import com.arenas.droidfan.update.UpdateActivity;

/**
 * Created by Arenas on 2016/7/15.
 */
public class DetailPresenter implements DetailContract.Presenter , DataSource.GetStatusCallback{

    private static final String TAG = DetailPresenter.class.getSimpleName();

    private final FanFouDB mFanFouDB;
    private final DetailContract.View mView;

    private StatusModel mStatusModel;
    private int m_id;
    private boolean mIsFavorite;
    private int mActionType;
    private Context mContext;

    public DetailPresenter(int _id , int type , Context context , DetailContract.View mView) {
        this.mFanFouDB = FanFouDB.getInstance(context);
        this.mView = mView;
        m_id = _id;
        mActionType = type;

        mContext = context;

        mView.setPresenter(this);
    }

    @Override
    public void start() {
        switch (mActionType){
            case DetailActivity.TYPE_HOME:
                mFanFouDB.getHomeTLStatus(m_id , this);
                break;
            case DetailActivity.TYPE_MENTIONS:
                mFanFouDB.getNoticeStatus(m_id , this);
                break;
            case DetailActivity.TYPE_PUBLIC:
                mFanFouDB.getPublicStatus(m_id , this);
                break;
            case DetailActivity.TYPE_PROFILE:
                mFanFouDB.getProfileStatus(m_id , this);
                break;
            case DetailActivity.TYPE_FAVORITES:
                mFanFouDB.getFavorite(m_id , this);
                break;
        }

    }

    @Override
    public void onStatusLoaded(StatusModel statusModel) {
        mStatusModel = statusModel;
        if (mStatusModel.getFavorited() == 1){
            mIsFavorite = true;
        }
        showUi(statusModel);
    }

    @Override
    public void onDataNotAvailable() {
        mView.showError();
    }

    private void showUi(StatusModel statusModel){
        mView.showAvatar(statusModel.getUserProfileImageUrl());
        mView.showUsername(statusModel.getUserScreenName());
        mView.showUserId("@" + statusModel.getUserId());
        mView.showStatusText(statusModel.getText());
        mView.showDate(DateTimeUtils.formatDate(statusModel.getTime()));
        mView.showSource("通过"+statusModel.getSource());
        mView.showPhoto(statusModel.getPhotoLargeUrl());
        if (mIsFavorite){
            mView.showFavorite(R.drawable.ic_favorite_black);
        }
        if (mStatusModel.getUserId().equals(AppContext.getAccount())){
            mView.showDelete();
        }
    }

    @Override
    public void reply() {
        UpdateActivity.start(mContext , m_id , UpdateActivity.TYPE_REPLY , mActionType);
    }

    @Override
    public void retweet() {
        UpdateActivity.start(mContext , m_id , UpdateActivity.TYPE_RETWEET , mActionType);
    }

    @Override
    public void delete() {
        FanFouService.delete(mContext , mStatusModel.getId());
        mView.finish();
    }

    @Override
    public void favorite() {
        if (mIsFavorite){
            mView.showFavorite(R.drawable.ic_favorite_grey);
            mFanFouDB.updateFavorite(m_id , 0 );
            FanFouService.unfavorite(mContext , mStatusModel.getId());
            mFanFouDB.deleteItem(FavoritesColumns.TABLE_NAME , mStatusModel.getId());
            mIsFavorite = false;
        }else {
            mView.showFavorite(R.drawable.ic_favorite_black);
            mFanFouDB.updateFavorite(m_id , 1);
            FanFouService.favorite(mContext , mStatusModel.getId());
            mIsFavorite = true;
        }
    }

    @Override
    public void sendMessage() {
        ChatActivity.start(mContext , mStatusModel.getUserId() , mStatusModel.getUserScreenName());
    }

    @Override
    public void openUser() {
        ProfileActivity.start(mContext , mStatusModel.getUserId());
    }
}
