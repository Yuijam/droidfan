package com.arenas.droidfan.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.usb.UsbRequest;
import android.media.MediaRouter;
import android.util.Log;

import com.arenas.droidfan.data.FavoritesColumns;
import com.arenas.droidfan.data.NoticeColumns;
import com.arenas.droidfan.data.HomeStatusColumns;
import com.arenas.droidfan.data.ProfileColumns;
import com.arenas.droidfan.data.PublicStatusColumns;
import com.arenas.droidfan.data.StatusColumns;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.data.model.UserColumns;
import com.arenas.droidfan.data.model.UserModel;

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
    public void deleteItem(String tableName, String msgId) {
        db.delete(tableName , "id = ?", new String[]{msgId});
    }

    @Override
    public String getFavoritesSinceId(String owner) {
        return getSinceId(FavoritesColumns.TABLE_NAME , owner);
    }

    @Override
    public void saveFavorites(StatusModel statusModel) {
        saveStatus(FavoritesColumns.TABLE_NAME , statusModel);
    }

    @Override
    public void getFavorite(int _id, GetStatusCallback callback) {
        StatusModel statusModel = getStatus(_id , FavoritesColumns.TABLE_NAME);
        if (statusModel != null){
            callback.onStatusLoaded(statusModel);
        }else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void getFavoritesList(String owner , LoadStatusCallback callback) {
        List<StatusModel> statusModelList = getStatusList(FavoritesColumns.TABLE_NAME , owner);
        if (statusModelList.isEmpty()){
            callback.onDataNotAvailable();
        }else {
            callback.onStatusLoaded(statusModelList);
        }
    }

    @Override
    public void getUser(String id, GetUserCallback callback) {
        UserModel user = null;
        Cursor c = db.rawQuery("select * from " + UserColumns.TABLE_NAME + " where id = ?" , new String[]{id});
        if (c.moveToFirst()){
            user = new UserModel();
            user.setId(DBUtil.parseString(c , UserColumns.ID));
            user.setAccount(DBUtil.parseString(c , UserColumns.ACCOUNT));
            user.setOwner(DBUtil.parseString(c , UserColumns.OWNER));
            user.setType(DBUtil.parseInt(c , UserColumns.TYPE));
            user.setTime(DBUtil.parseLong(c , UserColumns.TIME));
            user.setScreenName(DBUtil.parseString(c , UserColumns.SCREEN_NAME));
            user.setLocation(DBUtil.parseString(c , UserColumns.LOCATION));
            user.setGender(DBUtil.parseString(c , UserColumns.GENDER));

            user.setBirthday(DBUtil.parseString(c , UserColumns.BIRTHDAY));
            user.setDescription(DBUtil.parseString(c , UserColumns.DESCRIPTION));
            user.setProfileImageUrl(DBUtil.parseString(c , UserColumns.PROFILE_IMAGE_URL));
            user.setProfileImageUrlLarge(DBUtil.parseString(c , UserColumns.PROFILE_IMAGE_URL_LARGE));
            user.setUrl(DBUtil.parseString(c , UserColumns.URL));
            user.setStatus(DBUtil.parseString(c , UserColumns.STATUS));
            user.setFollowersCount(DBUtil.parseInt(c , UserColumns.FOLLOWERS_COUNT));
            user.setFriendsCount(DBUtil.parseInt(c , UserColumns.FRIENDS_COUNT));
            user.setFavouritesCount(DBUtil.parseInt(c , UserColumns.FAVORITES_COUNT));
            user.setStatusesCount(DBUtil.parseInt(c , UserColumns.STATUSES_COUNT));
            user.setFollowing(DBUtil.parseInt(c , UserColumns.FOLLOWING));
            user.setProtect(DBUtil.parseInt(c , UserColumns.PROTECTED));
            user.setNotifications(DBUtil.parseInt(c , UserColumns.NOTIFICATIONS));
            user.setVerified(DBUtil.parseInt(c , UserColumns.VERIFIED));
            user.setFollowMe(DBUtil.parseInt(c , UserColumns.FOLLOW_ME));
        }
        c.close();
        if (user != null){
            callback.onUserLoaded(user);
        }else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void saveUser(UserModel user, int type) {
        ContentValues cv = new ContentValues();
        cv.put(UserColumns.ID , user.getId());
        cv.put(UserColumns.ACCOUNT , user.getAccount());
        cv.put(UserColumns.TYPE , user.getType());
        cv.put(UserColumns.OWNER , user.getOwner());
        cv.put(UserColumns.TIME , user.getTime());
        cv.put(UserColumns.STATUS, user.getStatus());
        cv.put(UserColumns.SCREEN_NAME, user.getScreenName());
        cv.put(UserColumns.LOCATION, user.getLocation());
        cv.put(UserColumns.GENDER, user.getGender());
        cv.put(UserColumns.BIRTHDAY , user.getBirthday());
        cv.put(UserColumns.DESCRIPTION , user.getDescription());
        cv.put(UserColumns.PROFILE_IMAGE_URL , user.getProfileImageUrl());
        cv.put(UserColumns.PROFILE_IMAGE_URL_LARGE , user.getProfileImageUrlLarge());
        cv.put(UserColumns.URL , user.getUrl());
        cv.put(UserColumns.FOLLOWERS_COUNT , user.getFollowersCount());
        cv.put(UserColumns.FRIENDS_COUNT , user.getFriendsCount());
        cv.put(UserColumns.FAVORITES_COUNT , user.getFavouritesCount());
        cv.put(UserColumns.STATUSES_COUNT , user.getStatusesCount());
        cv.put(UserColumns.FOLLOWING , user.getFollowing());
        cv.put(UserColumns.PROTECTED , user.getProtect());
        cv.put(UserColumns.NOTIFICATIONS , user.getNotifications());
        cv.put(UserColumns.VERIFIED , user.getVerified());
        cv.put(UserColumns.FOLLOW_ME , user.getFollowMe());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.replace(UserColumns.TABLE_NAME , null , cv);
    }

    @Override
    public String getProfileSinceId(String owner) {
        return getSinceId(ProfileColumns.TABLE_NAME , owner);
    }

    @Override
    public void saveProfileStatus(StatusModel status) {
        saveStatus(ProfileColumns.TABLE_NAME , status);
    }

    @Override
    public void getProfileStatusList(String owner , LoadStatusCallback callback) {
        List<StatusModel> statusList = getStatusList(ProfileColumns.TABLE_NAME , owner);
        if (statusList.isEmpty()){
            callback.onDataNotAvailable();
        }else {
            callback.onStatusLoaded(statusList);
        }
    }

    @Override
    public void getProfileStatus(int _id , GetStatusCallback callback) {
        StatusModel status = getStatus(_id , ProfileColumns.TABLE_NAME);
        if (status != null){
            callback.onStatusLoaded(status);
        }else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void savePublicStatus(StatusModel status) {
        saveStatus(PublicStatusColumns.TABLE_NAME , status);
    }

    @Override
    public void getPublicStatusList(LoadStatusCallback callback) {
        List<StatusModel> statusList = getStatusList(PublicStatusColumns.TABLE_NAME , null);
        if (statusList.isEmpty()){
            callback.onDataNotAvailable();
        }else {
            callback.onStatusLoaded(statusList);
        }
    }

    @Override
    public void getPublicStatus(int _id , GetStatusCallback callback) {
        StatusModel status = getStatus(_id , PublicStatusColumns.TABLE_NAME);
        if (status != null){
            callback.onStatusLoaded(status);
        }else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void saveNoticeStatus(StatusModel status) {
        saveStatus(NoticeColumns.TABLE_NAME , status);
    }

    @Override
    public void getNoticeStatusList(LoadStatusCallback callback) {
        List<StatusModel> statusList = getStatusList(NoticeColumns.TABLE_NAME , null);
        if (statusList.isEmpty()){
            callback.onDataNotAvailable();
        }else {
            callback.onStatusLoaded(statusList);
        }
    }

    @Override
    public void getNoticeStatus(int _id, GetStatusCallback callback) {
        StatusModel statusModel = getStatus(_id , NoticeColumns.TABLE_NAME);
        if (statusModel != null){
            callback.onStatusLoaded(statusModel);
        }else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void updateFavorite(int _id , int favorite) {
        ContentValues values = new ContentValues();
        values.put(HomeStatusColumns.FAVORITED, favorite);
        db.update(HomeStatusColumns.TABLE_NAME, values, "_id = ?", new String[]{String.valueOf(_id)});
    }

    @Override
    public void getHomeTLStatus(int _id , GetStatusCallback callback) {
        StatusModel status = getStatus(_id , HomeStatusColumns.TABLE_NAME);
        if (status != null){
            callback.onStatusLoaded(status);
        }else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public String getMaxId(){
        String maxId = null;
        Cursor c = db.query(HomeStatusColumns.TABLE_NAME , null ,null ,null , null , null ,null);
        if (c != null) {
            c.moveToLast();
            maxId = DBUtil.parseString(c , HomeStatusColumns.ID);
        }
        return maxId;
    }

    @Override
    public String getNoticeSinceId() {
        return getSinceId(NoticeColumns.TABLE_NAME , null);
    }

    @Override
    public String getHomeTLSinceId() {
        return getSinceId(HomeStatusColumns.TABLE_NAME , null);
    }

    private String getSinceId(String tableName , String owner){
        String sinceId = null;
        Cursor c;
        if (owner == null){
             c = db.query(tableName , null ,null ,null , null , null , null);
        }else {
            c = db.rawQuery("select * from " + tableName + " where owner = ?" , new String[]{owner});
        }
        if (c.moveToFirst()){
            Log.d(TAG , "c.moveToFirst != 0 -----");
            sinceId = DBUtil.parseString(c , HomeStatusColumns.ID);
        }
        Log.d(TAG , "sinceId = " + sinceId);
        c.close();
        return sinceId;
    }

    @Override
    public void saveHomeTLStatus(StatusModel status) {
        saveStatus(HomeStatusColumns.TABLE_NAME , status);
    }

    @Override
    public void getHomeTLStatusList(LoadStatusCallback callback) {
        List<StatusModel> statusList = getStatusList(HomeStatusColumns.TABLE_NAME , null);
        if (statusList.isEmpty()){
            callback.onDataNotAvailable();
        }else {
            callback.onStatusLoaded(statusList);
        }
    }

    private void saveStatus(String tableName , StatusModel status){
        ContentValues cv = new ContentValues();

        cv.put(HomeStatusColumns.ID , status.getId());
        cv.put(HomeStatusColumns.ACCOUNT , status.getAccount());
        cv.put(HomeStatusColumns.RAWID , status.getRawId());
        cv.put(HomeStatusColumns.OWNER , status.getOwner());
        cv.put(HomeStatusColumns.TIME , status.getTime());
        cv.put(HomeStatusColumns.TEXT, status.getText());
        cv.put(HomeStatusColumns.SIMPLE_TEXT, status.getSimpleText());
        cv.put(HomeStatusColumns.SOURCE, status.getSource());
        cv.put(HomeStatusColumns.GEO, status.getGeo());
        cv.put(HomeStatusColumns.PHOTO, status.getPhoto());

        cv.put(HomeStatusColumns.USER_ID, status.getUserId());
        cv.put(HomeStatusColumns.USER_SCREEN_NAME, status.getUserScreenName());
        cv.put(HomeStatusColumns.USER_PROFILE_IMAGE_URL, status.getUserProfileImageUrl());

        cv.put(HomeStatusColumns.IN_REPLY_TO_STATUS_ID, status.getInReplyToStatusId());
        cv.put(HomeStatusColumns.IN_REPLY_TO_USER_ID, status.getInReplyToUserId());
        cv.put(HomeStatusColumns.IN_REPLY_TO_SCREEN_NAME, status.getInReplyToScreenName());

        cv.put(HomeStatusColumns.RT_STATUS_ID, status.getRtStatusId());
        cv.put(HomeStatusColumns.RT_USER_ID, status.getRtUserId());
        cv.put(HomeStatusColumns.RT_USER_SCREEN_NAME, status.getRtScreenName());

        cv.put(HomeStatusColumns.PHOTO_IMAGE_URL, status.getPhotoImageUrl());
        cv.put(HomeStatusColumns.PHOTO_THUMB_URL, status.getPhotoThumbUrl());
        cv.put(HomeStatusColumns.PHOTO_LARGE_URL, status.getPhotoLargeUrl());

        cv.put(HomeStatusColumns.TRUNCATED, status.getTruncated());
        cv.put(HomeStatusColumns.FAVORITED, status.getFavorited());
        cv.put(HomeStatusColumns.RETWEETED, status.getRetweeted());
        cv.put(HomeStatusColumns.SELF, status.getSelf());

        cv.put(HomeStatusColumns.READ, status.getRead());
        cv.put(HomeStatusColumns.THREAD, status.getThread());
        cv.put(HomeStatusColumns.SPECIAL, status.getSpecial());

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.replaceOrThrow(tableName , null , cv);
    }

    private StatusModel getStatus(int _id , String tableName){
        StatusModel status = null;
        Cursor c = db.rawQuery("select * from " + tableName + " where _id = " + _id , null);
        if (c != null){
            c.moveToFirst();
            status = new StatusModel();
            status.set_id(DBUtil.parseInt(c , HomeStatusColumns._ID));
            status.setId(DBUtil.parseString(c , HomeStatusColumns.ID));
            status.setAccount(DBUtil.parseString(c , HomeStatusColumns.ACCOUNT));
            status.setOwner(DBUtil.parseString(c , HomeStatusColumns.OWNER));
            status.setRawId(DBUtil.parseLong(c , HomeStatusColumns.RAWID));
            status.setTime(DBUtil.parseLong(c , HomeStatusColumns.TIME));
            status.setText(DBUtil.parseString(c , HomeStatusColumns.TEXT));
            status.setSimpleText(DBUtil.parseString(c , HomeStatusColumns.SIMPLE_TEXT));
            status.setSource(DBUtil.parseString(c , HomeStatusColumns.SOURCE));

            status.setGeo(DBUtil.parseString(c , HomeStatusColumns.GEO));
            status.setPhoto(DBUtil.parseString(c , HomeStatusColumns.PHOTO));
            status.setUserId(DBUtil.parseString(c , HomeStatusColumns.USER_ID));
            status.setUserScreenName(DBUtil.parseString(c , HomeStatusColumns.USER_SCREEN_NAME));
            status.setUserProfileImageUrl(DBUtil.parseString(c , HomeStatusColumns.USER_PROFILE_IMAGE_URL));
            status.setInReplyToStatusId(DBUtil.parseString(c , HomeStatusColumns.IN_REPLY_TO_STATUS_ID));
            status.setInReplyToUserId(DBUtil.parseString(c , HomeStatusColumns.IN_REPLY_TO_USER_ID));
            status.setInReplyToScreenName(DBUtil.parseString(c , HomeStatusColumns.IN_REPLY_TO_SCREEN_NAME));
            status.setRtStatusId(DBUtil.parseString(c , HomeStatusColumns.RT_STATUS_ID));
            status.setRtUserId(DBUtil.parseString(c , HomeStatusColumns.RT_USER_ID));
            status.setRtScreenName(DBUtil.parseString(c , HomeStatusColumns.RT_USER_SCREEN_NAME));
            status.setPhotoImageUrl(DBUtil.parseString(c , HomeStatusColumns.PHOTO_IMAGE_URL));
            status.setPhotoLargeUrl(DBUtil.parseString(c , HomeStatusColumns.PHOTO_LARGE_URL));
            status.setPhotoThumbUrl(DBUtil.parseString(c , HomeStatusColumns.PHOTO_THUMB_URL));
            status.setTruncated(DBUtil.parseInt(c , HomeStatusColumns.TRUNCATED));
            status.setFavorited(DBUtil.parseInt(c , HomeStatusColumns.FAVORITED));
            status.setRetweeted(DBUtil.parseInt(c , HomeStatusColumns.RETWEETED));
            status.setSelf(DBUtil.parseInt(c , HomeStatusColumns.SELF));
            status.setRead(DBUtil.parseInt(c , HomeStatusColumns.READ));
            status.setThread(DBUtil.parseInt(c , HomeStatusColumns.THREAD));
            status.setSpecial(DBUtil.parseInt(c , HomeStatusColumns.SPECIAL));
        }
        if (c != null){
            c.close();
        }
        return status;
    }

    private List<StatusModel> getStatusList(String tableName , String owner){
        List<StatusModel> statusList = new ArrayList<>();
        Cursor c ;
        if (owner == null){
            c = db.query(tableName , null , null , null , null , null , HomeStatusColumns.RAWID + " desc");
        }else {
            c = db.rawQuery("select * from " + tableName + " where owner = ? order by rawid desc" , new String[]{owner});
        }
        if (c.moveToFirst()){
            do {
                StatusModel status = new StatusModel();
                status.set_id(DBUtil.parseInt(c , HomeStatusColumns._ID));
                status.setId(DBUtil.parseString(c , HomeStatusColumns.ID));
                status.setAccount(DBUtil.parseString(c , HomeStatusColumns.ACCOUNT));
                status.setOwner(DBUtil.parseString(c , HomeStatusColumns.OWNER));
                status.setRawId(DBUtil.parseLong(c , HomeStatusColumns.RAWID));
                status.setTime(DBUtil.parseLong(c , HomeStatusColumns.TIME));
                status.setText(DBUtil.parseString(c , HomeStatusColumns.TEXT));
                status.setSimpleText(DBUtil.parseString(c , HomeStatusColumns.SIMPLE_TEXT));
                status.setSource(DBUtil.parseString(c , HomeStatusColumns.SOURCE));

                status.setGeo(DBUtil.parseString(c , HomeStatusColumns.GEO));
                status.setPhoto(DBUtil.parseString(c , HomeStatusColumns.PHOTO));
                status.setUserId(DBUtil.parseString(c , HomeStatusColumns.USER_ID));
                status.setUserScreenName(DBUtil.parseString(c , HomeStatusColumns.USER_SCREEN_NAME));
                status.setUserProfileImageUrl(DBUtil.parseString(c , HomeStatusColumns.USER_PROFILE_IMAGE_URL));
                status.setInReplyToStatusId(DBUtil.parseString(c , HomeStatusColumns.IN_REPLY_TO_STATUS_ID));
                status.setInReplyToUserId(DBUtil.parseString(c , HomeStatusColumns.IN_REPLY_TO_USER_ID));
                status.setInReplyToScreenName(DBUtil.parseString(c , HomeStatusColumns.IN_REPLY_TO_SCREEN_NAME));
                status.setRtStatusId(DBUtil.parseString(c , HomeStatusColumns.RT_STATUS_ID));
                status.setRtUserId(DBUtil.parseString(c , HomeStatusColumns.RT_USER_ID));
                status.setRtScreenName(DBUtil.parseString(c , HomeStatusColumns.RT_USER_SCREEN_NAME));
                status.setPhotoImageUrl(DBUtil.parseString(c , HomeStatusColumns.PHOTO_IMAGE_URL));
                status.setPhotoLargeUrl(DBUtil.parseString(c , HomeStatusColumns.PHOTO_LARGE_URL));
                status.setPhotoThumbUrl(DBUtil.parseString(c , HomeStatusColumns.PHOTO_THUMB_URL));
                status.setTruncated(DBUtil.parseInt(c , HomeStatusColumns.TRUNCATED));
                status.setFavorited(DBUtil.parseInt(c , HomeStatusColumns.FAVORITED));
                status.setRetweeted(DBUtil.parseInt(c , HomeStatusColumns.RETWEETED));
                status.setSelf(DBUtil.parseInt(c , HomeStatusColumns.SELF));
                status.setRead(DBUtil.parseInt(c , HomeStatusColumns.READ));
                status.setThread(DBUtil.parseInt(c , HomeStatusColumns.THREAD));
                status.setSpecial(DBUtil.parseInt(c , HomeStatusColumns.SPECIAL));
                statusList.add(status);
            }while (c.moveToNext());
        }
        c.close();
        return statusList;
    }
}
