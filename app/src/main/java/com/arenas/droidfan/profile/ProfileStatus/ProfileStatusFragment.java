package com.arenas.droidfan.profile.profilestatus;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arenas.droidfan.R;
import com.arenas.droidfan.adapter.MyOnItemClickListener;
import com.arenas.droidfan.adapter.OnStatusImageClickListener;
import com.arenas.droidfan.adapter.StatusAdapter;
import com.arenas.droidfan.data.ProfileColumns;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.detail.DetailActivity;
import com.arenas.droidfan.main.hometimeline.HomeTimelineFragment;
import com.arenas.droidfan.photo.PhotoActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class ProfileStatusFragment extends HomeTimelineFragment {

    public ProfileStatusFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profilestatus_list, container, false);
        init(view);
        return view;
    }

    @Override
    public void init(View view) {
        ButterKnife.bind(this , view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setPullRefreshEnabled(false);
        recyclerView.setLoadingListener(this);
        recyclerView.setAdapter(mAdapter);
    }

    MyOnItemClickListener Listener = new MyOnItemClickListener() {
        @Override
        public void onItemClick(View view , int _id , int position) {
            DetailActivity.start(getActivity() , DetailActivity.TYPE_PROFILE , _id , position);
        }

        @Override
        public void onItemLongClick(int id, int position) {

        }
    };

    OnStatusImageClickListener imageClickListener = new OnStatusImageClickListener() {
        @Override
        public void onImageClick(int _id) {
            PhotoActivity.start(getContext() , _id , ProfileColumns.TABLE_NAME , null , 0);
        }
    };

    @Override
    public void initAdapter() {
        mAdapter = new StatusAdapter(getContext() , new ArrayList<StatusModel>(0) , Listener , imageClickListener);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        recyclerView.loadMoreComplete();
    }
}
