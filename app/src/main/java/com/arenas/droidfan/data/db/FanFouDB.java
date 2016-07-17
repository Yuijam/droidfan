package com.arenas.droidfan.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.arenas.droidfan.data.StatusColumns;
import com.arenas.droidfan.data.model.StatusModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arenas on 2016/7/9.
 */
public class FanFouDB implements DataSource{

    private static final String TAG = FanFouDB.class.getSimpleName();
    private static FanFouDB INSTANCE;
    private FanFouDBHelper mDbHelper;
    private SQLiteDatabase db;

    private FanFouDB(Context context){
        mDbHelper = new FanFouDBHelper(context);
        db = mDbHelper.getWritableDatabase();
    }

    public static FanFouDB getInstance(Context context){
        if (INSTANCE == null){
            INSTANCE = new FanFouDB(context);
        }
        return INSTANCE;
    }

    @Override
    public void updateFavorite(int _id , int favorite) {
        ContentValues values = new ContentValues();
        values.put(StatusColumns.FAVORITED, favorite);
        db.update(StatusColumns.TABLE_NAME, values, "_id = ?", new String[]{String.valueOf(_id)});
    }

    @Override
    public void getStatus(int _id , GetStatusCallback callback) {
        StatusModel status = null;
        Cursor c = db.rawQuery("select * from " + StatusColumns.TABLE_NAME + " where _id = " + _id , null);
        if (c != null){
            c.moveToFirst();
            status = new StatusModel();
            status.set_id(DBUtil.parseInt(c , StatusColumns._ID));
            status.setId(DBUtil.parseString(c , StatusColumns.ID));
            status.setAccount(DBUtil.parseString(c , StatusColumns.ACCOUNT));
            status.setOwner(DBUtil.parseString(c , StatusColumns.OWNER));
            status.setRawId(DBUtil.parseLong(c , StatusColumns.RAWID));
            status.setTime(DBUtil.parseLong(c , StatusColumns.TIME));
            status.setText(DBUtil.parseString(c , StatusColumns.TEXT));
            status.setSimpleText(DBUtil.parseString(c , StatusColumns.SIMPLE_TEXT));
            status.setSource(DBUtil.parseString(c , StatusColumns.SOURCE));

            status.setGeo(DBUtil.parseString(c , StatusColumns.GEO));
            status.setPhoto(DBUtil.parseString(c , StatusColumns.PHOTO));
            status.setUserRawid(DBUtil.parseLong(c , StatusColumns.USER_RAWID));
            status.setUserId(DBUtil.parseString(c , StatusColumns.USER_ID));
            status.setUserScreenName(DBUtil.parseString(c , StatusColumns.USER_SCREEN_NAME));
            status.setUserProfileImageUrl(DBUtil.parseString(c , StatusColumns.USER_PROFILE_IMAGE_URL));
            status.setInReplyToStatusId(DBUtil.parseString(c , StatusColumns.IN_REPLY_TO_STATUS_ID));
            status.setInReplyToUserId(DBUtil.parseString(c , StatusColumns.IN_REPLY_TO_USER_ID));
            status.setInReplyToScreenName(DBUtil.parseString(c , StatusColumns.IN_REPLY_TO_SCREEN_NAME));
            status.setRtStatusId(DBUtil.parseString(c , StatusColumns.RT_STATUS_ID));
            status.setRtUserId(DBUtil.parseString(c , StatusColumns.RT_USER_ID));
            status.setRtScreenName(DBUtil.parseString(c , StatusColumns.RT_USER_SCREEN_NAME));
            status.setPhotoImageUrl(DBUtil.parseString(c , StatusColumns.PHOTO_IMAGE_URL));
            status.setPhotoLargeUrl(DBUtil.parseString(c , StatusColumns.PHOTO_LARGE_URL));
            status.setPhotoThumbUrl(DBUtil.parseString(c , StatusColumns.PHOTO_THUMB_URL));
            status.setTruncated(DBUtil.parseInt(c , StatusColumns.TRUNCATED));
            status.setFavorited(DBUtil.parseInt(c , StatusColumns.FAVORITED));
            status.setRetweeted(DBUtil.parseInt(c , StatusColumns.RETWEETED));
            status.setSelf(DBUtil.parseInt(c , StatusColumns.SELF));
            status.setRead(DBUtil.parseInt(c , StatusColumns.READ));
            status.setThread(DBUtil.parseInt(c , StatusColumns.THREAD));
            status.setSpecial(DBUtil.parseInt(c , StatusColumns.SPECIAL));
        }
        if (c != null){
            c.close();
        }
        if (status != null){
            callback.onStatusLoaded(status);
        }else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public String getMaxId(){
        String maxId = null;
        Cursor c = db.query(StatusColumns.TABLE_NAME , null ,null ,null , null , null ,null);
        if (c != null) {
            c.moveToLast();
            maxId = DBUtil.parseString(c , StatusColumns.ID);
        }
        return maxId;
    }

    @Override
    public String getSinceId() {
        String sinceId = null;
        Cursor c = db.query(StatusColumns.TABLE_NAME , null ,null ,null , null , null ,null);
        if (c != null){
            c.moveToFirst();
            sinceId = DBUtil.parseString(c , StatusColumns.ID);
        }
        return sinceId;
    }

    @Override
    public void saveStatus(StatusModel status) {
        ContentValues cv = new ContentValues();

        cv.put(StatusColumns.ID , status.getId());
        cv.put(StatusColumns.ACCOUNT , status.getAccount());
        cv.put(StatusColumns.RAWID , status.getRawId());
        cv.put(StatusColumns.OWNER , status.getOwner());
        cv.put(StatusColumns.TIME , status.getTime());
        cv.put(StatusColumns.TEXT, status.getText());
        cv.put(StatusColumns.SIMPLE_TEXT, status.getSimpleText());
        cv.put(StatusColumns.SOURCE, status.getSource());
        cv.put(StatusColumns.GEO, status.getGeo());
        cv.put(StatusColumns.PHOTO, status.getPhoto());

        cv.put(StatusColumns.USER_RAWID, status.getUserRawid());
        cv.put(StatusColumns.USER_ID, status.getUserId());
        cv.put(StatusColumns.USER_SCREEN_NAME, status.getUserScreenName());
        cv.put(StatusColumns.USER_PROFILE_IMAGE_URL, status.getUserProfileImageUrl());

        cv.put(StatusColumns.IN_REPLY_TO_STATUS_ID, status.getInReplyToStatusId());
        cv.put(StatusColumns.IN_REPLY_TO_USER_ID, status.getInReplyToUserId());
        cv.put(StatusColumns.IN_REPLY_TO_SCREEN_NAME, status.getInReplyToScreenName());

        cv.put(StatusColumns.RT_STATUS_ID, status.getRtStatusId());
        cv.put(StatusColumns.RT_USER_ID, status.getRtUserId());
        cv.put(StatusColumns.RT_USER_SCREEN_NAME, status.getRtScreenName());

        cv.put(StatusColumns.PHOTO_IMAGE_URL, status.getPhotoImageUrl());
        cv.put(StatusColumns.PHOTO_THUMB_URL, status.getPhotoThumbUrl());
        cv.put(StatusColumns.PHOTO_LARGE_URL, status.getPhotoLargeUrl());

        cv.put(StatusColumns.TRUNCATED, status.getTruncated());
        cv.put(StatusColumns.FAVORITED, status.getFavorited());
        cv.put(StatusColumns.RETWEETED, status.getRetweeted());
        cv.put(StatusColumns.SELF, status.getSelf());

        cv.put(StatusColumns.READ, status.getRead());
        cv.put(StatusColumns.THREAD, status.getThread());
        cv.put(StatusColumns.SPECIAL, status.getSpecial());

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.insert(StatusColumns.TABLE_NAME , null , cv);
    }

    @Override
    public void getStatusList(LoadStatusCallback callback) {
        List<StatusModel> statusList = new ArrayList<>();

        Cursor c = db.query(StatusColumns.TABLE_NAME , null , null , null , null , null , StatusColumns.RAWID + " desc");

        if (c.moveToFirst()){
            do {
                StatusModel status = new StatusModel();
                status.set_id(DBUtil.parseInt(c , StatusColumns._ID));
                status.setId(DBUtil.parseString(c , StatusColumns.ID));
                status.setAccount(DBUtil.parseString(c , StatusColumns.ACCOUNT));
                status.setOwner(DBUtil.parseString(c , StatusColumns.OWNER));
                status.setRawId(DBUtil.parseLong(c , StatusColumns.RAWID));
                status.setTime(DBUtil.parseLong(c , StatusColumns.TIME));
                status.setText(DBUtil.parseString(c , StatusColumns.TEXT));
                status.setSimpleText(DBUtil.parseString(c , StatusColumns.SIMPLE_TEXT));
                status.setSource(DBUtil.parseString(c , StatusColumns.SOURCE));

                status.setGeo(DBUtil.parseString(c , StatusColumns.GEO));
                status.setPhoto(DBUtil.parseString(c , StatusColumns.PHOTO));
                status.setUserRawid(DBUtil.parseLong(c , StatusColumns.USER_RAWID));
                status.setUserId(DBUtil.parseString(c , StatusColumns.USER_ID));
                status.setUserScreenName(DBUtil.parseString(c , StatusColumns.USER_SCREEN_NAME));
                status.setUserProfileImageUrl(DBUtil.parseString(c , StatusColumns.USER_PROFILE_IMAGE_URL));
                status.setInReplyToStatusId(DBUtil.parseString(c , StatusColumns.IN_REPLY_TO_STATUS_ID));
                status.setInReplyToUserId(DBUtil.parseString(c , StatusColumns.IN_REPLY_TO_USER_ID));
                status.setInReplyToScreenName(DBUtil.parseString(c , StatusColumns.IN_REPLY_TO_SCREEN_NAME));
                status.setRtStatusId(DBUtil.parseString(c , StatusColumns.RT_STATUS_ID));
                status.setRtUserId(DBUtil.parseString(c , StatusColumns.RT_USER_ID));
                status.setRtScreenName(DBUtil.parseString(c , StatusColumns.RT_USER_SCREEN_NAME));
                status.setPhotoImageUrl(DBUtil.parseString(c , StatusColumns.PHOTO_IMAGE_URL));
                status.setPhotoLargeUrl(DBUtil.parseString(c , StatusColumns.PHOTO_LARGE_URL));
                status.setPhotoThumbUrl(DBUtil.parseString(c , StatusColumns.PHOTO_THUMB_URL));
                status.setTruncated(DBUtil.parseInt(c , StatusColumns.TRUNCATED));
                status.setFavorited(DBUtil.parseInt(c , StatusColumns.FAVORITED));
                status.setRetweeted(DBUtil.parseInt(c , StatusColumns.RETWEETED));
                status.setSelf(DBUtil.parseInt(c , StatusColumns.SELF));
                status.setRead(DBUtil.parseInt(c , StatusColumns.READ));
                status.setThread(DBUtil.parseInt(c , StatusColumns.THREAD));
                status.setSpecial(DBUtil.parseInt(c , StatusColumns.SPECIAL));
                statusList.add(status);
            }while (c.moveToNext());
        }
        if (statusList.isEmpty()){
            callback.onDataNotAvailable();
        }else {
            callback.onStatusLoaded(statusList);
        }
    }
}
