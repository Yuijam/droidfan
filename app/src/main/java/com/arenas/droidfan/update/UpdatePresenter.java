package com.arenas.droidfan.update;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.view.menu.MenuView;
import android.text.TextUtils;
import android.util.Log;

import com.arenas.droidfan.Util.CompatUtils;
import com.arenas.droidfan.Util.ImageUtils;
import com.arenas.droidfan.Util.StatusUtils;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.data.model.StatusUpdateInfo;
import com.arenas.droidfan.detail.DetailContract;
import com.arenas.droidfan.service.FanFouService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;

/**
 * Created by Arenas on 2016/7/11.
 */
public class UpdatePresenter implements UpdateContract.Presenter , DataSource.GetStatusCallback{

    private static final String TAG = UpdatePresenter.class.getSimpleName();

    private FanFouDB mFanFouDB;
    private final UpdateContract.View mView;

    private File mPhoto;
    private String mPhotoPath;
    private int m_Id = -1;
    private StatusModel mStatusModel;
    private int mType;

    public UpdatePresenter(int _id ,int type ,  FanFouDB mFanFouDB, UpdateContract.View mView) {
        this.mFanFouDB = mFanFouDB;
        this.mView = mView;

        m_Id = _id;
        mType = type;

        mView.setPresenter(this);
    }

    @Override
    public void update(Context context , String text ) {
        if (TextUtils.isEmpty(text) || text.length() >140){
            mView.showError();
            return;
        }
        startService(context , text , mPhoto);
    }

    private void startService(Context context , String text , File photo) {
        if (photo == null){
            switch (mType){
                case UpdateActivity.TYPE_REPLY:
                    FanFouService.reply(context , mStatusModel.getId() , text);
                    break;
                case UpdateActivity.TYPE_RETWEET:
                    FanFouService.retweet(context , mStatusModel.getId() , text);
                    break;
                default:
                    FanFouService.newStatus(context , text);
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

    @Override
    public void start() {
        if (!isNewStatus()){
            populateStatusText();
        }
    }

    private void populateStatusText(){
        mFanFouDB.getStatus(m_Id , this);
        StringBuilder sb = new StringBuilder();
        switch (mType){
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
    public void onStatusLoaded(StatusModel statusModel) {
        mStatusModel = statusModel;
    }

    @Override
    public void onDataNotAvailable() {
        mView.showError();
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
}
