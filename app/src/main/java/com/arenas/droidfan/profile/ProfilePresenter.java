package com.arenas.droidfan.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.NetworkUtils;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.UserModel;
import com.arenas.droidfan.main.message.chat.ChatActivity;
import com.arenas.droidfan.users.UserListActivity;

import org.greenrobot.eventbus.EventBus;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Arenas on 2016/7/22.
 */
public class ProfilePresenter implements ProfileContract.Presenter , DataSource.GetUserCallback{

    private static final String TAG = ProfilePresenter.class.getSimpleName();

    private FanFouDB mFanFouDB;
    private ProfileContract.View mView;
    private String mUserId;
    private Context mContext;
    private UserModel mUser;
    private EventBus eventBus;

    private boolean mIsAFollower;
    private boolean testFollowerComplete;
    private boolean fetchUserComplete;

    private boolean startComplete;

    private static final int TYPE_FOLLOW = 0;
    private static final int TYPE_UNFOLLOW= 1;

    public ProfilePresenter(ProfileContract.View mView , String userId ,Context context) {
        this.mFanFouDB = FanFouDB.getInstance(context);
        this.mView = mView;
        mUserId = userId;
        mContext = context;
        eventBus = EventBus.getDefault();
    }

    @Override
    public void start() {
        if (!startComplete){
            mView.showProgress();
            if (!isMe()){
                testFollower();
                fetchUser();
            }else {
                postLoadData();
                loadUser();
            }
            startComplete = true;
        }
    }

    private void postLoadData(){
        eventBus.postSticky(new ProfileEvent(true , false));
    }

    private void postDoNotLoadData(){
        eventBus.postSticky(new ProfileEvent(false , false));
    }

    private void postRefresh(){
        eventBus.postSticky(new ProfileEvent(false , true));
    }


    private boolean isMe(){
        return mUserId.equals(AppContext.getAccount());
    }

    private void loadUser(){
        mFanFouDB.getUserById(mUserId , this);
    }

    @Override
    public void onUserLoaded(UserModel userModel) {
        mUser = userModel;
        if (isStatusAvailable()){
            postLoadData();
        }else {
            postDoNotLoadData();
            Utils.showToast(mContext , mContext.getString(R.string.error_protected));
        }
        mView.hideProgress();
        initView(userModel);
        Log.d(TAG , "onUserLoaded------->");
    }

    @Override
    public void onDataNotAvailable() {
        fetchUser();
    }

    private void fetchUser() {
        Log.d(TAG , "fetchUser!!!");
        if (!NetworkUtils.isNetworkConnected(mContext)){
            Utils.showToast(mContext , mContext.getString(R.string.network_is_disconnected));
            mView.hideProgress();
            return;
        }
        rx.Observable.create(new rx.Observable.OnSubscribe<UserModel>() {
            @Override
            public void call(Subscriber<? super UserModel> subscriber) {
                try{
                    Log.d(TAG , "observable thread = " + Thread.currentThread().getId());
                    UserModel model = AppContext.getApi().showUser(mUserId);
                    subscriber.onNext(model);
                    subscriber.onCompleted();
                }catch (ApiException e){
                    subscriber.onError(e);
                }

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new rx.Observer<UserModel>() {
            @Override
            public void onCompleted() {
                Log.d(TAG , "onCompleted~~");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG , e.toString());
                mView.hideProgress();
            }

            @Override
            public void onNext(UserModel models) {
                Log.d(TAG , "observer thread = " + Thread.currentThread().getId());
                if(models != null){
                    mFanFouDB.saveUser(models , 0);
                    fetchUserComplete = true;
                    if (isMe() || testFollowerComplete){
                        loadUser();
                    }
                }else {
                    mView.hideProgress();
                    Utils.showToast(mContext , "未找到该用户！");
                }
            }
        });
    }

    private void testFollower(){
        Log.d(TAG , "testFollower~");
        rx.Observable.create(new rx.Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try{
                    Log.d(TAG , "observable thread = " + Thread.currentThread().getId());
                    if (!NetworkUtils.isNetworkConnected(mContext)){
                        Utils.showToast(mContext , mContext.getString(R.string.network_is_disconnected));
                        return;
                    }
                    boolean isAFollower = AppContext.getApi().isFriends(mUserId , AppContext.getAccount());
                    subscriber.onNext(isAFollower);
                    subscriber.onCompleted();
                }catch (ApiException e){
                    subscriber.onError(e);
                }

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new rx.Observer<Boolean>() {
            @Override
            public void onCompleted() {
                Log.d(TAG , "onCompleted~~");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Boolean isAFollower) {
                Log.d(TAG , "observer thread = " + Thread.currentThread().getId());
                testFollowerComplete = true;
                mIsAFollower = isAFollower;
                if (fetchUserComplete){
                    loadUser();
                }
            }
        });
    }

    private void initView(UserModel user){
        mView.showAvatar(user.getProfileImageUrlLarge());
        mView.showUserId("@"+user.getId());
        mView.showFavoriteCount(user.getFavouritesCount());
        mView.showFollowerCount(user.getFollowersCount());
        mView.showFollowingCount(user.getFriendsCount());
        mView.showStatusCount(user.getStatusesCount());
        mView.showTitle(user.getScreenName());
        mView.showDescription(Utils.handleDescription(user.getDescription()));
        if (!isMe()){
            Log.d(TAG , "!me showView");
            mView.showFoButton(getFollowState());
            if (mIsAFollower){
                mView.showDMView();
            }
//            if (!isStatusAvailable()){
//                //show toast 的工作交给 ProfileStatus
//            }
        }
    }

    private int getFollowState(){
        if (isFollowing() && mIsAFollower){
//            return "互相关注中";
            return R.drawable.ic_foer_and_foing;
        }else if (isFollowing()){
//            return "正在关注";
            return R.drawable.ic_following;
        }else {
//            return "添加关注";
            return R.drawable.ic_follow_add;
        }
    }

    private boolean isFollowing(){
        return mUser.getFollowing() == 1;
    }

    @Override
    public void follow() {
        if (isFollowing()){
            showDialog("取消关注？");
        }else {
            executeFoAndUnfo();
        }
    }

    private boolean isProtected(){
        return mUser.getProtect() == 1;
    }

    @Override
    public void openFollower() {
        if (!isStatusAvailable()){
            mView.showError("只向关注TA的人公开");
        }else {
            UserListActivity.start(mContext , mUserId , UserListActivity.TYPE_FOLLOWERS);
        }
    }

    @Override
    public void openFollowing() {
        if (!isStatusAvailable()){
            mView.showError("只向关注TA的人公开");
        }else {
            UserListActivity.start(mContext, mUserId, UserListActivity.TYPE_FOLLOWING);
        }
    }

    private boolean isStatusAvailable(){
        return mUserId.equals(AppContext.getAccount()) || isFollowing() || !isProtected();
    }

    @Override
    public void refresh() {
        postRefresh();
        testFollower();
        fetchUser();
    }

    private void showDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AlertDialog dialog = builder.setMessage(message).setNegativeButton(mContext.getString(R.string.cancel)
                , null).setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                executeFoAndUnfo();
            }
        }).create();
        dialog.show();
    }

    private void executeFoAndUnfo(){
        if (isFollowing()){
            //unfo
            mView.showFoButton(R.drawable.ic_follow_add);
            follow(TYPE_UNFOLLOW);
        }else if (!isStatusAvailable()){
            //fo protected user
            Utils.showToast(mContext , "已发送关注请求,等待对方确认！");
            follow(TYPE_FOLLOW);
        }else {
            //fo general user
            mView.showFoButton(R.drawable.ic_following);
            follow(TYPE_FOLLOW);
        }
//        refresh();
    }

    private void follow(final int type){
        rx.Observable.create(new rx.Observable.OnSubscribe<UserModel>() {
            @Override
            public void call(Subscriber<? super UserModel> subscriber) {

                if (!NetworkUtils.isNetworkConnected(mContext)){
                    Utils.showToast(mContext , mContext.getString(R.string.network_is_disconnected));
                    return;
                }

                try{
                    Log.d(TAG , "observable thread = " + Thread.currentThread().getId());
                    UserModel model;
                    if (type == TYPE_FOLLOW){
                        model = AppContext.getApi().follow(mUserId);
                    }else {
                        model = AppContext.getApi().unfollow(mUserId);
                    }
                    subscriber.onNext(model);
                    subscriber.onCompleted();
                }catch (ApiException e){
                    subscriber.onError(e);
                }

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new rx.Observer<UserModel>() {
            @Override
            public void onCompleted() {
                Log.d(TAG , "onCompleted~~");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(UserModel model) {
                Log.d(TAG , "observer thread = " + Thread.currentThread().getId());
                if (model == null){
                    Utils.showToast(mContext , "请求失败！");
                }
            }
        });
    }

    @Override
    public void openChatView() {
        ChatActivity.start(mContext , mUserId , mUser.getScreenName());
    }
}