package com.arenas.droidfan.profile.favorite;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.adapter.MyOnItemClickListener;
import com.arenas.droidfan.adapter.OnStatusImageClickListener;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.data.FavoritesColumns;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.detail.DetailActivity;
import com.arenas.droidfan.main.hometimeline.HomeTimelineFragment;
import com.arenas.droidfan.adapter.StatusAdapter;
import com.arenas.droidfan.photo.PhotoActivity;
import com.arenas.droidfan.profile.ProfileActivity;
import com.arenas.droidfan.service.FanFouService;

import java.util.ArrayList;

public class FavoriteFragment extends HomeTimelineFragment {

    public FavoriteFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        init(view);
        return view;
    }

    MyOnItemClickListener Listener = new MyOnItemClickListener() {
        @Override
        public void onItemClick(View view , int position) {
            int _id = mAdapter.getStatus(position).get_id();
            DetailActivity.start(getContext() , DetailActivity.TYPE_FAVORITES , _id);
        }

        @Override
        public void onItemLongClick(int id, int position) {

        }
    };

    OnStatusImageClickListener imageClickListener = new OnStatusImageClickListener() {
        @Override
        public void onImageClick(int _id) {
            PhotoActivity.start(getContext() , _id , FavoritesColumns.TABLE_NAME , null , 0);
        }
    };

    @Override
    public void addAction() {
        mIntentFilter.addAction(FanFouService.FILTER_PROFILE_FAVORITES);
    }

    @Override
    public void initAdapter() {
        mAdapter = new StatusAdapter(getContext() , new ArrayList<StatusModel>(0) , Listener , imageClickListener);
    }

    @Override
    public void showError(String error) {
        Utils.showToast(getContext() , error);
    }
}
