package com.arenas.droidfan.profile.photoalbum;


import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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
import com.arenas.droidfan.profile.profilestatus.ProfileStatusFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class PhotoAlbumFragment extends ProfileStatusFragment {

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

    public void init(View view){
        ButterKnife.bind(this , view);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext() , 3));
        recyclerView.setPullRefreshEnabled(false);
        recyclerView.setLoadingListener(this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void showStatus(List<StatusModel> status) {
        mAdapter.replaceData(status);
    }

    MyOnItemClickListener listener = new MyOnItemClickListener() {
        @Override
        public void onItemClick(View view, int position , int i) {
            // TODO: 2016/8/10 诶？居然没有问题？
            Log.d(TAG , "position = " + position);
            PhotoActivity.start(getContext() , 0 , null , mAdapter.getStatus(position).getUserId() , position);
        }

        @Override
        public void onItemLongClick(int id, int position) {

        }
    };
}
