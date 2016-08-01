package com.arenas.droidfan.notify;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;
import com.arenas.droidfan.api.Api;
import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.DirectMessageModel;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.main.MainActivity;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Arenas on 2016/8/1.
 */
public class PushService extends IntentService {

    public PushService(){
        super("PushService");
    }

    private SharedPreferences sharedPref;

    private FanFouDB mFanFouDB;
    private Api mApi;
    private Calendar mCalendar;

    @Override
    protected void onHandleIntent(Intent intent) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getBoolean("do_not_notify_at_night" , false)){
            mCalendar = Calendar.getInstance();
            int curHour = mCalendar.get(Calendar.HOUR_OF_DAY);
            if ( curHour > 7 && curHour < 23){
                checkMentions();
                checkMessage();
            }
        }else {
            checkMentions();
            checkMessage();
        }

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        String time = sharedPref.getString("sync_frequency" , "");
        long triggerAtTime = SystemClock.elapsedRealtime() + Integer.parseInt(time)*60*1000;
//        long triggerAtTime = SystemClock.elapsedRealtime() + 60*1000;
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
    }

    private void checkMentions(){
        mFanFouDB = FanFouDB.getInstance(this);
        mApi = AppContext.getApi();

        Paging paging = new Paging();
        paging.sinceId = mFanFouDB.getNoticeSinceId();
        try {
            List<StatusModel> models = mApi.getMentions(paging);
            if (models != null && models.size() > 0 ){
                showNotification("有"+ models.size() + "条消息提到了你" , 1);
                for (StatusModel s : models){
                    mFanFouDB.saveNoticeStatus(s);
                }
            }
        }catch (ApiException e){
            e.printStackTrace();
        }
    }

    private void checkMessage(){
        mFanFouDB = FanFouDB.getInstance(this);
        mApi = AppContext.getApi();

        Paging paging = new Paging();
        paging.sinceId = mFanFouDB.getDMSinceId();
        try {
            List<DirectMessageModel> models = mApi.getDirectMessagesInbox(paging);
            if (models != null && models.size() > 0){
                showNotification("有"+models.size() + "条新私信" , 2);
                mFanFouDB.saveDirectMessages(models);
            }
        }catch (ApiException e){
            e.printStackTrace();
        }
    }

    private void showNotification(String text , int id){
        Intent i = new Intent(this , MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis()).setContentTitle(getString(R.string.app_name))
                .setContentText(text).setContentIntent(pi).build();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        notification.defaults |= Notification.DEFAULT_LIGHTS;
        if (sharedPref.getBoolean("notifications_new_message" , false)){
            notification.sound = Uri.parse(sharedPref.getString("notifications_new_message_ringtone" , ""));
//            notification.defaults |= Notification.DEFAULT_SOUND;
        }
        if(sharedPref.getBoolean("notifications_new_message_vibrate" , false)){
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }

//        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        manager.notify(id, notification);
    }
}
