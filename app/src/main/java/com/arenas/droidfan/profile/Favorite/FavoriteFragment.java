package com.arenas.droidfan.profile.favorite;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.detail.DetailActivity;
import com.arenas.droidfan.main.hometimeline.HomeTimelineFragment;
import com.arenas.droidfan.adapter.StatusAdapter;
import com.arenas.droidfan.profile.ProfileActivity;
import com.arenas.droidfan.service.FanFouService;

import java.util.ArrayList;

public class FavoriteFragment extends HomeTimelineFragment {

    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        userId = ((ProfileActivity)getActivity()).getUserId();
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
        FanFouService.getFavoritesList(getContext() , userId , p);
    }

    @Override
    public void showError(String error) {
        if (userId.equals(AppContext.getAccount())){
            Toast.makeText(getContext() , getString(R.string.error_no_status) , Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getContext() , getString(R.string.error_protected) , Toast.LENGTH_SHORT).show();
        }
    }
}
