package com.arenas.droidfan.profile.favorite;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arenas.droidfan.R;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.detail.DetailActivity;
import com.arenas.droidfan.main.hometimeline.HomeTimelineFragment;
import com.arenas.droidfan.main.StatusAdapter;
import com.arenas.droidfan.profile.ProfileActivity;
import com.arenas.droidfan.service.FanFouService;

import java.util.ArrayList;

public class FavoriteFragment extends HomeTimelineFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        init(view);
        return view;
    }

    StatusAdapter.OnItemClickListener Listener = new StatusAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view , int position) {
            int _id = mAdapter.getStatus(position).get_id();
            DetailActivity.start(getContext() , DetailActivity.TYPE_FAVORITES , _id);
        }

        @Override
        public void onItemLongClick(int id, int position) {

        }
    };

    @Override
    public void addAction() {
        mIntentFilter.addAction(FILTER_FAVORITES);
    }

    @Override
    public void initAdapter() {
        mAdapter = new StatusAdapter(getContext() , new ArrayList<StatusModel>(0) , Listener);
    }

    @Override
    public void startService(Paging p) {
        FanFouService.getFavoritesList(getContext() , getActivity().getIntent().getStringExtra(ProfileActivity.EXTRA_USER_ID) , p);
    }

}
