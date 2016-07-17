package com.arenas.droidfan.detail;

import android.content.Context;
import android.util.Log;

import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.DateTimeUtils;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.data.StatusColumns;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.StatusModel;
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

    public DetailPresenter(int _id ,  FanFouDB mFanFouDB , DetailContract.View mView) {
        this.mFanFouDB = mFanFouDB;
        this.mView = mView;
        m_id = _id;

        mView.setPresenter(this);
    }

    @Override
    public void start() {
        mFanFouDB.getStatus(m_id , this);
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
        mView.showPhoto(statusModel.getPhotoThumbUrl());
        if (mIsFavorite){
            mView.showFavorite(R.drawable.ic_favorite_black);
        }
    }

    @Override
    public void reply(Context context) {
        UpdateActivity.start(context , m_id , UpdateActivity.TYPE_REPLY);
    }

    @Override
    public void retweet(Context context) {
        UpdateActivity.start(context , m_id , UpdateActivity.TYPE_RETWEET);
    }

    @Override
    public void favorite(Context context) {
        if (mIsFavorite){
            mView.showFavorite(R.drawable.ic_favorite_grey);
            mFanFouDB.updateFavorite(m_id , 0 );
            FanFouService.unfavorite(context , mStatusModel.getId());
            mIsFavorite = false;
        }else {
            mView.showFavorite(R.drawable.ic_favorite_black);
            mFanFouDB.updateFavorite(m_id , 1);
            FanFouService.favorite(context , mStatusModel.getId());
            mIsFavorite = true;
        }
    }
}
