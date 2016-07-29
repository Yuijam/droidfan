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
        void showError(String text);
        void showFollowState(String text);
        void showProgress();
        void hideProgress();
        void showFoButton();
        void showIsFollowing(String text);
        void showDescription(String text);
    }

    interface Presenter extends BasePresenter {
        void onReceive(Context context , Intent intent);
        void follow();
        void openFollower();
        void openFollowing();
        void openStatus();
        void openFavorites();
        void openPhotoAlbum();
    }
}
