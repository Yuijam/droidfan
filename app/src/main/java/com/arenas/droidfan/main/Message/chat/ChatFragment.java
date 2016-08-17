package com.arenas.droidfan.main.message.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arenas.droidfan.R;
import com.arenas.droidfan.adapter.ChatAdapter;
import com.arenas.droidfan.adapter.MyOnItemClickListener;
import com.arenas.droidfan.data.model.DirectMessageModel;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Arenas on 2016/7/26.
 */
public class ChatFragment extends Fragment implements ChatContract.View
        , View.OnClickListener , TextWatcher , XRecyclerView.LoadingListener {

    private static final String TAG = ChatFragment.class.getSimpleName();

    private ChatContract.Presenter mPresenter;

    private ChatAdapter mAdapter;

    @BindView(R.id.recycler_view) XRecyclerView xRecyclerView;
    @BindView(R.id.input_text) EditText inputText;
    @BindView(R.id.send) Button send;
    @BindView(R.id.invalid_notice) TextView editInvalidNotice;
    @BindView(R.id.edit_message_layout) LinearLayout editLayout;
    @BindView(R.id.progressbar) ProgressBar progressBar;

    private CharSequence temp;

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
        public void onItemClick(View view, int position , int i) {

        }

        @Override
        public void onItemLongClick(int id, int position) {
            // TODO: 2016/7/26
//            Utils.showToast(getContext() , "long click !! ");
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        ButterKnife.bind(this , view);

        xRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        xRecyclerView.setLoadingListener(this);
        xRecyclerView.setAdapter(mAdapter);

        inputText.addTextChangedListener(this);

        send.setOnClickListener(this);

        setHasOptionsMenu(true);
    }

    @Override
    public void onRefresh() {
        mPresenter.refresh();
    }

    @Override
    public void onLoadMore() {
        mPresenter.getMore();
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
                mPresenter.send(inputText.getText().toString());
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
        xRecyclerView.smoothScrollToPosition(position);
    }

    @Override
    public void showError(String text) {
//        Utils.showToast(getContext() , text);
    }

    @Override
    public void showProgressbar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressbar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void refreshComplete() {
        xRecyclerView.refreshComplete();
    }

    @Override
    public void loadMoreComplete() {
        xRecyclerView.loadMoreComplete();
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
        send.setBackground(getResources().getDrawable(R.drawable.bg_send_button_activate));
    }
    private void invalidSend(){
        send.setBackground(getResources().getDrawable(R.drawable.bg_send_button_invalid));
    }

    @Override
    public void emptyInput() {
        inputText.setText("");
    }

    @Override
    public void showEditMessageLayout() {
        editLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEditInvalidNotice() {
        editInvalidNotice.setVisibility(View.VISIBLE);
    }

    @Override
    public void disableSend() {

    }

    @Override
    public void enableSend() {

    }
}
