package com.arenas.droidfan.data.model;

/**
 * @author mcxiaoke
 * @version 1.1 2012.02.27
 */
public interface DirectMessageColumns {

    String _ID = "_id";

    String RAWID = "rawid";// rawid in number format
    String ACCOUNT = "account"; // related account id/userid
    String OWNER = "owner"; // owner id of the item

    String TYPE = "type"; // type of the item

    String ID = "id"; // id in string format
    String TIME = "time"; // created at of the item
    String TEXT = "text";

    String SENDER_ID = "sender_id";
    String SENDER_SCREEN_NAME = "sender_screen_name";
    String SENDER_PROFILE_IMAGE_URL = "sender_profile_image_url";

    String RECIPIENT_ID = "recipient_id";
    String RECIPIENT_SCREEN_NAME = "recipient_screen_name";
    String RECIPIENT_PROFILE_IMAGE_URL = "recipient_profile_image_url";

    String CONVERSATION_ID = "conversation_id";

    String READ = "read";
    String INCOMING = "incoming";

    String TABLE_NAME = "dm";

    String CREATE_TABLE = "create table "
            + TABLE_NAME + " ( "
            + _ID + " integer primary key autoincrement, "

            + ID + " text not null, "
            + ACCOUNT + " text not null, "
            + OWNER + " text, "

            + TYPE + " integer not null, "

            + RAWID + " integer not null, "
            + TIME + " integer not null, "

            + TEXT + " text not null, "

            + SENDER_ID + " text not null, "
            + SENDER_SCREEN_NAME + " text not null, "
            + SENDER_PROFILE_IMAGE_URL + " text not null, "

            + RECIPIENT_ID + " text not null, "
            + RECIPIENT_SCREEN_NAME + " text not null, "
            + RECIPIENT_PROFILE_IMAGE_URL + " text not null, "

            + CONVERSATION_ID + " text not null, "

            + READ + " boolean not null, "
            + INCOMING + " boolean not null, "

            + "unique ( "
            + ACCOUNT + ","
            + TYPE + ","
            + ID
            + " ) on conflict ignore );";
}
