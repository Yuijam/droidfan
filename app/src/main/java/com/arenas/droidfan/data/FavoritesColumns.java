package com.arenas.droidfan.data;

/**
 * Created by Arenas on 2016/7/21.
 */
public class FavoritesColumns extends StatusColumns {
    public static final String TABLE_NAME = "favorites";

    public static final String CREATE_TABLE = "create table "
            + TABLE_NAME + " ( "
            + _ID + " integer primary key autoincrement, "

            + ID + " text not null, "
            + ACCOUNT + " text not null, "
            + OWNER + " text, "

            + RAWID + " integer not null, "
            + TIME + " integer not null, "

            + TEXT + " text not null, "
            + SIMPLE_TEXT + " text not null, "
            + SOURCE + " text not null, "
            + GEO + " text, "
            + PHOTO + " text, "

            + USER_ID + " text not null, "
            + USER_SCREEN_NAME + " text not null, "
            + USER_PROFILE_IMAGE_URL + " text not null, "

            + IN_REPLY_TO_STATUS_ID + " text, "
            + IN_REPLY_TO_USER_ID + " text, "
            + IN_REPLY_TO_SCREEN_NAME + " text, "

            + RT_STATUS_ID + " text, "
            + RT_USER_ID + " text, "
            + RT_USER_SCREEN_NAME + " text, "

            + PHOTO_IMAGE_URL + " text, "
            + PHOTO_THUMB_URL + " text, "
            + PHOTO_LARGE_URL + " text, "

            + TRUNCATED + " integer not null, "
            + FAVORITED + " integer not null, "
            + RETWEETED + " integer not null, "
            + SELF + " integer not null, "

            + READ + " integer not null, "
            + THREAD + " integer not null, "
            + SPECIAL + " integer not null, "

            + "unique ( "
            + ACCOUNT + ","
            + ID
            + " ) on conflict ignore );";
}
