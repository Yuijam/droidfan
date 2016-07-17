package com.arenas.droidfan.data.model;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Parcelable;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.21
 */
public interface Model extends Parcelable {

    ContentValues values();

    Uri getContentUri();

    String getTable();

}
