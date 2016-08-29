package com.arenas.droidfan.main.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.adapter.ConversationListAdapter;
import com.arenas.droidfan.data.model.DirectMessageModel;
import com.arenas.droidfan.main.BaseFragment;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Arenas on 2016/6/23.
 */
public class MessageFragment extends Fragment implements MessageContract.View
        , XRecyclerView.LoadingListener {

    private String TAG = MessageFragment.class.getSimpleName();

    private MessageContract.Presenter mPresenter;
    private ConversationListAdapter mAdapter;

    @BindView(R.id.recycler_view)
    XRecyclerView xRecyclerView;

    @Override
    public void setPresenter(Object presenter) {
        mPresenter = (MessageContract.Presenter)presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ConversationListAdapter(getContext() , new ArrayList<DirectMessageModel>());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message , container , false);
        initView(view);
        return view;
    }

    private void initView(View view){
        ButterKnife.bind(this , view);
        xRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        xRecyclerView.setLoadingListener(this);
        xRecyclerView.setDragRate(BaseFragment.DRAG_RATE);
        xRecyclerView.setLoadingMoreEnabled(false);
        xRecyclerView.setAdapter(mAdapter);
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
    public void showProgressbar() {

    }

    @Override
    public void hideProgressbar() {
        xRecyclerView.refreshComplete();
        xRecyclerView.loadMoreComplete();
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mPresenter.onReceive(context , intent);
        }
    }

    @Override
    public void showError(String text) {
        Utils.showToast(getActivity() , text);
    }

    @Override
    public void showList(List<DirectMessageModel> models) {
        mAdapter.replaceData(models);
    }
}
