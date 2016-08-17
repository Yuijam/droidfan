package com.arenas.droidfan.update;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;

import com.arenas.droidfan.BasePresenter;
import com.arenas.droidfan.BaseView;

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
        void finish();
    }

    interface Presenter extends BasePresenter{
        void update(Context context , String text);
        void onResult(Context context , int requestCode, int resultCode, Intent data);
        void deletePhoto();
        void takePhoto(Activity activity , int requestCode);
        void saveDraft(String text);
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }
}
