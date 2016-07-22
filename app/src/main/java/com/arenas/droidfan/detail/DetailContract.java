package com.arenas.droidfan.detail;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.arenas.droidfan.BasePresenter;
import com.arenas.droidfan.BaseView;

/**
 * Created by Arenas on 2016/7/15.
 */
public interface DetailContract {

    interface View extends BaseView{
        void showUsername(String username);
        void showUserId(String userId);
        void showAvatar(String url);
        void showStatusText(String status);
        void showDate(String date);
        void showSource(String source);
        void showPhoto(String url);
        void showError();
        void showFavorite(int resId);
        void showDelete();
    }

    interface Presenter extends BasePresenter{
        void reply(Context context);
        void retweet(Context context);
        void favorite(Context context);
        void delete(Context context);
    }
}
