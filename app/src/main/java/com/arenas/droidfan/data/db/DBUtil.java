package com.arenas.droidfan.data.db;

import android.database.Cursor;

import com.arenas.droidfan.detail.DetailActivity;

/**
 * Created by Arenas on 2016/7/9.
 */
public final class DBUtil {

    public static String parseString(Cursor c, String columnName) {
        return c.getString(c.getColumnIndexOrThrow(columnName));
    }

    public static long parseLong(Cursor c, String columnName) {
        return c.getLong(c.getColumnIndexOrThrow(columnName));
    }

    public static int parseInt(Cursor c, String columnName) {
        return c.getInt(c.getColumnIndexOrThrow(columnName));
    }

    public static boolean parseBoolean(Cursor c, String columnName) {
        return parseInt(c , columnName) != 0;
    }
}
