package com.arenas.droidfan.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.arenas.droidfan.Api.Api;
import com.arenas.droidfan.Api.ApiException;
import com.arenas.droidfan.Api.Paging;
import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.main.HomeTimeline.HomeTimelineFragment;

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



    private FanFouDB mFanFouDB;
    private static final Api mApi = AppContext.getApi();
    private String mFilterAction;

    public FanFouService(){
        super("FanFouService");
    }

    public static void getProfileTimeline(Context context , Paging paging , String userId){
        Intent intent = new Intent(context , FanFouService.class);
        intent.putExtra(EXTRA_REQUEST , PROFILE_TIMELINE);
        intent.putExtra(EXTRA_PAGING , paging);
        intent.putExtra(EXTRA_USER_ID , userId);
        context.startService(intent);
    }

    public static void reply(Context context , String id , String statusText){
        Intent intent = new Intent(context , FanFouService.class);
        intent.putExtra(EXTRA_REQUEST , REPLY);
        intent.putExtra(EXTRA_STATUS_TEXT , statusText);
        intent.putExtra(EXTRA_MSG_ID , id);
        context.startService(intent);
    }

    public static void retweet(Context context , String id , String statusText){
        Intent intent = new Intent(context , FanFouService.class);
        intent.putExtra(EXTRA_REQUEST , RETWEET);
        intent.putExtra(EXTRA_STATUS_TEXT , statusText);
        intent.putExtra(EXTRA_MSG_ID , id);
        context.startService(intent);
    }

    public static void newStatus(Context context , String statusText){
        Intent intent = new Intent(context , FanFouService.class);
        intent.putExtra(EXTRA_REQUEST , UPDATE_STATUS);
        intent.putExtra(EXTRA_STATUS_TEXT , statusText);
        context.startService(intent);
    }

    public static void favorite(Context context , String msgId){
        Intent intent = new Intent(context , FanFouService.class);
        intent.putExtra(EXTRA_REQUEST , FAVORITE);
        intent.putExtra(EXTRA_MSG_ID , msgId);
        context.startService(intent);
    }

    public static void unfavorite(Context context , String msgId){
        Intent intent = new Intent(context , FanFouService.class);
        intent.putExtra(EXTRA_REQUEST , UNFAVORITE);
        intent.putExtra(EXTRA_MSG_ID , msgId);
        context.startService(intent);
    }

    public static void getMentions(Context context , Paging paging){
        start(context , MENTIONS , paging);
    }

    public static void getHomeTimeline(Context context , Paging paging){
        start(context , HOME_TIMELINE , paging);
    }

    public static void getPublicTimeline(Context context ){
        Intent intent = new Intent(context , FanFouService.class);
        intent.putExtra(EXTRA_REQUEST , PUBLIC);
        context.startService(intent);
    }

    private static void start(Context context , int requestCode , Paging paging){
        Intent intent = new Intent(context , FanFouService.class);
        intent.putExtra(EXTRA_REQUEST , requestCode);
        intent.putExtra(EXTRA_PAGING , paging);
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
                    saveHomeTLStatus(mApi.getHomeTimeline(p));
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
                    saveMetions(mApi.getMentions(p));
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
                    saveProfileStatus(mApi.getUserTimeline(userId , p));
                    break;
            }
        }catch (ApiException e){
            e.toString();
        }

    }

    private void saveProfileStatus(List<StatusModel> statusModels){
        for (StatusModel s : statusModels){
            mFanFouDB.saveProfileStatus(s);
        }
    }

    private void savePublicStatus(List<StatusModel> statusModels){
        for (StatusModel s : statusModels){
            mFanFouDB.savePublicStatus(s);
        }
    }

    private void saveMetions(List<StatusModel> statusModels){
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
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sendLocalBroadcast(mFilterAction);
    }
}
