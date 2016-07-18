package com.arenas.droidfan.main;

/**
 * Created by Arenas on 2016/7/18.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.arenas.droidfan.Api.Paging;
import com.arenas.droidfan.BasePresenter;
import com.arenas.droidfan.R;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.detail.DetailActivity;
import com.arenas.droidfan.main.HomeTimeline.HomeTimelineContract;
import com.arenas.droidfan.service.FanFouService;
import com.arenas.droidfan.update.UpdateActivity;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Arenas on 2016/6/23.
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener
        , SwipeRefreshLayout.OnRefreshListener , StatusContract.View{

    public static final String FILTER_ACTION = "com.arenas.droidfan.HOMETIMELINE";

    //broadcast
    private IntentFilter mIntentFilter;
    private LocalBroadcastManager mLocalBroadcastManager;
    private LocalReceiver mLocalReceiver;

    private StatusAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public abstract void setPresenter(Object presenter);

    StatusAdapter.OnItemClickListener mListener = new StatusAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            int _id = mAdapter.getStatus(position).get_id();
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_STATUS_ID, _id);
            startActivity(intent);
        }

        @Override
        public void onItemLongClick(int id, int position) {

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(FILTER_ACTION);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        mLocalReceiver = new LocalReceiver();
        mLocalBroadcastManager.registerReceiver(mLocalReceiver, mIntentFilter);

        mAdapter = new StatusAdapter(getContext() , new ArrayList<StatusModel>() , mListener );
    }

    public void init(View view){
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        RecyclerView mRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton mFAB = (FloatingActionButton)view.findViewById(R.id.fab);
        mFAB.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        onClickView(view);
    }

    public abstract void onClickView(View view);

    @Override
    public void showUpdateStatusUi() {
        Intent intent = new Intent(getContext() , UpdateActivity.class);
        startActivity(intent);
    }

    @Override
    public void startService(Paging p) {
        Intent intent = new Intent(getContext() , FanFouService.class);
        intent.putExtra(FanFouService.EXTRA_PAGING , p);
        intent.putExtra(FanFouService.EXTRA_REQUEST , FanFouService.HOME_TIMELINE);
        getContext().startService(intent);
    }

    @Override
    public void showStatus(List<StatusModel> status) {
        mAdapter.replaceData(status);
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            onReceive(context , intent);
        }
    }

    public abstract void onReceive(Context context , Intent intent);

    @Override
    public void showError() {
        Toast.makeText(getContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mLocalReceiver);
    }

    @Override
    public abstract void onRefresh();

    @Override
    public void hideRefreshBar() {
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showRefreshBar() {
        if (!mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }
}

