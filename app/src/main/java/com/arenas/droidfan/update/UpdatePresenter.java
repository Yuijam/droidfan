package com.arenas.droidfan.update;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.CompatUtils;
import com.arenas.droidfan.Util.ImageUtils;
import com.arenas.droidfan.Util.NetworkUtils;
import com.arenas.droidfan.Util.StatusUtils;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.api.Api;
import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.Draft;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.data.model.UserModel;
import com.arenas.droidfan.detail.DetailActivity;
import com.arenas.droidfan.draft.DraftActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Arenas on 2016/7/11.
 */
public class UpdatePresenter implements UpdateContract.Presenter
        , DataSource.GetStatusCallback , DataSource.LoadUserCallback {

    private static final String TAG = UpdatePresenter.class.getSimpleName();

    public static final int REQUEST_STORAGE_PERMISSION = 4;
    public static final String STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

    private FanFouDB mFanFouDB;
    private final UpdateContract.View mView;

    private File mPhoto;
    private String mPhotoPath;
    private int m_Id = -1;
    private StatusModel mStatusModel;
    private int mActionType;
    private int mStatusType;
    private Context mContext;
    private String text;
    private Api api;
    private String id;//reply or retweet id
    private Activity activity;

    private int requestFlag;

    public UpdatePresenter(int _id ,int type , int statusType , Context context , UpdateContract.View mView) {
        this.mFanFouDB = FanFouDB.getInstance(context);
        this.mView = mView;

        m_Id = _id;
        mActionType = type;
        mStatusType = statusType;
        mContext = context;

        api = AppContext.getApi();
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        if (!isNewStatus()){
            populateStatusText();
        }
        loadFollowing();//为了获取@的列表
    }

    @Override
    public void update(Context context , String text ) {
        if (TextUtils.isEmpty(text) || text.length() >140){
            return;
        }
        this.text = text;
        updateStatus();
        mView.showHome();
    }

    private void loadFollowing(){
        mFanFouDB.getFollowing(AppContext.getAccount() , this);
    }

    @Override
    public void onUsersLoaded(List<UserModel> userModelList) {
        String[] users = new String[userModelList.size()];
        int i = 0;
        for (UserModel u : userModelList){
            users[i++] = "@" + u.getScreenName() + " ";
        }
        mView.setAutoTextAdapter(users);
    }

    @Override
    public void onDataNotAvailable() {
        mView.showError();
    }

    private void getFollowing(){
        rx.Observable.create(new rx.Observable.OnSubscribe<List<UserModel>>() {
            @Override
            public void call(Subscriber<? super List<UserModel>> subscriber) {

                if (!NetworkUtils.isNetworkConnected(mContext)){
                    Utils.showToast(mContext , mContext.getString(R.string.network_is_disconnected));
                    return;
                }

                try{
                    Log.d(TAG , "observable thread = " + Thread.currentThread().getId());
                    List<UserModel> models = api.getFriends(AppContext.getAccount() , null);
                    mFanFouDB.saveFollowing(models , AppContext.getAccount());

                    subscriber.onNext(models);
                    subscriber.onCompleted();
                }catch (ApiException e){
                    subscriber.onError(e);
                }

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new rx.Observer<List<UserModel>>() {
            @Override
            public void onCompleted() {
                Log.d(TAG , "onCompleted~~");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<UserModel> models) {
                Log.d(TAG , "observer thread = " + Thread.currentThread().getId());
                if (models.isEmpty()){
                    Log.d(TAG , "failed to fetch following list ~~~!!!!!");
                }else {
                    loadFollowing();
                }
            }
        });
    }

    private void updateStatus() {
        Observable.create(new Observable.OnSubscribe<StatusModel>() {
            @Override
            public void call(Subscriber<? super StatusModel> subscriber) {
                if (!NetworkUtils.isNetworkConnected(mContext)){
                    Utils.showToast(mContext , mContext.getString(R.string.network_is_disconnected));
                    return;
                }
                StatusModel model;
                try{
                    if (mPhoto == null){
                        switch (mActionType){
                            case UpdateActivity.TYPE_REPLY:
                                model = api.updateStatus(text , id , "" , "");
                                break;
                            case UpdateActivity.TYPE_RETWEET:
                                model = api.updateStatus(text , "" , id , "");
                                break;
                            default:
                                model = api.updateStatus(text, "" , "" , "");
                                break;
                        }
                    }else {
                        model = api.uploadPhoto(mPhoto , text , "");
                    }
                    Log.d(TAG , "observable thread = " + Thread.currentThread().getId());
                    subscriber.onNext(model);
                    subscriber.onCompleted();
                }catch (ApiException e){
                    subscriber.onError(e);
                }

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new rx.Observer<StatusModel>() {
            @Override
            public void onCompleted() {
                Log.d(TAG , "onCompleted~~");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(StatusModel models) {
                Log.d(TAG , "observer thread = " + Thread.currentThread().getId());
                if(models == null){
                    saveDraft(text);
                    Utils.showToast(mContext , "发送失败，已保存到草稿箱");
                }
            }
        });
    }

    private void populateStatusText(){
        switch (mStatusType){
            case DetailActivity.TYPE_HOME:
                mFanFouDB.getHomeTLStatus(m_Id , this);
                break;
            case DetailActivity.TYPE_MENTIONS:
                mFanFouDB.getNoticeStatus(m_Id , this);
                break;
            case DetailActivity.TYPE_PUBLIC:
                mFanFouDB.getPublicStatus(m_Id , this);
                break;
            case DetailActivity.TYPE_PROFILE:
                mFanFouDB.getProfileStatus(m_Id , this);
                break;
            case DetailActivity.TYPE_FAVORITES:
                mFanFouDB.getFavorite(m_Id , this);
                break;
            default:
                String text = "@放學後茶會 ";
                mView.setStatusText(text);
                mView.setSelection(text);
                break;
        }
    }

    @Override
    public void onStatusLoaded(StatusModel statusModel) {
        mStatusModel = statusModel;
        id = statusModel.getId();
        StringBuilder sb = new StringBuilder();
        switch (mActionType){
            case UpdateActivity.TYPE_REPLY:
                ArrayList<String> names = StatusUtils.getMentions(mStatusModel);
                for (String name : names) {
                    sb.append("@").append(name).append(" ");
                }
                mView.setStatusText(sb.toString());
                mView.setSelection(sb.toString());
                break;
            case UpdateActivity.TYPE_RETWEET:
                sb.append(" 转@").append(mStatusModel.getUserScreenName()).append(" ")
                        .append(mStatusModel.getSimpleText());
                mView.setStatusText(sb.toString());
                break;
        }
    }

    @Override
    public void onUsersNotAvailable() {
        getFollowing();
    }

    private boolean isNewStatus(){
        return m_Id == -1;
    }

    @Override
    public void onResult(Context context , int requestCode, int resultCode, Intent data) {
        Log.d(TAG , "onResult requstCode = " + requestCode + " , resultCode = " + resultCode);
        if (resultCode == Activity.RESULT_OK ){
            switch (requestCode){
                case UpdateFragment.REQUEST_SELECT_PHOTO:
                    mPhotoPath = CompatUtils.getPath(context , data.getData());
                    mPhoto = new File(mPhotoPath);
                    mView.showPhoto(ImageUtils.scalePic(context , mPhotoPath , 90));
                    break;
                case UpdateFragment.REQUEST_TAKE_PHOTO:
                    mPhoto = new File(mPhotoPath);
                    mView.showPhoto(ImageUtils.scalePic(context , mPhotoPath , 90 ));
                    break;
                case UpdateFragment.REQUEST_DRAFT:
                    Draft draft = data.getParcelableExtra(DraftActivity.EXTRA_DRAFT);
                    mView.setStatusText(draft.text);
                    mPhotoPath = draft.fileName;
                    switch (draft.type){
                        case Draft.TYPE_REPLY:
                            mActionType = UpdateActivity.TYPE_REPLY;
                            id = draft.reply;
                            break;
                        case Draft.TYPE_REPOST:
                            mActionType = UpdateActivity.TYPE_RETWEET;
                            id = draft.repost;
                            break;
                        default://避免在转发的时候又打开草稿 总之就是害怕出错啦
                            mActionType = 0;
                            id = null;
                            break;
                    }
                    break;
            }
        }
    }

    @Override
    public void deletePhoto() {
        mView.hidePhoto();
        mPhoto = null;
    }

    @Override
    public void takePhoto(Activity activity , int requestCode){
        this.activity = activity;
        if (isStoragePermissionGranted()){//有权限
            mPhotoPath = Environment.getExternalStorageDirectory() + "/DCIM/Camera/" + Utils.getCurTimeStr() + ".png";
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mPhotoPath)));
            activity.startActivityForResult(intent, requestCode);
        }else {
            requestFlag = requestCode;
            ActivityCompat.requestPermissions(activity , new String[]{STORAGE_PERMISSION}
                    , REQUEST_STORAGE_PERMISSION);
        }
    }

    @Override
    public void selectPhoto(Activity activity, int requestCode) {
        this.activity = activity;
        if (isStoragePermissionGranted()){
            Utils.selectImage(activity , requestCode);
        }else {
            requestFlag = requestCode;
            ActivityCompat.requestPermissions(activity , new String[]{STORAGE_PERMISSION}
                    , REQUEST_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onPermissionRequestResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_PERMISSION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (requestFlag == UpdateFragment.REQUEST_TAKE_PHOTO)
                    takePhoto(activity , UpdateFragment.REQUEST_TAKE_PHOTO);
                else
                    selectPhoto(activity , UpdateFragment.REQUEST_SELECT_PHOTO);
            }
        }
    }

    private boolean isStoragePermissionGranted(){
        return ContextCompat.checkSelfPermission(mContext , STORAGE_PERMISSION)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void saveDraft(String text) {
        Draft draft = new Draft();
        draft.text = text;
        if (mActionType == UpdateActivity.TYPE_REPLY){
            draft.type = Draft.TYPE_REPLY;
            draft.reply = mStatusModel.getId();
        }else if (mActionType == UpdateActivity.TYPE_RETWEET){
            draft.type = Draft.TYPE_REPOST;
            draft.repost = mStatusModel.getId();
        }else {
            draft.type = Draft.TYPE_NONE;
        }
        if (mPhotoPath != null){
            draft.fileName = mPhotoPath;
        }
        mFanFouDB.saveDraft(draft);
    }
}
