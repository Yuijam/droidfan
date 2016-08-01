package com.arenas.droidfan.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.arenas.droidfan.data.FavoritesColumns;
import com.arenas.droidfan.data.NoticeColumns;
import com.arenas.droidfan.data.HomeStatusColumns;
import com.arenas.droidfan.data.PhotoColumns;
import com.arenas.droidfan.data.ProfileColumns;
import com.arenas.droidfan.data.PublicStatusColumns;
import com.arenas.droidfan.data.model.DirectMessageColumns;
import com.arenas.droidfan.data.model.DirectMessageModel;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.data.model.UserColumns;
import com.arenas.droidfan.data.model.UserModel;
import com.arenas.droidfan.users.UserListActivity;

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
    public String getPhotoSinceId(String owner) {
        return getSinceId(PhotoColumns.TABLE_NAME , owner);
    }

    @Override
    public void savePhotoTimeline(List<StatusModel> statusModels) {
        for (StatusModel s : statusModels){
            saveStatus(PhotoColumns.TABLE_NAME , s);
        }
    }

    @Override
    public void loadPhotoTimeline(String userId, LoadStatusCallback callback) {
        Cursor cursor = db.rawQuery("select * from " + PhotoColumns.TABLE_NAME + " where "
                + " user_id = ? order by rawid desc " , new String[]{userId});
        List<StatusModel> photoList = getStatusList(cursor);
        if (photoList.isEmpty()){
            callback.onDataNotAvailable();
        }else {
            callback.onStatusLoaded(photoList);
        }
    }

    @Override
    public void getPhotoStatus(int _id, GetStatusCallback callback) {
        StatusModel photoStatus = getStatus(_id , PhotoColumns.TABLE_NAME);
        if (photoStatus == null){
            callback.onDataNotAvailable();
        }else {
            callback.onStatusLoaded(photoStatus);
        }
    }

    //dm
    @Override
    public String getDMSinceId() {
        Cursor cursor = db.rawQuery("select * from " + DirectMessageColumns.TABLE_NAME + " order by id" , null);
        String sinceId = null;
        if (cursor.moveToLast()){
            sinceId = DBUtil.parseString(cursor , DirectMessageColumns.ID);
        }
        return sinceId;
    }

    @Override
    public void saveConversationList(List<DirectMessageModel> dms) {
        db.execSQL("delete from " + DirectMessageColumns.TABLE_NAME + " where "
        + DirectMessageColumns.TYPE + " = " + DirectMessageModel.TYPE_CONVERSATION_LIST);
        saveDirectMessages(dms);
    }

    @Override
    public void deleteDirectMessage(String dmId) {
        db.delete(DirectMessageColumns.TABLE_NAME , "id = ?" , new String[]{dmId});
    }

    @Override
    public void getConversation(String username, LoadDMCallback callback) {
        Cursor cursor = db.rawQuery("select * from " + DirectMessageColumns.TABLE_NAME + " where "
        + DirectMessageColumns.TYPE + " = ? and " + DirectMessageColumns.CONVERSATION_ID + " = ? "
        + " order by rawid " , new String[]{String.valueOf(DirectMessageModel.TYPE_OUTBOX) , username} );
        List<DirectMessageModel> models = getDM(cursor);
        if (models.isEmpty()){
            callback.onDataNotAvailable();
        }else {
            callback.onDMLoaded(models);
        }
    }

    @Override
    public void getConversationList(LoadDMCallback callback) {
        Cursor c = db.rawQuery("select * from " + DirectMessageColumns.TABLE_NAME + " where "
        + DirectMessageColumns.TYPE + " = " + DirectMessageModel.TYPE_CONVERSATION_LIST , null);
        List<DirectMessageModel> cl = getDM(c);
        if (cl.isEmpty()){
            callback.onDataNotAvailable();
        }else {
            callback.onDMLoaded(cl);
        }
    }

    private List<DirectMessageModel> getDM(Cursor c){
        List<DirectMessageModel> messageList = new ArrayList<>();
        if (c.moveToFirst()){
            do {
                DirectMessageModel model = new DirectMessageModel();
                model.setId(DBUtil.parseString(c , DirectMessageColumns.ID));
                model.setAccount(DBUtil.parseString(c , DirectMessageColumns.ACCOUNT));
                model.setOwner(DBUtil.parseString(c , DirectMessageColumns.OWNER));
                model.setType(DBUtil.parseInt(c , DirectMessageColumns.TYPE));
                model.setRawid(DBUtil.parseLong(c , DirectMessageColumns.RAWID));
                model.setTime(DBUtil.parseLong(c , DirectMessageColumns.TIME));
                model.setText(DBUtil.parseString(c , DirectMessageColumns.TEXT));
                model.setSenderId(DBUtil.parseString(c , DirectMessageColumns.SENDER_ID));
                model.setSenderScreenName(DBUtil.parseString(c , DirectMessageColumns.SENDER_SCREEN_NAME));
                model.setSenderProfileImageUrl(DBUtil.parseString(c , DirectMessageColumns.SENDER_PROFILE_IMAGE_URL));
                model.setRecipientId(DBUtil.parseString(c , DirectMessageColumns.RECIPIENT_ID));
                model.setRecipientScreenName(DBUtil.parseString(c , DirectMessageColumns.RECIPIENT_SCREEN_NAME));
                model.setRecipientProfileImageUrl(DBUtil.parseString(c , DirectMessageColumns.RECIPIENT_PROFILE_IMAGE_URL));
                model.setConversationId(DBUtil.parseString(c , DirectMessageColumns.CONVERSATION_ID));
                model.setRead(DBUtil.parseInt(c , DirectMessageColumns.READ));
                model.setIncoming(DBUtil.parseInt(c , DirectMessageColumns.INCOMING));
                messageList.add(model);
            }while (c.moveToNext());
        }
        c.close();
        return messageList;
    }

    @Override
    public void saveDirectMessages(List<DirectMessageModel> dms) {
        for (DirectMessageModel dm : dms){
            saveDirectMessage(dm);
        }
    }

    @Override
    public void saveDirectMessage(DirectMessageModel dm) {
        ContentValues values = new ContentValues();
        values.put(DirectMessageColumns.ID , dm.getId());
        values.put(DirectMessageColumns.ACCOUNT , dm.getAccount());
        values.put(DirectMessageColumns.OWNER , dm.getOwner());
        values.put(DirectMessageColumns.TYPE , dm.getType());
        values.put(DirectMessageColumns.RAWID , dm.getRawid());
        values.put(DirectMessageColumns.TIME , dm.getTime());
        values.put(DirectMessageColumns.TEXT , dm.getText());
        values.put(DirectMessageColumns.SENDER_ID , dm.getSenderId());
        values.put(DirectMessageColumns.SENDER_SCREEN_NAME , dm.getSenderScreenName());
        values.put(DirectMessageColumns.SENDER_PROFILE_IMAGE_URL , dm.getSenderProfileImageUrl());
        values.put(DirectMessageColumns.RECIPIENT_ID , dm.getRecipientId());
        values.put(DirectMessageColumns.RECIPIENT_SCREEN_NAME , dm.getRecipientScreenName());
        values.put(DirectMessageColumns.RECIPIENT_PROFILE_IMAGE_URL , dm.getRecipientProfileImageUrl());
        values.put(DirectMessageColumns.CONVERSATION_ID , dm.getConversationId());
        values.put(DirectMessageColumns.READ , dm.getRead());
        values.put(DirectMessageColumns.INCOMING , dm.getIncoming());
        db.insert(DirectMessageColumns.TABLE_NAME , null , values);
    }


    //user
    @Override
    public void getFollowing(String owner , LoadUserCallback callback){
        Cursor cursor = db.rawQuery("select * from " + UserColumns.TABLE_NAME + " where " +
                UserColumns.OWNER + " = ? " + " and " + UserColumns.TYPE + " = ? "
                 , new String[]{owner , String.valueOf(UserListActivity.TYPE_FOLLOWING)});

        List<UserModel> users = getUsers(cursor);
        if (users.isEmpty()){
            callback.onUsersNotAvailable();
        }else {
            callback.onUsersLoaded(users);
        }
    }

    @Override
    public void saveFollowers(List<UserModel> users , String owner){
        db.delete(UserColumns.TABLE_NAME , "owner = ? and type = ?",
                new String[]{owner , String.valueOf(UserListActivity.TYPE_FOLLOWERS)});
        for (UserModel u : users){
            saveUser(u , UserListActivity.TYPE_FOLLOWERS);
        }
    }

    @Override
    public void saveFollowing(List<UserModel> users , String owner){
        db.delete(UserColumns.TABLE_NAME , "owner = ? and type = ?",
                new String[]{owner , String.valueOf(UserListActivity.TYPE_FOLLOWING)});
        if (users.isEmpty())
            return;
        for (UserModel u : users){
            saveUser(u , UserListActivity.TYPE_FOLLOWING);
        }
    }

    @Override
    public void getFollowers(String owner , LoadUserCallback callback){

        Cursor cursor = db.rawQuery("select * from " + UserColumns.TABLE_NAME + " where " +
                UserColumns.OWNER + " = ? " + " and " + UserColumns.TYPE + " = ? "
                , new String[]{owner , String.valueOf(UserListActivity.TYPE_FOLLOWERS)});

        List<UserModel> users = getUsers(cursor);
        if (users.isEmpty()){
            callback.onUsersNotAvailable();
        }else {
            callback.onUsersLoaded(users);
        }
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
        Cursor cursor = db.rawQuery("select * from " + FavoritesColumns.TABLE_NAME + " where "
                + " owner = ? order by rawid desc " , new String[]{owner});
        List<StatusModel> statusModelList = getStatusList(cursor);
        if (statusModelList.isEmpty()){
            callback.onDataNotAvailable();
        }else {
            callback.onStatusLoaded(statusModelList);
        }
    }

    @Override
    public void getUserById(String id , GetUserCallback callback) {
        Cursor c = db.rawQuery("select * from " + UserColumns.TABLE_NAME + " where id = ?" , new String[]{id});
        UserModel user = getUser(c);
        if (user != null){
            callback.onUserLoaded(user);
        }else {
            callback.onDataNotAvailable();
        }
    }

    private UserModel getUser(Cursor c ){
        UserModel user = null;
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
        return user;
    }


    private List<UserModel> getUsers(Cursor c){
        List<UserModel> users = new ArrayList<>();
        if (c.moveToFirst()){
            do{
                UserModel user = new UserModel();
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
                users.add(user);
            }while (c.moveToNext());
        }
        c.close();
        return users;
    }

    @Override
    public void saveUser(UserModel user, int type) {
        ContentValues cv = new ContentValues();
        cv.put(UserColumns.ID , user.getId());
        cv.put(UserColumns.ACCOUNT , user.getAccount());
        cv.put(UserColumns.TYPE , type);
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
        Cursor cursor = db.rawQuery("select * from " + ProfileColumns.TABLE_NAME + " where "
                + " owner = ? order by rawid desc " , new String[]{owner});
        List<StatusModel> statusList = getStatusList(cursor);
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
        Cursor cursor = db.rawQuery("select * from " + PublicStatusColumns.TABLE_NAME + " order by rawid desc ",null);
        List<StatusModel> statusList = getStatusList(cursor);
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
        Cursor cursor = db.rawQuery("select * from " + NoticeColumns.TABLE_NAME + " order by rawid desc" , null);
        List<StatusModel> statusList = getStatusList(cursor);
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
    public String getNoticeMaxId() {
        Cursor cursor = db.rawQuery("select * from " + NoticeColumns.TABLE_NAME + " order by rawid " , null);
        return getMaxId(cursor);
    }

    @Override
    public String getPhotoMaxId(String userId) {
        Cursor cursor = db.rawQuery("select * from " + PhotoColumns.TABLE_NAME + " where owner = ? order by rawid " ,
                new String[]{userId});
        return getMaxId(cursor);
    }

    @Override
    public String getFavoritesMaxid(String userId) {
        Cursor cursor = db.rawQuery("select * from " + FavoritesColumns.TABLE_NAME + " where owner = ? order by "
                +" rawid" , new String[]{userId});
        return getMaxId(cursor);
    }

    @Override
    public String getHomeMaxId() {
        Cursor cursor = db.rawQuery("select * from " + HomeStatusColumns.TABLE_NAME + " order by rawid " ,
                null);
        return getMaxId(cursor);
    }

    @Override
    public String getProfileMaxId(String userId){
        Cursor cursor = db.rawQuery("select * from " + ProfileColumns.TABLE_NAME + " where owner = ? order by "
                + " rawid " , new String[]{userId});
        return getMaxId(cursor);
    }

    private String getMaxId(Cursor cursor){
        String maxId = null;
//        Cursor c = db.query(HomeStatusColumns.TABLE_NAME , null ,null ,null , null , null ,null);
        if (cursor.moveToFirst()) {
            maxId = DBUtil.parseString(cursor , HomeStatusColumns.ID);
        }
        return maxId;
    }

    @Override
    public String getNoticeSinceId() {
        Cursor cursor = db.rawQuery("select * from " + NoticeColumns.TABLE_NAME + " order by rawid " , null);
        String sinceId = null;
        if (cursor.moveToLast()){
            sinceId = DBUtil.parseString(cursor , NoticeColumns.ID);
        }
        return sinceId;
    }

    @Override
    public String getHomeTLSinceId() {
        return getSinceId(HomeStatusColumns.TABLE_NAME , null);
    }

    private String getSinceId(String tableName , String owner){
        String sinceId = null;
        Cursor c;
        if (owner == null){
             c = db.rawQuery("select * from " + HomeStatusColumns.TABLE_NAME + " order by rawid desc " , null);
        }else {
            c = db.rawQuery("select * from " + tableName + " where owner = ? order by rawid desc " , new String[]{owner});
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
        Cursor cursor = db.rawQuery("select * from " + HomeStatusColumns.TABLE_NAME + " order by rawid desc" , null);
        List<StatusModel> statusList = getStatusList(cursor);
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

    public StatusModel getStatus(int _id , String tableName){
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

    private List<StatusModel> getStatusList(Cursor c){
        List<StatusModel> statusList = new ArrayList<>();
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
