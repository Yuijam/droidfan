package com.arenas.droidfan.users;

import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arenas.droidfan.R;
import com.arenas.droidfan.update.UpdateContract;

public class UserListFragment extends Fragment implements UserContract.View{

    private UserContract.Presenter mPresenter;

    public UserListFragment() {

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
}
