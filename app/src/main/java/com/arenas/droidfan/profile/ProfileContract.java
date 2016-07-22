package com.arenas.droidfan.profile;

import android.content.Context;
import android.content.Intent;

import com.arenas.droidfan.BasePresenter;

/**
 * Created by Arenas on 2016/7/21.
 */
public interface ProfileContract {

    interface View {
        void showAvatar(String url);
        void showUserId(String userId);
        void showFollowingCount(int count);
        void showFollowerCount(int count);
        void showFavoriteCount(int count);
        void showStatusCount(int count);
        void showError();
    }

    interface Presenter extends BasePresenter{
        void onReceive(Context context , Intent intent);
    }
}
