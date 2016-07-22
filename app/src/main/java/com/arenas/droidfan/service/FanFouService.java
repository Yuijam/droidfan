package com.arenas.droidfan.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.MonthDisplayHelper;

import com.arenas.droidfan.api.Api;
import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.data.HomeStatusColumns;
import com.arenas.droidfan.data.ProfileColumns;
import com.arenas.droidfan.data.StatusColumns;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.data.model.UserModel;
import com.arenas.droidfan.main.hometimeline.HomeTimelineFragment;

import java.io.File;
import java.util.List;

/**
 * Created by Arenas on 2016/7/10.
 */
public class FanFouService extends IntentService {

    private static final String TAG = FanFouService.class.getSimpleName();

    public static final String EXTRA_REQUEST = "extra_fanfou";
    public static final String EXTRA_PAGING = "extra_paging";
    public static final String EXTRA_STATUS_TEXT = "extra_status_text";
    public static final String EXTRA_PHOTO = "extra_photo";
    public static final String EXTRA_MSG_ID = "extra_msg_id";
    public static final String EXTRA_USER_ID = "extra_user_id";

    public static final String EXTRA_HAS_NEW = "extra_has_new";

    public static final int HOME_TIMELINE = 1;
    public static final int UPDATE_STATUS = 2;
    public static final int UPLOAD_PHOTO = 3;
    public static final int REPLY = 4;
    public static final int RETWEET = 5;
    public static final int FAVORITE = 6;
    public static final int UNFAVORITE = 7;
    public static final int MENTIONS = 8;
    public static final int PUBLIC = 9;
    public static final int PROFILE_TIMELINE = 10;
    public static final int USER = 11;
    public static final int FAVORITES_LIST = 12;
    public static final int DELETE = 13;


    private FanFouDB mFanFouDB;
    private static final Api mApi = AppContext.getApi();
    private String mFilterAction;

    private boolean mHasNewData;

    public FanFouService(){
        super("FanFouService");
    }

    public static void delete(Context context , String msgId){
        start(context , DELETE , null , msgId , null , null);
    }

    public static void getFavoritesList(Context context , String userId , Paging paging){
        start(context , FAVORITES_LIST , paging , null , userId , null);
    }

    public static void getUser(Context context , String userId ){
        start(context , USER , null , null , userId , null);
    }

    public static void getProfileTimeline(Context context , Paging paging , String userId){
        start(context , PROFILE_TIMELINE , paging , null , userId , null);
    }

    public static void reply(Context context , String msgId , String statusText){
        start(context , REPLY , null , msgId , null , statusText);
    }

    public static void retweet(Context context , String msgId , String statusText){
        start(context , RETWEET , null , msgId , null , statusText);
    }

    public static void newStatus(Context context , String statusText){
        start(context , UPDATE_STATUS , null , null , null , statusText);
    }

    public static void favorite(Context context , String msgId){
        start(context , FAVORITE , null , msgId , null , null);
    }

    public static void unfavorite(Context context , String msgId){
        start(context , UNFAVORITE , null , msgId , null , null);
    }

    public static void getMentions(Context context , Paging paging){
        start(context , MENTIONS , paging , null , null , null);
    }

    public static void getHomeTimeline(Context context , Paging paging){
        start(context , HOME_TIMELINE , paging , null , null , null);
    }

    public static void getPublicTimeline(Context context ){
        start(context , PUBLIC , null , null , null , null);
    }

    private static void start(Context context , int requestCode , Paging paging , String msgId , String userId ,String statusText){
        Intent intent = new Intent(context , FanFouService.class);
        intent.putExtra(EXTRA_REQUEST , requestCode);
        intent.putExtra(EXTRA_PAGING , paging);
        intent.putExtra(EXTRA_MSG_ID , msgId);
        intent.putExtra(EXTRA_USER_ID , userId);
        intent.putExtra(EXTRA_STATUS_TEXT , statusText);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int request = intent.getIntExtra(EXTRA_REQUEST , 0);
        String mStatusText;
        String mId;
        Paging p;
        String userId;
        mFanFouDB = FanFouDB.getInstance(this);
        try {
            switch (request){
                case HOME_TIMELINE:
                    p = intent.getParcelableExtra(EXTRA_PAGING);
                    if (mApi.getHomeTimeline(p).size() == 0){
                        mHasNewData = false;
                    }else {
                        saveHomeTLStatus(mApi.getHomeTimeline(p));
                        mHasNewData = true;
                    }
                    mFilterAction = HomeTimelineFragment.FILTER_HOMETIMELINE;
                    break;
                case UPDATE_STATUS:
                    mStatusText = intent.getStringExtra(EXTRA_STATUS_TEXT);
                    mApi.updateStatus(mStatusText , "" , "" , "");
                    Log.d(TAG , "UPDATE STATUS -------- > ");
                    break;
                case UPLOAD_PHOTO:
                    File photo = (File)intent.getSerializableExtra(EXTRA_PHOTO);
                    mStatusText = intent.getStringExtra(EXTRA_STATUS_TEXT);
                    mApi.uploadPhoto(photo , mStatusText , "");
                    break;
                case REPLY:
                    mStatusText = intent.getStringExtra(EXTRA_STATUS_TEXT);
                    mId = intent.getStringExtra(EXTRA_MSG_ID);
                    mApi.updateStatus(mStatusText , mId , "" , "");
                    Log.d(TAG , "REPLY STATUS -------- > ");
                    break;
                case RETWEET:
                    mStatusText = intent.getStringExtra(EXTRA_STATUS_TEXT);
                    mId = intent.getStringExtra(EXTRA_MSG_ID);
                    mApi.updateStatus(mStatusText , "" , mId , "");
                    Log.d(TAG , "RETWEET STATUS -------- > ");
                    break;
                case FAVORITE:
                    mId = intent.getStringExtra(EXTRA_MSG_ID);
                    mApi.favorite(mId);
                    break;
                case UNFAVORITE:
                    mId = intent.getStringExtra(EXTRA_MSG_ID);
                    mApi.unfavorite(mId);
                    break;
                case MENTIONS:
                    p = intent.getParcelableExtra(EXTRA_PAGING);
                    if (mApi.getMentions(p).size() == 0){
                        mHasNewData = false;
                    }else {
                        saveMentions(mApi.getMentions(p));
                        mHasNewData = true;
                    }
                    mFilterAction = HomeTimelineFragment.FILTER_HOMETIMELINE;
                    Log.d(TAG , "getMetions--------->");
                    break;
                case PUBLIC:
                    savePublicStatus(mApi.getPublicTimeline());
                    mFilterAction = HomeTimelineFragment.FILTER_PUBLICTIMELINE;
                    Log.d(TAG , "getPublicStatus------->");
                    break;
                case PROFILE_TIMELINE:
                    userId = intent.getStringExtra(EXTRA_USER_ID);
                    p = intent.getParcelableExtra(EXTRA_PAGING);
                    mFilterAction = HomeTimelineFragment.FILTER_PROFILETIMELINE;
                    if (mApi.getUserTimeline(userId , p).size() == 0 ){
                        mHasNewData = false;
                    }else {
                        saveProfileStatus(mApi.getUserTimeline(userId , p));
                        mHasNewData = true;
                    }
                    break;
                case USER:
                    Log.d(TAG , "getUser------->");
                    userId = intent.getStringExtra(EXTRA_USER_ID);
                    mFilterAction = HomeTimelineFragment.FILTER_USER;
                    saveUser(mApi.showUser(userId));
                    break;
                case FAVORITES_LIST:
                    Log.d(TAG , "get favorites list-------->");
                    userId = intent.getStringExtra(EXTRA_USER_ID);
                    p = intent.getParcelableExtra(EXTRA_PAGING);
                    mFilterAction = HomeTimelineFragment.FILTER_FAVORITES;
                    if (mApi.getFavorites(userId , p).size() == 0){
                        mHasNewData = false;
                    }else {
                        saveFavoritesList(mApi.getFavorites(userId , p));
                        mHasNewData = true;
                    }
                    break;
                case DELETE:
                    mId = intent.getStringExtra(EXTRA_MSG_ID);
                    mFanFouDB.deleteItem(HomeStatusColumns.TABLE_NAME , mId);
                    mFanFouDB.deleteItem(ProfileColumns.TABLE_NAME , mId);
                    mApi.deleteStatus(mId);
                    break;
            }
        }catch (ApiException e){
            e.toString();
        }

    }

    private void saveFavoritesList(List<StatusModel> statusModels){
        for (StatusModel s : statusModels){
            mFanFouDB.saveFavorites(s);
        }
    }

    private void saveUser(UserModel user){
        mFanFouDB.saveUser(user , 0);
    }

    private void saveProfileStatus(List<StatusModel> statusModels){
        Log.d(TAG , "saveProfileStatus--------->");
        for (StatusModel s : statusModels){
            mFanFouDB.saveProfileStatus(s);
        }
    }

    private void savePublicStatus(List<StatusModel> statusModels){
        for (StatusModel s : statusModels){
            mFanFouDB.savePublicStatus(s);
        }
    }

    private void saveMentions(List<StatusModel> statusModels){
        for (StatusModel s : statusModels){
            mFanFouDB.saveNoticeStatus(s);
        }
    }

    private void saveHomeTLStatus(List<StatusModel> statusModels){
        for (StatusModel s : statusModels){
            mFanFouDB.saveHomeTLStatus(s);
        }
    }

    private void sendLocalBroadcast(String filterAction){
        Intent intent = new Intent(filterAction);
        Log.d(TAG , "filterAction = " + filterAction);

        intent.putExtra(EXTRA_HAS_NEW , mHasNewData);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sendLocalBroadcast(mFilterAction);
    }
}
