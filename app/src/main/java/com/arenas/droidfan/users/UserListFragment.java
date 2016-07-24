package com.arenas.droidfan.users;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.view.menu.MenuView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.adapter.StatusAdapter;
import com.arenas.droidfan.adapter.UsersAdapter;
import com.arenas.droidfan.api.rest.UsersMethods;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.data.model.UserModel;
import com.arenas.droidfan.detail.DetailActivity;
import com.arenas.droidfan.service.FanFouService;

import java.util.ArrayList;
import java.util.List;

public class UserListFragment extends Fragment implements UserContract.View{

    private UserContract.Presenter mPresenter;

    protected IntentFilter mIntentFilter;
    private LocalBroadcastManager mLocalBroadcastManager;
    private LocalReceiver mLocalReceiver;

    private UsersAdapter mAdapter;

    public UserListFragment() {

    }

    UsersAdapter.OnItemClickListener Listener = new UsersAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view , int position) {
            // TODO: 2016/7/24
//            int _id = mAdapter.getUser(position).get_id();
//            DetailActivity.start(getContext() , DetailActivity.TYPE_HOME , _id);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_list, container, false);
    }

    @Override
    public void setPresenter(Object presenter) {
        mPresenter = (UserContract.Presenter)presenter;
    }

    @Override
    public void showTitle(String title) {

    }

    @Override
    public void showProgressbar() {

    }

    @Override
    public void hideProgressbar() {

    }

    @Override
    public void showUsers(List<UserModel> users) {

    }

    @Override
    public void showError(String errorText) {
        Utils.showToast(getContext() , errorText);
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mPresenter.onReceive(context , intent);
        }
    }
}
