package com.arenas.droidfan.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.api.Api;
import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.Draft;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.update.UpdateActivity;

import java.io.File;

/**
 * Created by Arenas on 2016/8/31.
 */
public class PostService extends IntentService {

    private static final String TAG = PostService.class.getSimpleName();

    public PostService() {
        super("PostService");
    }

    private NotificationManager manager;

    private int actionType;
    private String msgId;
    private String text;
    private String photoPath;
    private Context context;

    private static final String  EXTRA_ACTION_TYPE = "extra_action_type";
    private static final String  EXTRA_ID = "extra_id";
    private static final String  EXTRA_TEXT = "extra_text";
    private static final String  EXTRA_PHOTO_PATH = "extra_photo_path";

    private static final int  SHOW_TOAST = 1;

    public static void start(Context context , int actionType , String msgid , String text , String photoPath){
        Intent intent = new Intent(context , PostService.class);
        intent.putExtra(EXTRA_ID , msgid);
        intent.putExtra(EXTRA_TEXT , text);
        intent.putExtra(EXTRA_PHOTO_PATH , photoPath);
        intent.putExtra(EXTRA_ACTION_TYPE , actionType);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        parseIntent(intent);

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        post();
    }

    private void parseIntent(Intent intent){
        actionType = intent.getIntExtra(EXTRA_ACTION_TYPE , -1);
        msgId = intent.getStringExtra(EXTRA_ID);
        text = intent.getStringExtra(EXTRA_TEXT);
        photoPath = intent.getStringExtra(EXTRA_PHOTO_PATH);
    }

    private void post(){
        showNotification();
        StatusModel model;
        Api api = AppContext.getApi();
        try {
            if (photoPath == null){
                switch (actionType){
                    case UpdateActivity.TYPE_REPLY:
                        model = api.updateStatus(text , msgId , "" , "");
                        break;
                    case UpdateActivity.TYPE_RETWEET:
                        model = api.updateStatus(text , "" , msgId , "");
                        break;
                    default:
                        model = api.updateStatus(text, "" , "" , "");
                        break;
                }
            }else {
                model = api.uploadPhoto(new File(photoPath) , text , "");
            }

            manager.cancel(10);

            if (model == null){
                Log.d(TAG , "model == null 发送失败~");
            }
        }catch (ApiException e){
            Log.d(TAG , e.statusCode + "");
            manager.cancel(10);
            saveDraft();
            showToast();
        }finally {
            manager.cancel(10);
        }

    }

    private void showNotification(){
        int id = 10;
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        RemoteViews remoteViews = new RemoteViews(getPackageName() , R.layout.layout_notification);
        remoteViews.setImageViewResource(R.id.notification_icon , R.drawable.notification_logo);
        remoteViews.setTextViewText(R.id.title , "正在发送");
        builder.setContent(remoteViews);
        builder.setSmallIcon(R.drawable.notification_icon);
        builder.setContentIntent(PendingIntent.getActivity(this, 0,new Intent(), 0));
        builder.setOngoing(true);
        manager.notify(id , builder.build());

    }

    private void showToast(){
        Message message = new Message();
        message.what = SHOW_TOAST;
        handler.sendMessage(message);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SHOW_TOAST:
                    Toast.makeText(context , getString(R.string.failed_post) , Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    public void saveDraft() {
        Draft draft = new Draft();
        draft.text = text;
        if (actionType == UpdateActivity.TYPE_REPLY){
            draft.type = Draft.TYPE_REPLY;
            draft.reply = msgId;
        }else if (actionType == UpdateActivity.TYPE_RETWEET){
            draft.type = Draft.TYPE_REPOST;
            draft.repost = msgId;
        }else {
            draft.type = Draft.TYPE_NONE;
        }
        if (photoPath != null){
            draft.fileName = photoPath;
        }
        FanFouDB.getInstance(this).saveDraft(draft);
    }
}
