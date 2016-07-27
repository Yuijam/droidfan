package com.arenas.droidfan.main.publicstatus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.arenas.droidfan.adapter.MyOnItemClickListener;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.R;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.detail.DetailActivity;
import com.arenas.droidfan.main.hometimeline.HomeTimelineFragment;
import com.arenas.droidfan.adapter.StatusAdapter;
import com.arenas.droidfan.service.FanFouService;

import java.util.ArrayList;

/**
 * Created by Arenas on 2016/6/23.
 */
public class PublicFragment extends HomeTimelineFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_public, container , false);
        init(view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void addAction() {
        mIntentFilter.addAction(FILTER_PUBLICTIMELINE);
    }

    MyOnItemClickListener Listener = new MyOnItemClickListener() {
        @Override
        public void onItemClick(View view , int position) {
            int _id = mAdapter.getStatus(position).get_id();
            DetailActivity.start(getContext() , DetailActivity.TYPE_PUBLIC , _id);
        }

        @Override
        public void onItemLongClick(int id, int position) {

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                getActivity().finish();
                break;
        }
        return true;
    }

    @Override
    public void initAdapter() {
        mAdapter = new StatusAdapter(getContext() , new ArrayList<StatusModel>(0), Listener);
    }
}
