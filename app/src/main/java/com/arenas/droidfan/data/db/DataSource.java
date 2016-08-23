package com.arenas.droidfan.data.db;

import com.arenas.droidfan.data.model.DirectMessageModel;
import com.arenas.droidfan.data.model.Draft;
import com.arenas.droidfan.data.model.StatusModel;
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
        void onUsersNotAvailable();
    }

    interface LoadDMCallback{
        void onDMLoaded(List<DirectMessageModel> messageModels);
        void onDataNotAvailable();
    }

    interface LoadDraftCallback{
        void onDraftLoaded(List<Draft> drafts);
        void onDataNotAvailable();
    }

    void savePublicStatus(StatusModel status);

    void savePublicStatusList(List<StatusModel> statusModels);

    void getPublicStatusList(LoadStatusCallback callback);

    void getPublicStatus(int _id , GetStatusCallback callback);

    void saveHomeTLStatus(StatusModel status);

    void saveHomeTLStatusList(List<StatusModel> modelList);

    void saveNoticeStatus(StatusModel status);

    void saveNoticeStatusList(List<StatusModel> modelList);

    void getHomeTLStatusList(LoadStatusCallback callback);

    void getNoticeStatusList(LoadStatusCallback callback);

    void getHomeTLStatus(int _id , GetStatusCallback callback);

    void getNoticeStatus(int _id , GetStatusCallback callback);

    void updateFavorite(String table , int id , int favorite);

    //profile status
    void saveProfileStatusList(List<StatusModel> statusModels);

    void getProfileStatusList(String owner , LoadStatusCallback callback);

    void getProfileStatus(int  _id , GetStatusCallback callback);

    void saveProfileStatus(StatusModel status);

    //favorites
    void saveFavoritesList(List<StatusModel> statusModels);

    void saveFavorites(StatusModel statusModel);

    void getFavorite(int _id , GetStatusCallback callback);

    void getFavoritesList(String owner , LoadStatusCallback callback);

    void deleteItem(String tableName , String msgId);

    void deleteFavorites(String owner);
    //sinceId && maxId
    String getHomeTLSinceId();

    String getNoticeSinceId();

    String getProfileMaxId(String userId);

    String getHomeMaxId();

    String getNoticeMaxId();

    String getPhotoMaxId(String userId);

    String getFavoritesMaxid(String userId);

    String getProfileSinceId(String owner);

    String getFavoritesSinceId(String owner);

    String getDMSinceId(String username);//指定用户的私信SinceId

    String getDMSinceId(); //整个表的SinceId 用于check新私信

    String getDMMaxId(String username);

    String getPhotoSinceId(String owner);

    //User
    void saveUser(UserModel user , int type );

    void getUserById(String id , GetUserCallback callback);

    void getFollowers(String owner , LoadUserCallback callback);

    void getFollowing(String owner , LoadUserCallback callback);

    void saveFollowers(List<UserModel> users , String owner);

    void saveFollowing(List<UserModel> users , String owner);

    void deleteFollowers(String owner);

    void deleteFollowing(String owner);

    //DM
    void saveDirectMessage(DirectMessageModel dm);

    void saveDirectMessages(List<DirectMessageModel> dms);

    void getConversationList(LoadDMCallback callback);

    void getConversation(String userId , LoadDMCallback callback);

    void deleteDirectMessage(String dmId);

    void saveConversationList(List<DirectMessageModel> dms);


    //photo
    void savePhotoTimeline(List<StatusModel> statusModels);

    void loadPhotoTimeline(String userId , LoadStatusCallback callback);

    void getPhotoStatus(int _id , GetStatusCallback callback);

    //delete
    void deleteAll();

    void deleteHomeTimeline();

    //draft
    void saveDraft(Draft draft);

    void loadDrafts(LoadDraftCallback callback);

    void updateDraft(Draft draft);

    void deleteDraft(int id );
}
