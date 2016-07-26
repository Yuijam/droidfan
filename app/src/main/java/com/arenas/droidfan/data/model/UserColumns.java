package com.arenas.droidfan.data.model;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.16
 */
public interface  UserColumns {
    String ACCOUNT = "account"; // related account id/userid
    String OWNER = "owner"; // owner id of the item
    String _ID = "_id"; // id in string format
    String ID = "id"; //

    String TYPE = "type";
    String TIME = "time"; // created at of the item
    String STATUS = "status";
    String SCREEN_NAME = "screen_name";

    String LOCATION = "location";
    String GENDER = "gender";
    String BIRTHDAY = "birthday";
    String DESCRIPTION = "description";

    String PROFILE_IMAGE_URL = "profile_image_url";
    String PROFILE_IMAGE_URL_LARGE = "profile_image_url_large";
    String URL = "url";


    String FOLLOWERS_COUNT = "followers_count";
    String FRIENDS_COUNT = "friends_count";
    String FAVORITES_COUNT = "favourites_count";
    String STATUSES_COUNT = "statuses_count";

    String FOLLOWING = "following";
    String PROTECTED = "protected";
    String NOTIFICATIONS = "notifications";
    String VERIFIED = "verified";
    String FOLLOW_ME = "follow_me";


    String TABLE_NAME = "user";
    String CREATE_TABLE = "create table "
            + TABLE_NAME + " ( "
            + _ID + " integer primary key autoincrement, "

            + ID + " text not null, "
            + ACCOUNT + " text not null, "
            + OWNER + " text, "
            + TYPE + " integer, "
            + TIME + " integer not null, "

            + SCREEN_NAME + " text not null, "
            + LOCATION + " text, "
            + GENDER + " text, "
            + BIRTHDAY + " text, "
            + DESCRIPTION + " text, "

            + PROFILE_IMAGE_URL + " text not null, "
            + PROFILE_IMAGE_URL_LARGE + " text not null, "
            + URL + " text, "
            + STATUS + " text, "

            + FOLLOWERS_COUNT + " integer not null, "
            + FRIENDS_COUNT + " integer not null, "
            + FAVORITES_COUNT + " integer not null, "
            + STATUSES_COUNT + " integer not null, "

            + FOLLOWING + " integer not null, "
            + PROTECTED + " integer not null, "
            + NOTIFICATIONS + " integer not null, "
            + VERIFIED + " integer not null, "
            + FOLLOW_ME + " integer not null, "

            + "unique ( "
            + ACCOUNT + ","
            + TYPE + ","
            + ID
            + " ) on conflict ignore );";
}
