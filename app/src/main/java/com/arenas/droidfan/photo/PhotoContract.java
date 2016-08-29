package com.arenas.droidfan.photo;

import android.app.Activity;

import com.arenas.droidfan.BasePresenter;
import com.arenas.droidfan.BaseView;
import com.arenas.droidfan.data.model.StatusModel;

import java.util.List;

/**
 * Created by Arenas on 2016/7/28.
 */
public interface PhotoContract {

    interface View extends BaseView{
        void showPhoto(List<StatusModel> models);
        void showError(String text);
        void setCurrentPage(int position);
    }

    interface Presenter extends BasePresenter{
        void savePhoto(Activity activity , String url);
        void onRequestResult(int requestCode,String[] permissions, int[] grantResults);
    }
}
