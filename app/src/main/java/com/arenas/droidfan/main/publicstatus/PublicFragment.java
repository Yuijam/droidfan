package com.arenas.droidfan.main.publicstatus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.arenas.droidfan.R;
import com.arenas.droidfan.adapter.MyOnItemClickListener;
import com.arenas.droidfan.adapter.OnStatusImageClickListener;
import com.arenas.droidfan.adapter.StatusAdapter;
import com.arenas.droidfan.data.PublicStatusColumns;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.detail.DetailActivity;
import com.arenas.droidfan.main.hometimeline.HomeTimelineFragment;
import com.arenas.droidfan.photo.PhotoActivity;

import java.util.ArrayList;

import butterknife.BindView;

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

    MyOnItemClickListener Listener = new MyOnItemClickListener() {
        @Override
        public void onItemClick(View view , StatusModel statusModel , int position) {
            DetailActivity.start(PublicFragment.this , statusModel , position);
        }

        @Override
        public void onItemLongClick(int id, int position) {

        }
    };

    @Override
    public void init(View view) {
        super.init(view);
        recyclerView.setLoadingMoreEnabled(false);
    }

    OnStatusImageClickListener imageClickListener = new OnStatusImageClickListener() {
        @Override
        public void onImageClick(StatusModel statusModel) {
            PhotoActivity.start(getContext() ,statusModel);
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
        mAdapter = new StatusAdapter(getContext() , new ArrayList<StatusModel>(0), Listener , imageClickListener);
    }
}
