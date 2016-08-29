package com.arenas.droidfan.detail;

import android.content.Context;
import android.util.Log;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.DateTimeUtils;
import com.arenas.droidfan.Util.NetworkUtils;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.api.Api;
import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.data.FavoritesColumns;
import com.arenas.droidfan.data.HomeStatusColumns;
import com.arenas.droidfan.data.NoticeColumns;
import com.arenas.droidfan.data.ProfileColumns;
import com.arenas.droidfan.data.PublicStatusColumns;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.main.TabFragmentAdapter;
import com.arenas.droidfan.main.message.chat.ChatActivity;
import com.arenas.droidfan.photo.PhotoActivity;
import com.arenas.droidfan.profile.ProfileActivity;
import com.arenas.droidfan.update.UpdateActivity;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
    private String tableName;
    private int position;

    private Api api;

    public DetailPresenter(int _id , int type , Context context , DetailContract.View mView , int position) {
        this.mFanFouDB = FanFouDB.getInstance(context);
        this.mView = mView;
        m_id = _id;
        mActionType = type;
        this.position = position;

        mContext = context;
        api = AppContext.getApi();
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        switch (mActionType){
            case DetailActivity.TYPE_HOME:
                mFanFouDB.getHomeTLStatus(m_id , this);
                tableName = HomeStatusColumns.TABLE_NAME;
                break;
            case DetailActivity.TYPE_MENTIONS:
                mFanFouDB.getNoticeStatus(m_id , this);
                tableName = NoticeColumns.TABLE_NAME;
                break;
            case DetailActivity.TYPE_PUBLIC:
                mFanFouDB.getPublicStatus(m_id , this);
                tableName = PublicStatusColumns.TABLE_NAME;
                break;
            case DetailActivity.TYPE_PROFILE:
                mFanFouDB.getProfileStatus(m_id , this);
                tableName = ProfileColumns.TABLE_NAME;
                break;
            case DetailActivity.TYPE_FAVORITES:
                Log.d(TAG , "m_id = " + m_id);
                mFanFouDB.getFavorite(m_id , this);
                tableName = FavoritesColumns.TABLE_NAME;
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
        mView.showSource(" 通过"+statusModel.getSource());
        mView.showPhoto(statusModel.getPhotoImageUrl());
        if (mIsFavorite){
            mView.showFavorite(R.drawable.ic_favorite_red);
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
        rx.Observable.create(new rx.Observable.OnSubscribe<StatusModel>() {
            @Override
            public void call(Subscriber<? super StatusModel> subscriber) {
                if (!NetworkUtils.isNetworkConnected(mContext)){
                    Utils.showToast(mContext , mContext.getString(R.string.network_is_disconnected));
                    return;
                }

                try{
                    Log.d(TAG , "observable thread = " + Thread.currentThread().getId());
                    StatusModel model = api.deleteStatus(mStatusModel.getId());
                    mFanFouDB.deleteItem(HomeStatusColumns.TABLE_NAME , mStatusModel.getId());
                    mFanFouDB.deleteItem(ProfileColumns.TABLE_NAME , mStatusModel.getId());
                    subscriber.onNext(model);
                    subscriber.onCompleted();
                }catch (ApiException e){
                    subscriber.onError(e);
                }

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new rx.Observer<StatusModel>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(StatusModel model) {
//                Log.d(TAG , "observer thread = " + Thread.currentThread().getId());
                // TODO: 2016/8/15  要操作一下麽?删除失败什么的 啊 还是先这样吧 有问题再说
            }
        });
        mView.setResult(DetailActivity.RESULT_DELETE , position , mActionType);
        mView.finish();
    }



    @Override
    public void favorite() {
        if (mIsFavorite){//数据库更新N个地方 oh no !
            mView.showFavorite(R.drawable.ic_favorite_grey);
            mFanFouDB.deleteItem(FavoritesColumns.TABLE_NAME , mStatusModel.getId());
            mFanFouDB.updateFavorite(tableName , m_id , 0);
        }else {
            mView.showFavorite(R.drawable.ic_favorite_red);
            mFanFouDB.updateFavorite(tableName , m_id , 1);
        }

        Observable.create(new Observable.OnSubscribe<StatusModel>() {
            @Override
            public void call(Subscriber<? super StatusModel> subscriber) {
                if (!NetworkUtils.isNetworkConnected(mContext)){
                    Utils.showToast(mContext , mContext.getString(R.string.network_is_disconnected));
                    return;
                }

                try{
                    Log.d(TAG , "observable thread = " + Thread.currentThread().getId());
                    StatusModel model;
                    if (mIsFavorite){
                        model = api.unfavorite(mStatusModel.getId());
                        mIsFavorite = false;
                    }else {
                        model = api.favorite(mStatusModel.getId());
                        mIsFavorite = true;
                    }
                    subscriber.onNext(model);
                    subscriber.onCompleted();
                }catch (ApiException e){
                    subscriber.onError(e);
                }

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new rx.Observer<StatusModel>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(StatusModel model) {
//                Log.d(TAG , "observer thread = " + Thread.currentThread().getId());
                // TODO: 2016/8/15  要操作一下麽?删除失败什么的 啊 还是先这样吧 有问题再说
            }
        });
    }

    @Override
    public void sendMessage() {
        ChatActivity.start(mContext , mStatusModel.getUserId() , mStatusModel.getUserScreenName());
    }

    @Override
    public void openUser() {
        ProfileActivity.start(mContext , mStatusModel.getUserId());
    }

    @Override
    public void showLargePhoto() {
        PhotoActivity.start(mContext , m_id , tableName , null , -1);
    }
}
