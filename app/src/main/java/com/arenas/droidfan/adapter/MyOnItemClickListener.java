package com.arenas.droidfan.adapter;

import android.view.View;

import com.arenas.droidfan.data.model.StatusModel;

/**
 * Created by Arenas on 2016/7/26.
 */
public interface MyOnItemClickListener {
    void onItemClick(View view , StatusModel statusModel, int position);
    void onItemLongClick(int id , int position);
}
