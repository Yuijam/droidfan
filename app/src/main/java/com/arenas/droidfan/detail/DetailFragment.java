package com.arenas.droidfan.detail;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.StatusUtils;
import com.squareup.picasso.Picasso;

public class DetailFragment extends Fragment implements DetailContract.View , View.OnClickListener{

    private static final String TAG = DetailFragment.class.getSimpleName();

    private DetailContract.Presenter mPresenter;

    private ImageView mAvatar;
    private TextView mUsername;
    private TextView mUserId;
    private TextView mStatusDetail;
    private ImageView mPhoto;
    private TextView mDate;
    private TextView mSource;

    private ImageView mFavorite;
    private ImageView mDelete;
    private ImageView mReply;

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
        mUsername = (TextView)view.findViewById(R.id.tv_username);
        mUserId = (TextView)view.findViewById(R.id.user_id);
        mStatusDetail = (TextView)view.findViewById(R.id.status_detail);
        mStatusDetail.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                copyToClipBoard(getContext() , mStatusDetail.getText().toString());
                return true;
            }
        });
        mDate = (TextView)view.findViewById(R.id.date);
        mSource = (TextView)view.findViewById(R.id.source);
        mAvatar = (ImageView)view.findViewById(R.id.iv_avatar);
        mPhoto = (ImageView)view.findViewById(R.id.photo);

        mReply = (ImageView)view.findViewById(R.id.reply);
        mReply.setOnClickListener(this);
        ImageView retweet = (ImageView) view.findViewById(R.id.retweet);
        retweet.setOnClickListener(this);
        mFavorite = (ImageView)view.findViewById(R.id.favorite);
        mFavorite.setOnClickListener(this);
        ImageView message = (ImageView)view.findViewById(R.id.message);
        message.setOnClickListener(this);
        ImageView share = (ImageView)view.findViewById(R.id.share);
        share.setOnClickListener(this);
        mDelete = (ImageView)view.findViewById(R.id.delete);
        mDelete.setOnClickListener(this);

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
                mPresenter.reply(getContext());
                break;
            case R.id.retweet:
                mPresenter.retweet(getContext());
                break;
            case R.id.favorite:
                mPresenter.favorite(getContext());
                break;
            case R.id.delete:
                showDeleteDialog();
                break;
        }
    }

    @Override
    public void finish() {
        getActivity().finish();
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
        Picasso.with(getContext()).load(url).into(mPhoto);
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
                        mPresenter.delete(getContext());
                    }
                }).create();
        alertDialog.show();
    }
}
