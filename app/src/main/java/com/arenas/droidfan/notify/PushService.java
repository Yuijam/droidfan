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
import android.util.Log;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.NetworkUtils;
import com.arenas.droidfan.api.Api;
import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.DirectMessageModel;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.main.MainActivity;
import com.arenas.droidfan.main.TabFragmentAdapter;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Arenas on 2016/8/1.
 */
public class PushService extends IntentService {

    private static final String TAG = PushService.class.getSimpleName();

    public PushService(){
        super("PushService");
    }

    private static SharedPreferences sharedPref;

    private FanFouDB mFanFouDB;
    private Api mApi;
    private Calendar mCalendar;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG , "onCreate>>>");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (!NetworkUtils.isNetworkConnected(this))
            return;

        if (sharedPref.getBoolean("do_not_notify_at_night" , true)){
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
    }


    public static boolean isServiceAlarmOn(Context context){
        Intent i = new Intent(context , PushService.class);
        PendingIntent pi = PendingIntent.getService(context , 0 , i , PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    public static void setServiceAlarm(Context context){
        Log.d(TAG , "setServiceAlarm----------->");
        Intent i = new Intent(context , PushService.class);
        PendingIntent pi = PendingIntent.getService(context , 0 , i , 0);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        AlarmManager manager = (AlarmManager)context.getSystemService(ALARM_SERVICE);

//        String time = PreferenceManager.getDefaultSharedPreferences(context).getString("sync_frequency" , "5");
//        Log.d(TAG , "time = " + time);
        int repeatTime = 5 * 60 * 1000;
        manager.setRepeating(AlarmManager.RTC , System.currentTimeMillis() , repeatTime , pi);
    }

    public static void cancelPushService(Context context){
        Intent i = new Intent(context , PushService.class);
        PendingIntent pi = PendingIntent.getService(context , 0 , i , 0);

        if (pi != null){
            AlarmManager manager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
            Log.d(TAG , "cancel alarm~");
            manager.cancel(pi);
            pi.cancel();
        }else {
            Log.d(TAG , "pi == null , so how to cancel ?");
        }
    }

    public static boolean shouldStartAlarm(Context context){
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isNotifyOn = sharedPref.getBoolean("notification" , true);
        return  !isServiceAlarmOn(context) && isNotifyOn;
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
        }catch (Exception e){
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showNotification(String text , int id){
        Intent i = new Intent(this , MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(this).setSmallIcon(R.drawable.notification_icon)
                .setWhen(System.currentTimeMillis()).setContentTitle(getString(R.string.app_name))
                .setContentText(text).setContentIntent(pi).build();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        notification.defaults |= Notification.DEFAULT_LIGHTS;
        if (sharedPref.getBoolean("notifications_new_message" , true)){
            notification.sound = Uri.parse(sharedPref.getString("notifications_new_message_ringtone" , "content://settings/system/notification_sound"));
//            notification.defaults |= Notification.DEFAULT_SOUND;
        }
        if(sharedPref.getBoolean("notifications_new_message_vibrate" , true)){
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }

//        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        manager.notify(id, notification);
    }
}
