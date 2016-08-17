package com.arenas.droidfan.detail;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.StatusUtils;
import com.arenas.droidfan.Util.Utils;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailFragment extends Fragment implements DetailContract.View , View.OnClickListener{

    private static final String TAG = DetailFragment.class.getSimpleName();

    private DetailContract.Presenter mPresenter;

    @BindView(R.id.tv_username) TextView mUsername;
    @BindView(R.id.user_id) TextView mUserId;
    @BindView(R.id.status_detail) TextView mStatusDetail;
    @BindView(R.id.source) TextView mSource;
    @BindView(R.id.date) TextView mDate;
    @BindView(R.id.photo) ImageView mPhoto;
    @BindView(R.id.iv_avatar) ImageView mAvatar;
    @BindView(R.id.reply) ImageView mReply;
    @BindView(R.id.delete) ImageView mDelete;
    @BindView(R.id.favorite) ImageView mFavorite;
    @BindView(R.id.layout_user) LinearLayout mUserlayout;
    @BindView(R.id.retweet) ImageView retweet;
    @BindView(R.id.message) ImageView message;

    @BindView(R.id.share) ImageView share;

    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance() {
        return new DetailFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(Object presenter) {
        mPresenter = (DetailContract.Presenter)presenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        init(view);
        return view;
    }

    private void init(View view){
        ButterKnife.bind(this , view);

        mStatusDetail.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                copyToClipBoard(getContext() , mStatusDetail.getText().toString());
                return true;
            }
        });

        mReply.setOnClickListener(this);
        retweet.setOnClickListener(this);
        mFavorite.setOnClickListener(this);
        message.setOnClickListener(this);
        share.setOnClickListener(this);
        mDelete.setOnClickListener(this);
        mUserlayout.setOnClickListener(this);

        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.reply:
                mPresenter.reply();
                break;
            case R.id.retweet:
                mPresenter.retweet();
                break;
            case R.id.favorite:
                mPresenter.favorite();
                break;
            case R.id.delete:
                showDeleteDialog();
                break;
            case R.id.message:
                mPresenter.sendMessage();
                break;
            case R.id.layout_user:
                mPresenter.openUser();
                break;
            case R.id.share:
                Utils.showToast(getContext() , "不要戳我，我还没完成");
                break;

        }
    }

    @Override
    public void finish() {
        getActivity().finish();
    }

    @Override
    public void setResult(int resultCode , int position , int type) {
        Intent intent = new Intent();
        intent.putExtra(DetailActivity.EXTRA_POSITION , position);
        intent.putExtra(DetailActivity.EXTRA_STATUS_TYPE , type);
        getActivity().setResult(resultCode , intent);
    }

    private void copyToClipBoard(Context context, String content) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text", content);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context , "已复制到剪切板" , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDelete() {
        mDelete.setVisibility(View.VISIBLE);
        mReply.setVisibility(View.GONE);
    }

    @Override
    public void showUsername(String username) {
        mUsername.setText(username);
    }

    @Override
    public void showUserId(String userId) {
        mUserId.setText(userId);
    }

    @Override
    public void showAvatar(String url) {
        Picasso.with(getContext()).load(url).into(mAvatar);
    }

    @Override
    public void showStatusText(String status) {
        StatusUtils.setStatus(mStatusDetail , status);
    }

    @Override
    public void showDate(String date) {
        mDate.setText(date);
    }

    @Override
    public void showSource(String source) {
        mSource.setText(source);
    }

    @Override
    public void showPhoto(String url) {
//        Picasso.with(getContext()).load(url).into(mPhoto);
        Glide.with(getContext()).load(url).into(mPhoto);
    }

    @Override
    public void showFavorite(int resId) {
        mFavorite.setImageResource(resId);
    }

    @Override
    public void showError() {
        Toast.makeText(getContext() , "something is wrong ! " , Toast.LENGTH_SHORT).show();
    }

    private void showDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final AlertDialog alertDialog = builder.setMessage(getString(R.string.delete_dialog_message))
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mPresenter.delete();
                    }
                }).create();
        alertDialog.show();
    }
}
