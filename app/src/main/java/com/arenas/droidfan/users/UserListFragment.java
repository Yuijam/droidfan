package com.arenas.droidfan.users;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.arenas.droidfan.R;
import com.arenas.droidfan.adapter.MyOnItemClickListener;
import com.arenas.droidfan.adapter.UsersAdapter;
import com.arenas.droidfan.data.model.UserModel;
import com.arenas.droidfan.profile.ProfileActivity;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserListFragment extends Fragment implements UserContract.View
        , XRecyclerView.LoadingListener{

    private UserContract.Presenter mPresenter;

    private UsersAdapter mAdapter;

    @BindView(R.id.recycler_view)
    XRecyclerView recyclerView;
    @BindView(R.id.progressbar)
    ProgressBar progressBar;
    public UserListFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new UsersAdapter(getContext() , new ArrayList<UserModel>(0));
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
        ButterKnife.bind(this , view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLoadingListener(this);
    }

    @Override
    public void onLoadMore() {
        mPresenter.getMore();
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
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressbar() {
        progressBar.setVisibility(View.GONE);
        recyclerView.loadMoreComplete();
        recyclerView.refreshComplete();
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
}
