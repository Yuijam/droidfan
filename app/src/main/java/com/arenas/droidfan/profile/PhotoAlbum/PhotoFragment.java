package com.arenas.droidfan.profile.photoalbum;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arenas.droidfan.R;
import com.arenas.droidfan.adapter.PhotoAlbumAdapter;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.main.hometimeline.HomeTimelineFragment;
import com.arenas.droidfan.service.FanFouService;

import java.util.ArrayList;
import java.util.List;

public class PhotoFragment extends HomeTimelineFragment {

    private static final String TAG = PhotoFragment.class.getSimpleName();

    private PhotoAlbumAdapter mAdapter;

    public PhotoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        init(view);
        return view;
    }

    @Override
    public void init(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        RecyclerView mRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext() , 3));
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton mFAB = (FloatingActionButton)view.findViewById(R.id.fab);
        mFAB.setOnClickListener(this);
    }

    public void addAction(){
        mIntentFilter.addAction(FanFouService.FILTER_PHOTOTIMELINE);
    }

    @Override
    public void showStatus(List<StatusModel> status) {
        mAdapter.replaceData(status);
    }

    @Override
    public void initAdapter() {
        mAdapter = new PhotoAlbumAdapter(getContext() , new ArrayList<StatusModel>() , null);
    }
}
