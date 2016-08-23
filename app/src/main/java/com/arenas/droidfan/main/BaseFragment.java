package com.arenas.droidfan.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.adapter.StatusAdapter;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.main.hometimeline.HomeTimelineContract;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Arenas on 2016/7/18.
 */
public abstract class BaseFragment extends Fragment implements HomeTimelineContract.View ,
         XRecyclerView.LoadingListener{

    public static final String TAG = BaseFragment.class.getSimpleName();

    public static final String FILTER_HOMETIMELINE = "com.arenas.droidfan.HOMETIMELINE";
    public static final String FILTER_PUBLICTIMELINE = "com.arenas.droidfan.PUBLICTIMELINE";
    public static final String FILTER_PROFILETIMELINE = "com.arenas.droidfan.PROFILETIMELINE";

    protected HomeTimelineContract.Presenter mPresenter;

    protected StatusAdapter mAdapter;

    public @BindView(R.id.recycler_view) XRecyclerView recyclerView;
    public @BindView(R.id.progressbar) ProgressBar progressBar;

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
        initAdapter();
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
        ButterKnife.bind(this , view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        Log.d(TAG , "height = " + recyclerView.getHeight());


//        recyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
//        recyclerView.setLoadingMoreProgressStyle(ProgressStyle.SemiCircleSpin);
//        recyclerView.setArrowImageView(R.drawable.iconfont_downgrey);

        recyclerView.setLoadingListener(this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh() {
        mPresenter.refresh();
    }

    @Override
    public void onLoadMore() {
        mPresenter.getMore();
    }

    @Override
    public void showStatus(List<StatusModel> status) {
        mAdapter.replaceData(status);
    }

    @Override
    public void showError(String error) {
        Utils.showToast(getContext() , error);
    }

    @Override
    public void hideProgressBar() {
        recyclerView.refreshComplete();
        recyclerView.loadMoreComplete();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }
}
