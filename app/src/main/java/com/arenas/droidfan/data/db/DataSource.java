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

    void saveStatus(StatusModel status);

    void getStatusList(LoadStatusCallback callback);

    String getSinceId();

    String getMaxId();

    void getStatus(int _id , GetStatusCallback callback);

    void updateFavorite(int id , int favorite);
}
