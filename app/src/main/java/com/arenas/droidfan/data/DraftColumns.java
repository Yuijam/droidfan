package com.arenas.droidfan.data;

public interface DraftColumns {
    String ID = "id";
    String TYPE = "type";
    String TEXT = "text";
    String REPLY = "reply";
    String REPOST = "repost";
    String FILE = "filename";
    String TABLE_NAME = "draft";

    String CREATE_TABLE = "create table "
            + TABLE_NAME + " ( "
            + ID + " integer primary key autoincrement, "
            + TYPE + " integer not null, "
            + TEXT + " text not null, "
            + REPLY + " text, "
            + REPOST + " text, "
            + FILE + " text, "
            + "unique ( "
            + TEXT + " , " + FILE
            + " ) on conflict ignore );";
}
