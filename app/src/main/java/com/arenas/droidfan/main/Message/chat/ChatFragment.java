package com.arenas.droidfan.main.message.chat;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.adapter.ChatAdapter;
import com.arenas.droidfan.adapter.MyOnItemClickListener;
import com.arenas.droidfan.data.model.DirectMessageModel;
import com.arenas.droidfan.service.FanFouService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arenas on 2016/7/26.
 */
public class ChatFragment extends Fragment implements ChatContract.View
        , SwipeRefreshLayout.OnRefreshListener , View.OnClickListener , TextWatcher {

    private static final String TAG = ChatFragment.class.getSimpleName();

    private ChatContract.Presenter mPresenter;

    private SwipeRefreshLayout mSwipeRefresh;

    private ChatAdapter mAdapter;

    private IntentFilter mIntentFilter;
    private LocalBroadcastManager mLocalBroadcastManager;
    private LocalReceiver mLocalReceiver;
    private RecyclerView mRecyclerView;

    private EditText mInputText;
    private ImageView mSend;
    private CharSequence temp;

    private LinearLayout mEditLayout;
    private TextView mEditInvalidNotice;

    @Override
    public void setPresenter(Object presenter) {
        mPresenter = (ChatContract.Presenter)presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    MyOnItemClickListener listener = new MyOnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {

        }

        @Override
        public void onItemLongClick(int id, int position) {
            // TODO: 2016/7/26
            Utils.showToast(getContext() , "long click !! ");
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(FanFouService.FILTER_CONVERSATION);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        mLocalReceiver = new LocalReceiver();
        mLocalBroadcastManager.registerReceiver(mLocalReceiver, mIntentFilter);

        mAdapter = new ChatAdapter(getContext() , new ArrayList<DirectMessageModel>() , listener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat , container , false);
        init(view);
        return view;
    }

    private void init(View view){

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefresh = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        mSwipeRefresh.setOnRefreshListener(this);

        mInputText = (EditText)view.findViewById(R.id.input_text);
        mInputText.addTextChangedListener(this);

        mSend = (ImageView)view.findViewById(R.id.send);
        mSend.setOnClickListener(this);

        mEditInvalidNotice = (TextView)view.findViewById(R.id.invalid_notice);
        mEditLayout = (LinearLayout)view.findViewById(R.id.edit_message_layout);

        setHasOptionsMenu(true);
    }

//    @Override
//    public void onFocusChange(View view, boolean b) {
//        if (b){
//            Log.d(TAG , "onFocus~~~");
//        }else {
//            Log.d(TAG , "not onFocus~~~");
//        }
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.send:
                mPresenter.send(mInputText.getText().toString());
                break;
        }
    }

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
    public void showChatItems(List<DirectMessageModel> models) {
        mAdapter.replaceData(models);
    }

    @Override
    public void scrollTo(int position) {
        mRecyclerView.smoothScrollToPosition(position);
    }

    @Override
    public void showError(String text) {
//        Utils.showToast(getContext() , text);
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
    public void onRefresh() {
        mPresenter.refresh();
    }

    //TextWatch
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        temp = charSequence;
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        int rest = 140 - temp.length();
        if (rest < 0 || rest == 140){
            invalidSend();
        }else {
            activateSend();
        }
    }

    private void activateSend(){
        mSend.setImageDrawable(getResources().getDrawable(R.drawable.ic_send_black));
    }
    private void invalidSend(){
        mSend.setImageDrawable(getResources().getDrawable(R.drawable.ic_send_grey));
    }

    @Override
    public void emptyInput() {
        mInputText.setText("");
    }

    @Override
    public void showEditMessageLayout() {
        mEditLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEditInvalidNotice() {
        mEditInvalidNotice.setVisibility(View.VISIBLE);
    }

    @Override
    public void disableSend() {
//        mSend.set
    }

    @Override
    public void enableSend() {

    }
}
