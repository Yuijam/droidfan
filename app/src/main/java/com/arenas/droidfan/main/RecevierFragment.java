package com.arenas.droidfan.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by Arenas on 2016/7/24.
 */
public abstract class RecevierFragment extends Fragment {
    public static final String FILTER_HOMETIMELINE = "com.arenas.droidfan.HOMETIMELINE";
    public static final String FILTER_PUBLICTIMELINE = "com.arenas.droidfan.PUBLICTIMELINE";
    public static final String FILTER_PROFILETIMELINE = "com.arenas.droidfan.PROFILETIMELINE";
    public static final String FILTER_FAVORITES = "com.arenas.droidfan.FAVORITES";
    public static final String FILTER_USER = "com.arenas.droidfan.USER";

    protected IntentFilter mIntentFilter;
    private LocalBroadcastManager mLocalBroadcastManager;
    private LocalReceiver mLocalReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIntentFilter = new IntentFilter();
        addAction();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        mLocalReceiver = new LocalReceiver();
        mLocalBroadcastManager.registerReceiver(mLocalReceiver, mIntentFilter);
    }

    public void addAction(){
        mIntentFilter.addAction(FILTER_HOMETIMELINE);
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            onReceive(context , intent);
        }
    }

    abstract void onReceive(Context context , Intent intent);
}
