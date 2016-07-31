package com.arenas.droidfan.profile.photoalbum;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arenas.droidfan.R;
import com.arenas.droidfan.adapter.MyOnItemClickListener;
import com.arenas.droidfan.adapter.PhotoAlbumAdapter;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.main.hometimeline.HomeTimelineFragment;
import com.arenas.droidfan.photo.PhotoActivity;
import com.arenas.droidfan.profile.ProfileActivity;
import com.arenas.droidfan.service.FanFouService;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PhotoAlbumFragment extends HomeTimelineFragment {

    private static final String TAG = PhotoAlbumFragment.class.getSimpleName();

    private PhotoAlbumAdapter mAdapter;

    public PhotoAlbumFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        init(view);
        return view;
    }

    @Override
    public void initAdapter() {
        mAdapter = new PhotoAlbumAdapter(getContext() , new ArrayList<StatusModel>() , listener);
    }

    public void addAction(){
        mIntentFilter.addAction(FanFouService.FILTER_PHOTOTIMELINE);
    }

    public void init(View view){
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        mRecyclerView = (SuperRecyclerView)view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext() , 3));
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton mFAB = (FloatingActionButton)view.findViewById(R.id.fab);
        mFAB.setOnClickListener(this);
    }

    @Override
    public void showStatus(List<StatusModel> status) {
        mAdapter.replaceData(status);
    }

    MyOnItemClickListener listener = new MyOnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            PhotoActivity.start(getContext() , 0 , null , mAdapter.getStatus(position).getUserId() , position);
        }

        @Override
        public void onItemLongClick(int id, int position) {

        }
    };
}
