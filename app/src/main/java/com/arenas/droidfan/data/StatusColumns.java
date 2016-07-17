package com.arenas.droidfan.data;

public final class StatusColumns {
    public static String TEXT = "text";
    public static String SIMPLE_TEXT = "simple_text";
    public static String SOURCE = "source";
    public static String RAWID = "rawid";// rawid in number format
    public static String ACCOUNT = "account"; // related account id/userid
    public static String OWNER = "owner"; // owner id of the item

    public static String _ID = "_id"; //
    public static String ID = "id"; //

    public static String TIME = "time"; // created at of the item
    public static final String GEO = "geo";

    public static final String USER_RAWID = "user_rawid";
    public static final String USER_ID = "user_id";
    public static final String USER_SCREEN_NAME = "user_screen_name";
    public static final String USER_PROFILE_IMAGE_URL = "user_profile_image_url";

    public static final String IN_REPLY_TO_STATUS_ID = "in_reply_to_status_id";
    public static final String IN_REPLY_TO_USER_ID = "in_reply_to_user_id";
    public static final String IN_REPLY_TO_SCREEN_NAME = "in_reply_to_screen_name";

    public static final String RT_STATUS_ID = "rt_status_id";
    public static final String RT_USER_ID = "rt_user_id";
    public static final String RT_USER_SCREEN_NAME = "rt_user_screen_name";

    public static final String PHOTO_IMAGE_URL = "imageurl";
    public static final String PHOTO_THUMB_URL = "thumburl";
    public static final String PHOTO_LARGE_URL = "largeurl";

    public static final String TRUNCATED = "truncated";
    public static final String FAVORITED = "favorited";
    public static final String RETWEETED = "retweeted";
    public static final String SELF = "self";

    public static final String READ = "read";
    public static final String THREAD = "thread";
    public static final String PHOTO = "photo";
    public static final String SPECIAL = "special";
    public static final String TABLE_NAME = "status";

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

            + USER_RAWID + " integer not null, "
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
