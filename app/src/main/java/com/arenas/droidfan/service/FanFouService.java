package com.arenas.droidfan.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
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

    public static final int HOME_TIMELINE = 1;
    public static final int UPDATE_STATUS = 2;
    public static final int UPLOAD_PHOTO = 3;
    public static final int REPLY = 4;
    public static final int RETWEET = 5;
    public static final int FAVORITE = 6;
    public static final int UNFAVORITE = 7;


    private FanFouDB mFanFouDB;
    private static final Api mApi = AppContext.getApi();

    private String mStatusText;
    private String mId;

    public FanFouService(){
        super("FanFouService");
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

    @Override
    protected void onHandleIntent(Intent intent) {
        int request = intent.getIntExtra(EXTRA_REQUEST , 0);

        mFanFouDB = FanFouDB.getInstance(this);
        try {
            switch (request){
                case HOME_TIMELINE:
                    Paging p = intent.getParcelableExtra(EXTRA_PAGING);
                    saveStatus(mApi.getHomeTimeline(p));
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
            }
        }catch (ApiException e){
            e.toString();
        }

    }

    private void saveStatus(List<StatusModel> statusModels){
        for (StatusModel s : statusModels){
            mFanFouDB.saveHomeTLStatus(s);
        }
    }

    private void sendLocalBroadcast(){
        Intent intent = new Intent(HomeTimelineFragment.FILTER_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sendLocalBroadcast();
    }
}
