package com.arenas.droidfan.users;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.adapter.MyOnItemClickListener;
import com.arenas.droidfan.adapter.UsersAdapter;
import com.arenas.droidfan.data.model.UserModel;
import com.arenas.droidfan.service.FanFouService;

import java.util.ArrayList;
import java.util.List;

public class UserListFragment extends Fragment implements UserContract.View
        , SwipeRefreshLayout.OnRefreshListener{

    private UserContract.Presenter mPresenter;

    protected IntentFilter mIntentFilter;
    private LocalBroadcastManager mLocalBroadcastManager;
    private LocalReceiver mLocalReceiver;

    private UsersAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefresh;

    public UserListFragment() {

    }

    MyOnItemClickListener Listener = new MyOnItemClickListener() {
        @Override
        public void onItemClick(View view , int position) {
            // TODO: 2016/7/24
        }

        @Override
        public void onItemLongClick(int id, int position) {

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(FanFouService.FILTER_USERS);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        mLocalReceiver = new LocalReceiver();
        mLocalBroadcastManager.registerReceiver(mLocalReceiver, mIntentFilter);

        mAdapter = new UsersAdapter(getContext() , new ArrayList<UserModel>(0) , Listener);

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            getActivity().finish();
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        initView(view);
        return view;
    }

    private void initView(View view){
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);

        mSwipeRefresh = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
    }



    @Override
    public void onRefresh() {
        mPresenter.refresh();
    }

    @Override
    public void setPresenter(Object presenter) {
        mPresenter = (UserContract.Presenter)presenter;
    }

    @Override
    public void showProgressbar() {
        mSwipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideProgressbar() {
        mSwipeRefresh.setRefreshing(false);
    }

    @Override
    public void showUsers(List<UserModel> users) {
        mAdapter.replaceData(users);
    }

    @Override
    public void showError(String errorText) {
//        Toast.makeText(getContext() , errorText , Toast.LENGTH_SHORT).show();
//        Utils.showToast(getContext() , errorText);
        //// TODO: 2016/7/25 怎么会有bug？？？
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mPresenter.onReceive(context , intent);
        }
    }
}
