package com.arenas.droidfan.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.arenas.droidfan.data.NoticeColumns;
import com.arenas.droidfan.data.StatusColumns;
import com.arenas.droidfan.data.model.UserColumns;

public class FanFouDBHelper extends SQLiteOpenHelper {
    public static final String TAG = FanFouDBHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "DroidFan.db";
    public static final int DATABASE_VERSION = 1;

    public FanFouDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(StatusColumns.CREATE_TABLE);
        db.execSQL(UserColumns.CREATE_TABLE);
        db.execSQL(NoticeColumns.CREATE_TABLE);
//        db.execSQL(DirectMessageColumns.CREATE_TABLE);
//        db.execSQL(StatusUpdateInfoColumns.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}