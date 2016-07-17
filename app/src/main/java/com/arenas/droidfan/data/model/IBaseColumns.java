package com.arenas.droidfan.data.model;

import android.provider.BaseColumns;

public interface IBaseColumns extends BaseColumns {
    public static final int TYPE_NONE = 0;

    public static final String RAWID = "rawid";// rawid in number format
    public static final String ACCOUNT = "account"; // related account id/userid
    public static final String OWNER = "owner"; // owner id of the item
    public static final String NOTE = "note"; // state of the item, reserved

    public static final String TYPE = "type"; // type of the item
    public static final String FLAG = "flag"; // flag of the item, reserved

    public static final String ID = "id"; // id in string format
    public static final String TIME = "time"; // created at of the item

}
