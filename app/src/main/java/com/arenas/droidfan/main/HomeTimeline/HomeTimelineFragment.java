package com.arenas.droidfan.main.HomeTimeline;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arenas.droidfan.Api.Paging;
import com.arenas.droidfan.R;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.detail.DetailActivity;
import com.arenas.droidfan.main.BaseFragment;
import com.arenas.droidfan.main.StatusAdapter;
import com.arenas.droidfan.service.FanFouService;

import java.util.ArrayList;

/**
 * Created by Arenas on 2016/6/23.
 */
public class HomeTimelineFragment extends BaseFragment{


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home , container , false);
        init(view);
        return view;
    }

    StatusAdapter.OnItemClickListener Listener = new StatusAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view , int position) {
            int _id = mAdapter.getStatus(position).get_id();
            DetailActivity.start(getContext() , DetailActivity.TYPE_HOME , _id);
        }

        @Override
        public void onItemLongClick(int id, int position) {

        }
    };

    @Override
    public void initAdapter() {
        mAdapter = new StatusAdapter(getContext() , new ArrayList<StatusModel>(0) , Listener);
    }

    @Override
    public void startService(Paging p) {
        FanFouService.getHomeTimeline(getContext() , p);
    }
}
