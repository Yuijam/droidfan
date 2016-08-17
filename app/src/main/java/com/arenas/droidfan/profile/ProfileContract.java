package com.arenas.droidfan.profile;

import com.arenas.droidfan.BasePresenter;

/**
 * Created by Arenas on 2016/7/22.
 */
interface ProfileContract {

    interface View {
        void showAvatar(String url);
        void showUserId(String userId);
        void showFollowingCount(int count);
        void showFollowerCount(int count);
        void showFavoriteCount(int count);
        void showStatusCount(int count);
        void showTitle(String username);
        void showError(String text);
        void showProgress();
        void hideProgress();
        void showFoButton(int resId);
        void showDescription(String text);
        void showDMView();
    }

    interface Presenter extends BasePresenter {
        void follow();
        void openFollower();
        void openFollowing();
        void refresh();
        void openChatView();
    }
}
