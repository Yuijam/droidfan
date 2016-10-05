package com.arenas.droidfan.photo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.style.ImageSpan;
import android.util.Log;

import com.arenas.droidfan.Util.ImageUtils;
import com.arenas.droidfan.Util.PermissionUtils;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.Photo;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.update.UpdateFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arenas on 2016/7/28.
 */
public class PhotoPresenter implements PhotoContract.Presenter {

    private static final String TAG = PhotoPresenter.class.getSimpleName();

    private static final int REQUEST_STORAGE_PERMISSION = 1;

    private PhotoContract.View mView;
    private Context mContext;
    private List<StatusModel> statusModelList;
    private Activity activity;
    int mPosition;
    private String photoUrl;

    public PhotoPresenter(Context context , PhotoContract.View view , List<StatusModel> statusModels , int position){
        mView = view;
        mContext = context;
        mPosition = position;
        statusModelList = statusModels;

        mView.setPresenter(this);
    }

    @Override
    public void start() {
        mView.showPhoto(statusModelList);
        mView.setCurrentPage(mPosition);
    }

    @Override
    public void savePhoto(final Activity activity , final String photoUrl) {
        this.activity = activity;
        this.photoUrl = photoUrl;
        if (PermissionUtils.isStoragePermissionGranted(mContext)){
            new AsyncTask<String, Void, File>(){
                @Override
                protected File doInBackground(String... strings) {
                    try {
                        return Glide.with(mContext)
                                .load(photoUrl)
                                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                .get(); // needs to be called on background thread
                    }catch (Exception e){
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(File file) {
                    if (file == null)
                        return;
                    File dest = new File(ImageUtils.getPhotoName(photoUrl));
                    if (dest.exists() || ImageUtils.copyFile(file, dest)) {
                        Utils.showToast(mContext , "保存成功~");
                    }
                }
            }.execute();

        }else {
            PermissionUtils.requestStoragePermission(activity , REQUEST_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_PERMISSION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                savePhoto(activity , photoUrl);
            }
        }
    }
}
