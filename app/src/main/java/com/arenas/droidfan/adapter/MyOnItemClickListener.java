package com.arenas.droidfan.adapter;

import android.view.View;

/**
 * Created by Arenas on 2016/7/26.
 */
public interface MyOnItemClickListener {
    void onItemClick(View view , int _id , int position);
    void onItemLongClick(int id , int position);
}
