package com.arenas.droidfan.profile.ProfileStatus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.detail.DetailActivity;
import com.arenas.droidfan.main.HomeTimeline.HomeTimelineFragment;
import com.arenas.droidfan.main.StatusAdapter;
import com.arenas.droidfan.service.FanFouService;

import java.util.ArrayList;

public class ProfileStatusFragment extends HomeTimelineFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profilestatus_list, container, false);
        init(view);
        return view;
    }

    StatusAdapter.OnItemClickListener Listener = new StatusAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view , int position) {
            int _id = mAdapter.getStatus(position).get_id();
            DetailActivity.start(getContext() , DetailActivity.TYPE_PROFILE , _id);
        }

        @Override
        public void onItemLongClick(int id, int position) {

        }
    };

    @Override
    public void addAction() {
        mIntentFilter.addAction(FILTER_PROFILETIMELINE);
    }

    @Override
    public void initAdapter() {
        mAdapter = new StatusAdapter(getContext() , new ArrayList<StatusModel>(0) , Listener);
    }

    @Override
    public void startService(Paging p) {
        FanFouService.getProfileTimeline(getContext() , p , AppContext.getAccount());
    }
}
