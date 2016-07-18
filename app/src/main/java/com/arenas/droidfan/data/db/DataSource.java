package com.arenas.droidfan.data.db;

import com.arenas.droidfan.data.model.StatusModel;

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

    void saveHomeTLStatus(StatusModel status);

    void saveNoticeStatus(StatusModel status);

    void getHomeTLStatusList(LoadStatusCallback callback);

    void getNoticeStatusList(LoadStatusCallback callback);

    String getSinceId();

    String getMaxId();

    void getHomeTLStatus(int _id , GetStatusCallback callback);

    void getNoticeStatus(int _id , GetStatusCallback callback);

    void updateFavorite(int id , int favorite);
}
