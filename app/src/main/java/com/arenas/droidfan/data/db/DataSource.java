package com.arenas.droidfan.data.db;

import com.arenas.droidfan.data.model.DirectMessageModel;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.data.model.UserColumns;
import com.arenas.droidfan.data.model.UserModel;

import java.util.List;

/**
 * Created by Arenas on 2016/7/9.
 */
public interface DataSource {

    interface LoadStatusCallback{
        void onStatusLoaded(List<StatusModel> status);
        void onDataNotAvailable();
    }

    interface GetStatusCallback{
        void onStatusLoaded(StatusModel statusModel);
        void onDataNotAvailable();
    }

    interface GetUserCallback{
        void onUserLoaded(UserModel userModel);
        void onDataNotAvailable();
    }

    interface LoadUserCallback{
        void onUsersLoaded(List<UserModel> userModelList);
        void onDataNotAvailable();
    }

    interface LoadDMCallback{
        void onDMLoaded(List<DirectMessageModel> messageModels);
        void onDataNotAvailable();
    }

    void savePublicStatus(StatusModel status);

    void getPublicStatusList(LoadStatusCallback callback);

    void getPublicStatus(int _id , GetStatusCallback callback);

    void saveHomeTLStatus(StatusModel status);

    void saveNoticeStatus(StatusModel status);

    void getHomeTLStatusList(LoadStatusCallback callback);

    void getNoticeStatusList(LoadStatusCallback callback);

    String getHomeTLSinceId();

    String getNoticeSinceId();

    String getMaxId();

    void getHomeTLStatus(int _id , GetStatusCallback callback);

    void getNoticeStatus(int _id , GetStatusCallback callback);

    void updateFavorite(int id , int favorite);

    void getProfileStatusList(String owner , LoadStatusCallback callback);

    void getProfileStatus(int  _id , GetStatusCallback callback);

    void saveProfileStatus(StatusModel status);

    String getProfileSinceId(String owner);

    void saveFavorites(StatusModel statusModel);

    void getFavorite(int _id , GetStatusCallback callback);

    void getFavoritesList(String owner , LoadStatusCallback callback);

    String getFavoritesSinceId(String owner);

    void deleteItem(String tableName , String msgId);


    //User
    void saveUser(UserModel user , int type );

    void getUserById(String id , GetUserCallback callback);

    void getFollowers(String owner , LoadUserCallback callback);

    void getFollowing(String owner , LoadUserCallback callback);

    void saveFollowers(List<UserModel> users , String owner);

    void saveFollowing(List<UserModel> users , String owner);

    //DM
    void saveDirectMessage(DirectMessageModel dm);

    void saveDirectMessages(List<DirectMessageModel> dms);

    void getConversationList(LoadDMCallback callback);
}
