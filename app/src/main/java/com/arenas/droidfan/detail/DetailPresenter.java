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
import com.arenas.droidfan.main.hometimeline.HomeTimelineFragment;
import com.arenas.droidfan.main.message.chat.ChatActivity;
import com.arenas.droidfan.photo.PhotoActivity;
import com.arenas.droidfan.profile.ProfileActivity;
import com.arenas.droidfan.update.UpdateActivity;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Arenas on 2016/7/15.
 */
public class DetailPresenter implements DetailContract.Presenter {

    private static final String TAG = DetailPresenter.class.getSimpleName();

    private final FanFouDB mFanFouDB;
    private final DetailContract.View mView;

    private StatusModel mStatusModel;
    private boolean mIsFavorite;
    private Context mContext;
    private int position;
    private boolean completeStart;

    private Api api;

    public DetailPresenter(Context context , DetailContract.View mView , StatusModel statusModel , int position ) {
        this.mFanFouDB = FanFouDB.getInstance(context);
        this.mView = mView;
        this.position = position;

        mStatusModel = statusModel;
        mContext = context;
        api = AppContext.getApi();
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        if (!completeStart){
            showUi(mStatusModel);
            if (mStatusModel.getInReplyToStatusId() != null){
                mView.showProgressBar();
                fetchStatusContext();
            }
            completeStart = true;
        }
    }

    private boolean isFavorite(){
        return mStatusModel.getFavorited() == 1;
    }

    private void showUi(StatusModel statusModel){
        mView.showAvatar(statusModel.getUserProfileImageUrl());
        mView.showUsername(statusModel.getUserScreenName());
        mView.showUserId("@" + statusModel.getUserId());
        mView.showStatusText(statusModel.getText());
        mView.showDate(DateTimeUtils.formatDate(statusModel.getTime()));
        mView.showSource(" 通过"+statusModel.getSource());
        mView.showPhoto(statusModel.getPhotoLargeUrl());
        if (isFavorite()){
            mView.showFavorite(R.drawable.ic_favorite_red);
        }
        if (mStatusModel.getUserId().equals(AppContext.getAccount())){
            mView.showDelete();
        }
    }

    @Override
    public void reply() {
        UpdateActivity.start(mContext , mStatusModel , UpdateActivity.TYPE_REPLY);
    }

    @Override
    public void retweet() {
        UpdateActivity.start(mContext , mStatusModel , UpdateActivity.TYPE_RETWEET);
    }

    @Override
    public void delete() {
        if (!NetworkUtils.isNetworkConnected(mContext)){
            Utils.showToast(mContext , mContext.getString(R.string.network_is_disconnected));
            return;
        }
        rx.Observable.create(new rx.Observable.OnSubscribe<StatusModel>() {
            @Override
            public void call(Subscriber<? super StatusModel> subscriber) {
                try{
                    //先这么蛮横的干着再说 以后再改
                    //等整体框架确定好之后再改mFanFouDB.deleteItem(NoticeColumns.TABLE_NAME , mStatusModel.getId());
                    mFanFouDB.deleteItem(HomeStatusColumns.TABLE_NAME , mStatusModel.getId());
                    mFanFouDB.deleteItem(NoticeColumns.TABLE_NAME , mStatusModel.getId());
                    mFanFouDB.deleteItem(ProfileColumns.TABLE_NAME , mStatusModel.getId());
                    mFanFouDB.deleteItem(FavoritesColumns.TABLE_NAME , mStatusModel.getId());
                    mFanFouDB.deleteItem(PublicStatusColumns.TABLE_NAME , mStatusModel.getId());
                    api.deleteStatus(mStatusModel.getId());
                    subscriber.onNext(null);
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

                mView.finish();
            }

            @Override
            public void onNext(StatusModel model) {
                mView.setResult(DetailActivity.RESULT_DELETE , position );
                mView.finish();
            }
        });
    }

    @Override
    public void favorite() {
        if (!NetworkUtils.isNetworkConnected(mContext)){
            Utils.showToast(mContext , mContext.getString(R.string.network_is_disconnected));
            return;
        }
        Observable.create(new Observable.OnSubscribe<StatusModel>() {
            @Override
            public void call(Subscriber<? super StatusModel> subscriber) {

                try{
                    StatusModel model;
                    if (isFavorite()){
                        model = api.unfavorite(mStatusModel.getId());
                    }else {
                        model = api.favorite(mStatusModel.getId());
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
                Utils.showToast(mContext , "操作失败！");
            }

            @Override
            public void onNext(StatusModel model) {
                if (isFavorite()){
                    // TODO: 2016/10/5 这里有bug，不过先这样吧
                    mView.showFavorite(R.drawable.ic_favorite_grey);
                    mFanFouDB.deleteItem(FavoritesColumns.TABLE_NAME , mStatusModel.getId());
                    mStatusModel.setFavorited(0);
                    //数据库更新N个地方 oh no !先这么将就着
                    mFanFouDB.updateFavorite(HomeStatusColumns.TABLE_NAME , mStatusModel.get_id() , 0);
                    mFanFouDB.updateFavorite(PublicStatusColumns.TABLE_NAME , mStatusModel.get_id() , 0);
                    mFanFouDB.updateFavorite(NoticeColumns.TABLE_NAME , mStatusModel.get_id() , 0);
                    mFanFouDB.updateFavorite(FavoritesColumns.TABLE_NAME , mStatusModel.get_id() , 0);
                    mFanFouDB.updateFavorite(ProfileColumns.TABLE_NAME , mStatusModel.get_id() , 0);
                }else {
                    mView.showFavorite(R.drawable.ic_favorite_red);
                    mStatusModel.setFavorited(1);
                    mFanFouDB.updateFavorite(HomeStatusColumns.TABLE_NAME , mStatusModel.get_id() , 1);
                    mFanFouDB.updateFavorite(PublicStatusColumns.TABLE_NAME , mStatusModel.get_id() , 1);
                    mFanFouDB.updateFavorite(NoticeColumns.TABLE_NAME , mStatusModel.get_id() , 1);
                    mFanFouDB.updateFavorite(FavoritesColumns.TABLE_NAME , mStatusModel.get_id() , 1);
                    mFanFouDB.updateFavorite(ProfileColumns.TABLE_NAME , mStatusModel.get_id() , 1);
                }
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
        PhotoActivity.start(mContext , mStatusModel);
    }

    private void fetchStatusContext(){
        Observable.create(new Observable.OnSubscribe<List<StatusModel>>() {
            @Override
            public void call(Subscriber<? super List<StatusModel>> subscriber) {

                try{
                    List<StatusModel> statusModelList = api.getContextTimeline(mStatusModel.getId());
                    subscriber.onNext(statusModelList);
                    subscriber.onCompleted();
                }catch (ApiException e){
                    subscriber.onError(e);
                }

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new rx.Observer<List<StatusModel>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                mView.hideProgressBar();
            }

            @Override
            public void onNext(List<StatusModel> statusModelList) {
                mView.hideProgressBar();
                mView.showStatusContext(statusModelList);
            }
        });
    }

}
