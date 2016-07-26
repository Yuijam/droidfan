package com.arenas.droidfan.main.message;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.adapter.ChatAdapter;
import com.arenas.droidfan.adapter.ConversationListAdapter;
import com.arenas.droidfan.api.Paging;
import com.arenas.droidfan.data.model.DirectMessageModel;
import com.arenas.droidfan.main.message.chat.ChatActivity;
import com.arenas.droidfan.service.FanFouService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arenas on 2016/6/23.
 */
public class MessageFragment extends Fragment implements MessageContract.View
        , SwipeRefreshLayout.OnRefreshListener{

    private String TAG = MessageFragment.class.getSimpleName();

    private IntentFilter mIntentFilter;
    private LocalBroadcastManager mLocalBroadcastManager;
    private LocalReceiver mLocalReceiver;

    private MessageContract.Presenter mPresenter;
    private ConversationListAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefresh;

    @Override
    public void setPresenter(Object presenter) {
        mPresenter = (MessageContract.Presenter)presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    ConversationListAdapter.OnItemClickListener listener = new ConversationListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            String conversationId = mAdapter.getDM(position).getConversationId();
            ChatActivity.start(getContext() , conversationId);
        }

        @Override
        public void onItemLongClick(int id, int position) {

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(FanFouService.FILTER_CONVERSATION_LIST);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        mLocalReceiver = new LocalReceiver();
        mLocalBroadcastManager.registerReceiver(mLocalReceiver, mIntentFilter);

        mAdapter = new ConversationListAdapter(getContext() , new ArrayList<DirectMessageModel>() , listener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message , container , false);
        initView(view);
        return view;
    }

    private void initView(View view){
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);

        mSwipeRefresh = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setOnRefreshListener(this);


    }

    @Override
    public void onRefresh() {
        mPresenter.refresh();
    }

    @Override
    public void showProgressbar() {
        mSwipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideProgressbar() {
        mSwipeRefresh.setRefreshing(false);
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mPresenter.onReceive(context , intent);
        }
    }

    @Override
    public void showError(String text) {
        Utils.showToast(getContext() , text);
    }

    @Override
    public void showList(List<DirectMessageModel> models) {
        mAdapter.replaceData(models);
    }
}
