package com.arenas.droidfan.update;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.arenas.droidfan.BasePresenter;
import com.arenas.droidfan.BaseView;

import java.io.File;
import java.util.List;

/**
 * Created by Arenas on 2016/7/11.
 */
public interface UpdateContract {

    interface View extends BaseView{
        void showPhotoAlbum();
        void showPhoto(Bitmap bitmap);
        void hidePhoto();
        void showError();
        void showHome();
        void setStatusText(String statusText);
        void setSelection(String text);
        void setAutoTextAdapter(String[] users);
    }

    interface Presenter extends BasePresenter{
        void update(Context context , String text);
        void selectPhoto();
        void onResult(Context context , int requestCode, int resultCode, Intent data);
        void deletePhoto();
        void takePhoto(Fragment fragment , int requestCode);
        void onReceive(Context context , Intent intent);
    }
}
