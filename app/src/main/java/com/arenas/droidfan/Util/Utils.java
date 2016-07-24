package com.arenas.droidfan.Util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.Toast;

import com.arenas.droidfan.R;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.data.model.UserModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Arenas on 2016/6/2.
 */
public class Utils {
    private static final Pattern PATTERN_USER = Pattern.compile("(@.+?)\\s+", Pattern.MULTILINE);
    private static Pattern PATTERN_USERLINK = Pattern
            .compile("<a href=\"http://fanfou\\.com/(.*?)\" class=\"former\">(.*?)</a>");

    public static String getCurTimeStr(){
        String curTime;
        SimpleDateFormat Formatter = new SimpleDateFormat("yyyyMMdd_HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        curTime = Formatter.format(curDate);
        return curTime;
    }

    public static void selectImage(Fragment fragment , int requestCode){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        fragment.startActivityForResult(intent , requestCode);
    }
    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment , int frameId){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId,fragment);
        transaction.commit();
    }

    public static void showToast(Context context , String text){
        Toast.makeText(context , text , Toast.LENGTH_SHORT).show();
    }

    public static SpannableStringBuilder handleTextWithColor(String text , Context context){
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        int firstIndex;
        int endIndex;
        if (text.contains("@") && text.contains(" ")){
            firstIndex = text.indexOf("@" );
            endIndex = text.indexOf(" " , firstIndex);
            while ( endIndex < text.length() && endIndex != -1){
                builder.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary))
                        , firstIndex , endIndex , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                String subString = text.substring(endIndex , text.length());
                if (subString.contains("@") && subString.contains(" ")){
                    firstIndex = text.indexOf("@" , endIndex);
                    endIndex = text.indexOf(" " , firstIndex);
                }else {
                    break;
                }
            }
        }
        return builder;
    }

    public static SpannableStringBuilder handleSimpleText(Context context , String simpleText){
        String source = simpleText + " ";
        SpannableStringBuilder builder = new SpannableStringBuilder(source);
        String regex = "@(.+?)\\s+";
        Pattern pattern = PATTERN_USER.compile(regex);
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()){
            builder.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary)) ,
                    matcher.start() , matcher.end() , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    public static List<String> findUser(String htmlText){
        List<String> users = new ArrayList<>();
        final Matcher m = PATTERN_USERLINK.matcher(htmlText);
        while (m.find()) {
            String screenName = Html.fromHtml(m.group(2)).toString();
            users.add(screenName);
        }
        return users;
    }

    public static void highlightUser(Context context , StatusModel statusModel , List<String> key){
        String source = statusModel.getSimpleText();
        for (String s : key){
            for (int i = 0 ; i < source.length() ; i++){
                statusModel.getSimpleText().indexOf(s);
            }
        }
    }
}
