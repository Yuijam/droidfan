package com.arenas.droidfan.update;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.Util.CompatUtils;
import com.arenas.droidfan.Util.ImageUtils;
import com.arenas.droidfan.Util.StatusUtils;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.data.model.UserModel;
import com.arenas.droidfan.detail.DetailActivity;
import com.arenas.droidfan.main.message.MessageContract;
import com.arenas.droidfan.service.FanFouService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arenas on 2016/7/11.
 */
public class UpdatePresenter implements UpdateContract.Presenter
        , DataSource.GetStatusCallback , DataSource.LoadUserCallback {

    private static final String TAG = UpdatePresenter.class.getSimpleName();

    private FanFouDB mFanFouDB;
    private final UpdateContract.View mView;

    private File mPhoto;
    private String mPhotoPath;
    private int m_Id = -1;
    private StatusModel mStatusModel;
    private int mActionType;
    private int mStatusType;
    private Context mContext;

    public UpdatePresenter(int _id ,int type , int statusType , Context context , UpdateContract.View mView) {
        this.mFanFouDB = FanFouDB.getInstance(context);
        this.mView = mView;

        m_Id = _id;
        mActionType = type;
        mStatusType = statusType;
        mContext = context;

        mView.setPresenter(this);
    }

    @Override
    public void start() {
        if (!isNewStatus()){
            populateStatusText();
        }
        loadFollowing();
    }

    @Override
    public void update(Context context , String text ) {
        if (TextUtils.isEmpty(text) || text.length() >140){
            mView.showError();
            return;
        }
        startService(text , mPhoto);
    }

    private void loadFollowing(){
        mFanFouDB.getFollowing(AppContext.getAccount() , this);
    }

    @Override
    public void onUsersLoaded(List<UserModel> userModelList) {
        String[] users = new String[userModelList.size()];
        int i = 0;
        for (UserModel u : userModelList){
            users[i++] = "@" + u.getScreenName();
        }
        mView.setAutoTextAdapter(users);
    }

    @Override
    public void onDataNotAvailable() {
        mView.showError();
    }

    private void fetchUsers(){
        FanFouService.getFollowing(mContext , AppContext.getAccount());
    }

    private void startService(String text , File photo) {
        if (photo == null){
            switch (mActionType){
                case UpdateActivity.TYPE_REPLY:
                    FanFouService.reply(mContext , mStatusModel.getId() , text);
                    break;
                case UpdateActivity.TYPE_RETWEET:
                    FanFouService.retweet(mContext , mStatusModel.getId() , text);
                    break;
                default:
                    FanFouService.newStatus(mContext , text);
                    break;
            }
            mView.showHome();
        }else {
//            intent.putExtra(FanFouService.EXTRA_REQUEST , FanFouService.UPLOAD_PHOTO);
//            intent.putExtra(FanFouService.EXTRA_STATUS_TEXT , text);
//            intent.putExtra(FanFouService.EXTRA_PHOTO , mPhoto);
//            context.startService(intent);
        }

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
        }
    }

    @Override
    public void onStatusLoaded(StatusModel statusModel) {
        mStatusModel = statusModel;
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
        fetchUsers();
    }

    private boolean isNewStatus(){
        return m_Id == -1;
    }

    @Override
    public void selectPhoto() {
        mView.showPhotoAlbum();
    }

    @Override
    public void onResult(Context context , int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case UpdateFragment.REQUEST_SELECT_PHOTO:
                    String path = CompatUtils.getPath(context , data.getData());
                    mPhoto = new File(path);
                    mView.showPhoto(ImageUtils.scalePic(context , path , 90));
                    break;
                case UpdateFragment.REQUEST_TAKE_PHOTO:
                    mPhoto = new File(mPhotoPath);
                    mView.showPhoto(ImageUtils.scalePic(context , mPhotoPath , 90 ));
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
    public void takePhoto(Fragment fragment , int requestCode){
        mPhotoPath = Environment.getExternalStorageDirectory() + "/DCIM/Camera/" + Utils.getCurTimeStr() + ".png";
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mPhotoPath)));
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        loadFollowing();
    }
}
