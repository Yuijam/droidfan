package com.arenas.droidfan.photo;

import android.content.Context;
import android.text.style.ImageSpan;
import android.util.Log;

import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.StatusModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arenas on 2016/7/28.
 */
public class PhotoPresenter implements PhotoContract.Presenter , DataSource.LoadStatusCallback {

    private static final String TAG = PhotoPresenter.class.getSimpleName();

    private FanFouDB mFanFouDB;
    private PhotoContract.View mView;
    private String mTableName;
    private int m_id;
    private String mUserId;
    private Context mContext;
    private List<StatusModel> mDatas;
    int mPosition;

    public PhotoPresenter(Context context , PhotoContract.View view ,
                          String table , int _id , String userId , int position){
        mView = view;
        mFanFouDB = FanFouDB.getInstance(context);
        mTableName = table;
        mContext = context;
        m_id = _id;
        mUserId = userId;
        mPosition = position;
        Log.d(TAG , "mUserId = " + userId);
        mDatas = new ArrayList<>();

        mView.setPresenter(this);
    }

    @Override
    public void start() {
        if (isTimelinePhoto()){
            loadTimelineData();
            mView.showPhoto(mDatas);
        }else {
            loadProfileData();
        }
    }

    private boolean isTimelinePhoto(){
        return mUserId == null;
    }

    private void loadTimelineData(){
        mDatas.add(mFanFouDB.getStatus(m_id , mTableName));
    }

    private void loadProfileData(){
        mFanFouDB.loadPhotoTimeline(mUserId , this);
    }

    @Override
    public void onStatusLoaded(List<StatusModel> status) {
        mDatas = status;
        mView.showPhoto(status);
        mView.setCurrentPage(mPosition);
    }

    @Override
    public void onDataNotAvailable() {
        mView.showError("found nothing !");
    }
}
