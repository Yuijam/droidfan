package com.arenas.droidfan.update;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.BuildConfig;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.CompatUtils;
import com.arenas.droidfan.Util.ImageUtils;
import com.arenas.droidfan.Util.NetworkUtils;
import com.arenas.droidfan.Util.PermissionUtils;
import com.arenas.droidfan.Util.StatusUtils;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.api.Api;
import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.Draft;
import com.arenas.droidfan.data.model.Photo;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.data.model.UserModel;
import com.arenas.droidfan.detail.DetailActivity;
import com.arenas.droidfan.draft.DraftActivity;
import com.arenas.droidfan.service.PostService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Arenas on 2016/7/11.
 */
public class UpdatePresenter implements UpdateContract.Presenter
         , DataSource.LoadUserCallback {

    private static final String TAG = UpdatePresenter.class.getSimpleName();

    private FanFouDB mFanFouDB;
    private final UpdateContract.View mView;

    private File mPhoto;
    private String mPhotoPath;
    private StatusModel mStatusModel;
    private int mActionType;
    private Context mContext;
    private String text;
    private Api api;
    private String id;//reply or retweet id
    private Activity activity;
    private boolean startComplete;

    private int requestFlag;

    public UpdatePresenter(Context context , UpdateContract.View mView , int actionType , StatusModel statusModel) {
        this.mFanFouDB = FanFouDB.getInstance(context);
        this.mView = mView;

        mActionType = actionType;
        mStatusModel = statusModel;
        mContext = context;
        if (statusModel != null)
            id = statusModel.getId();

        api = AppContext.getApi();
        mView.setPresenter(this);
    }

    public UpdatePresenter(Context context , UpdateContract.View mView , int mActionType , String text , String mPhotoPath){
        this.mFanFouDB = FanFouDB.getInstance(context);
        mContext = context;
        this.mView = mView;
        this.text = text;
        this.mPhotoPath = mPhotoPath;
        this.mActionType = mActionType;
        api = AppContext.getApi();
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        if (!startComplete){
            if (!isNewStatus()){
                populateStatusText();
            }
            loadFollowing();//为了获取@的列表
            startComplete = true;
        }
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

    private void getFollowing(){

        if (!NetworkUtils.isNetworkConnected(mContext)){
            Utils.showToast(mContext , mContext.getString(R.string.network_is_disconnected));
            return;
        }

        rx.Observable.create(new rx.Observable.OnSubscribe<List<UserModel>>() {
            @Override
            public void call(Subscriber<? super List<UserModel>> subscriber) {

                try{
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
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG , e.getMessage());
            }

            @Override
            public void onNext(List<UserModel> models) {
                if (models.isEmpty()){
                    Log.d(TAG , "failed to fetch following list ~~~!!!!!");
                }else {
                    loadFollowing();
                }
            }
        });
    }

    private void updateStatus() {
        if (!NetworkUtils.isNetworkConnected(mContext)){
            saveDraft(text);
            Utils.showToast(mContext , mContext.getString(R.string.network_is_disconnected_and_saved_to_draft));
            return;
        }
        PostService.start(mContext , mActionType , id , text , mPhotoPath);
    }

    private void populateStatusText(){
        StringBuilder sb = new StringBuilder();
        switch (mActionType){
            case UpdateActivity.TYPE_REPLY:
                ArrayList<String> names = StatusUtils.getMentions(mStatusModel);
                for (String name : names) {
                    sb.append("@").append(name).append(" ");
                }
                mView.setStatusText(sb.toString());
                mView.setSelection(sb.toString());
                mView.refreshInputStatus();
                break;
            case UpdateActivity.TYPE_RETWEET:
                sb.append(" 转@").append(mStatusModel.getUserScreenName()).append(" ")
                        .append(mStatusModel.getSimpleText());
                mView.setStatusText(sb.toString());
                mView.refreshInputStatus();
                break;
            case UpdateActivity.TYPE_FEEDBACK:
                String feedBackHeader = "@卓小饭 "+ "["+Build.MODEL + " | " + Build.VERSION.RELEASE + "]";
                mView.setStatusText(feedBackHeader);
                mView.setSelection(feedBackHeader);
                mView.refreshInputStatus();
                break;
            case UpdateActivity.TYPE_SHARE:
                mView.setStatusText(text);
                mView.showPhoto(ImageUtils.scalePic(mContext , mPhotoPath , 90 ));
                mView.refreshInputStatus();
                break;
        }
    }

    @Override
    public void onUsersNotAvailable() {
        getFollowing();
    }

    private boolean isNewStatus(){
        return mActionType == -1;
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
                    if (mPhoto != null){
                        Log.d(TAG , "mPhoto != null");
                        mView.showPhoto(ImageUtils.scalePic(context , mPhotoPath , 90 ));
                    }else {
                        Log.d(TAG , "mPhoto == null");
                    }
                    break;
                case UpdateFragment.REQUEST_DRAFT:
                    Draft draft = data.getParcelableExtra(DraftActivity.EXTRA_DRAFT);
                    mView.setStatusText(draft.text);
                    mPhotoPath = draft.fileName;
                    if (mPhotoPath != null){
                        Bitmap bitmap = BitmapFactory.decodeFile(mPhotoPath);
                        mView.showPhoto(bitmap);
                    }
                    mView.refreshInputStatus();//更新字数和发送按钮的状态

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
        if (PermissionUtils.isStoragePermissionGranted(mContext)){//有权限
            mPhotoPath = Environment.getExternalStorageDirectory() + "/DCIM/Camera/" + System.currentTimeMillis() + ".png";
            mPhoto = new File(mPhotoPath);
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                //android 7 权限问题
                Uri contentUri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName()+".provider", mPhoto);
                intent.putExtra(MediaStore.EXTRA_OUTPUT , contentUri);
            }else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhoto));
            }
            activity.startActivityForResult(intent, requestCode);
        }else {
            requestFlag = requestCode;
            PermissionUtils.requestStoragePermission(activity , requestCode);
        }
    }

    @Override
    public void selectPhoto(Activity activity, int requestCode) {
        this.activity = activity;
        if (PermissionUtils.isStoragePermissionGranted(mContext)){
            Utils.selectImage(activity , requestCode);
        }else {
            requestFlag = requestCode;
            PermissionUtils.requestStoragePermission(activity , requestCode);
        }
    }

    @Override
    public void onPermissionRequestResult(int requestCode, String[] permissions, int[] grantResults) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (requestFlag == UpdateFragment.REQUEST_TAKE_PHOTO)
                    takePhoto(activity , UpdateFragment.REQUEST_TAKE_PHOTO);
                else
                    selectPhoto(activity , UpdateFragment.REQUEST_SELECT_PHOTO);
            }
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
