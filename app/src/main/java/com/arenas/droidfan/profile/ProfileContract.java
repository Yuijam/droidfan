package com.arenas.droidfan.profile;

import android.content.Context;
import android.content.Intent;

import com.arenas.droidfan.BasePresenter;

/**
 * Created by Arenas on 2016/7/22.
 */
public class ProfileContract {

    interface View {
        void showAvatar(String url);
        void showUserId(String userId);
        void showFollowingCount(int count);
        void showFollowerCount(int count);
        void showFavoriteCount(int count);
        void showStatusCount(int count);
        void showTitle(String username);
        void showError();
        void showLocation(String text);
        void showBirthday(String text);
        void showFollowMe(String text);
        void showProgress();
        void hideProgress();
        void showFoButton();
        void showIsFollowing(String text);
    }

    interface Presenter extends BasePresenter {
        void onReceive(Context context , Intent intent);
        void follow();
        void showFollower();
        void showFollowing();
    }
}
