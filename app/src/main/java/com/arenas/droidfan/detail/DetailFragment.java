package com.arenas.droidfan.detail;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.StatusUtils;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.adapter.StatusAdapter;
import com.arenas.droidfan.adapter.StatusContextAdapter;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.photo.PhotoActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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

    private XRecyclerView statusContext;
    private ProgressBar progressBar;

    private StatusContextAdapter adapter;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new StatusContextAdapter(this , new ArrayList<StatusModel>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        init(view);
        return view;
    }

    private void init(View view){
        View header = LayoutInflater.from(getContext()).inflate(R.layout.detail_header_view, (ViewGroup)view.findViewById(android.R.id.content),false);
        ButterKnife.bind(this , header);

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
        mPhoto.setOnClickListener(this);
        statusContext = (XRecyclerView)view.findViewById(R.id.status_context);
        statusContext.setLayoutManager(new LinearLayoutManager(getContext()));
        statusContext.addHeaderView(header);
        statusContext.setPullRefreshEnabled(false);
        statusContext.setLoadingMoreEnabled(false);
        statusContext.setAdapter(adapter);

        progressBar = (ProgressBar)view.findViewById(R.id.progressbar);

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
            case R.id.photo:
                mPresenter.showLargePhoto();
                break;
        }
    }

    @Override
    public void finish() {
        getActivity().finish();
    }

    @Override
    public void setResult(int resultCode , int position) {
        Intent intent = new Intent();
        intent.putExtra(DetailActivity.EXTRA_POSITION , position);
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
        Glide.with(getContext()).load(url).into(new GlideDrawableImageViewTarget(mPhoto){
            //应该是要由个gif的placeholder的 显示正在努力加载
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                super.onResourceReady(resource, animation);
                mPhoto.setVisibility(View.VISIBLE);
            }
        });
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
                        dialogInterface.dismiss();
                        mPresenter.delete();
                    }
                }).create();
        alertDialog.show();
    }

    @Override
    public void showStatusContext(List<StatusModel> statusModelsList) {
        adapter.replaceData(statusModelsList);
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }
}
