package com.arenas.droidfan.Util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;
import com.arenas.droidfan.myinterface.ListDialogListener;
import com.arenas.droidfan.myinterface.NomalDialogListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Arenas on 2016/6/2.
 */
public class Utils {

    public static String getCurTimeStr(){
        String curTime;
        SimpleDateFormat Formatter = new SimpleDateFormat("yyyyMMdd_HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        curTime = Formatter.format(curDate);
        return curTime;
    }

    public static void selectImage(Activity activity , int requestCode){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent , requestCode);
    }
    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment , int frameId){
        if (!fragment.isAdded()){
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(frameId,fragment);
            transaction.commit();
        }
    }

    public static void showToast(Context context , String text){
        Toast.makeText(context , text , Toast.LENGTH_SHORT).show();
    }

    public static void hideKeyboard(final Context context, final EditText input) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
    }

    public static String getVersionCode(){
        String version = "";
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = AppContext.getContext().getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(AppContext.getContext().getPackageName(),0);
            version = packInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    public static String handleDescription(String text){
        String str;
        Pattern pattern = Pattern.compile("\\n|\\r|\\t");
        Matcher matcher = pattern.matcher(text);
        str = matcher.replaceAll(" ");
        return str;
    }

    public static void createNomalDialog(Context context , String title , String message , String positiveText
            , String negativeText , final NomalDialogListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog alertDialog = builder.setMessage(message)
                .setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onNegativeButtonClick();
                    }
                })
                .setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onPositiveButtonClick();
                    }
                }).create();
        alertDialog.show();
    }

    public static void createListDialog(Context context , String[] items ,  final ListDialogListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.setItems(items , new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(listener != null){
                            listener.onItemClick(which);
                        }
                    }
                }).create();
        dialog.show();
    }
}
