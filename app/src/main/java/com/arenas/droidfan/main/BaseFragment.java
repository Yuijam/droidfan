package com.arenas.droidfan.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.R;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.main.hometimeline.HomeTimelineContract;
import com.arenas.droidfan.update.UpdateActivity;

import java.util.List;

/**
 * Created by Arenas on 2016/7/18.
 */
public abstract class BaseFragment extends Fragment implements HomeTimelineContract.View ,
        View.OnClickListener , SwipeRefreshLayout.OnRefreshListener{

    public static final String TAG = BaseFragment.class.getSimpleName();

    public static final String FILTER_HOMETIMELINE = "com.arenas.droidfan.HOMETIMELINE";
    public static final String FILTER_PUBLICTIMELINE = "com.arenas.droidfan.PUBLICTIMELINE";
    public static final String FILTER_PROFILETIMELINE = "com.arenas.droidfan.PROFILETIMELINE";
    public static final String FILTER_FAVORITES = "com.arenas.droidfan.FAVORITES";
    public static final String FILTER_USER = "com.arenas.droidfan.USER";

    private HomeTimelineContract.Presenter mPresenter;

    //broadcast
    protected IntentFilter mIntentFilter;
    private LocalBroadcastManager mLocalBroadcastManager;
    private LocalReceiver mLocalReceiver;

    protected StatusAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void setPresenter(Object presenter) {
        mPresenter = (HomeTimelineContract.Presenter)presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIntentFilter = new IntentFilter();
        addAction();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        mLocalReceiver = new LocalReceiver();
        mLocalBroadcastManager.registerReceiver(mLocalReceiver, mIntentFilter);
        initAdapter();
    }

    public void addAction(){
        mIntentFilter.addAction(FILTER_HOMETIMELINE);
    }

    public abstract void initAdapter();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home , container , false);
        init(view);
        return view;
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
        switch (view.getId()){
            case R.id.fab:
                mPresenter.newStatus();
                break;
        }
    }

    @Override
    public void showUpdateStatusUi() {
        Intent intent = new Intent(getContext() , UpdateActivity.class);
        startActivity(intent);
    }

    @Override
    public abstract void startService(Paging p);

    @Override
    public void showStatus(List<StatusModel> status) {
        mAdapter.replaceData(status);
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mPresenter.onReceive(context , intent);
        }
    }

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
    public void onRefresh() {
        mPresenter.refresh();
    }

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG , "onDestroyView-------->");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG , "onDetach---------->");
    }
}
