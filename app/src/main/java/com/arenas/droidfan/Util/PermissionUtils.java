package com.arenas.droidfan.Util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Arenas on 2016/8/26.
 */
public class PermissionUtils {

    public static final String STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

    public static boolean isStoragePermissionGranted(Context context){
        return isPermissionGranted(context , STORAGE_PERMISSION);
    }

    public static boolean isPermissionGranted(Context context , String permission){
        return ContextCompat.checkSelfPermission(context , permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestStoragePermission(Activity activity , int requestCode){
        requestPermission(activity , STORAGE_PERMISSION , requestCode);
    }

    public static void requestPermission(Activity activity , String permission , int requestCode ){
        ActivityCompat.requestPermissions(activity , new String[]{permission}
                , requestCode);
    }
}
