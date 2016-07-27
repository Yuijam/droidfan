package com.arenas.droidfan.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.arenas.droidfan.data.FavoritesColumns;
import com.arenas.droidfan.data.NoticeColumns;
import com.arenas.droidfan.data.HomeStatusColumns;
import com.arenas.droidfan.data.PhotoColumns;
import com.arenas.droidfan.data.ProfileColumns;
import com.arenas.droidfan.data.PublicStatusColumns;
import com.arenas.droidfan.data.model.DirectMessageColumns;
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
        db.execSQL(HomeStatusColumns.CREATE_TABLE);
        db.execSQL(UserColumns.CREATE_TABLE);
        db.execSQL(NoticeColumns.CREATE_TABLE);
        db.execSQL(PublicStatusColumns.CREATE_TABLE);
        db.execSQL(ProfileColumns.CREATE_TABLE);
        db.execSQL(FavoritesColumns.CREATE_TABLE);
        db.execSQL(DirectMessageColumns.CREATE_TABLE);
        db.execSQL(PhotoColumns.CREATE_TABLE);
//        db.execSQL(StatusUpdateInfoColumns.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}