package com.arenas.droidfan.main.HomeTimeline;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arenas.droidfan.Api.Paging;
import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.StatusUtils;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.detail.DetailActivity;
import com.arenas.droidfan.main.MainActivity;
import com.arenas.droidfan.service.FanFouService;
import com.arenas.droidfan.update.UpdateActivity;
import com.arenas.droidfan.update.UpdateContract;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arenas on 2016/6/23.
 */
public class HomeTimelineFragment extends Fragment implements HomeTimelineContract.View , View.OnClickListener , SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = HomeTimelineFragment.class.getSimpleName();
    public static final String FILTER_ACTION = "com.arenas.droidfan.HOMETIMELINE";

    private HomeTimelineContract.Presenter mPresenter;

    //broadcast
    private IntentFilter mIntentFilter;
    private LocalBroadcastManager mLocalBroadcastManager;
    private LocalReceiver mLocalReceiver;

    private TimelineAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    public HomeTimelineFragment() {
        // Required empty public constructor
    }

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
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(FILTER_ACTION);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        mLocalReceiver = new LocalReceiver();
        mLocalBroadcastManager.registerReceiver(mLocalReceiver, mIntentFilter);

        mAdapter = new TimelineAdapter(new ArrayList<StatusModel>() , mListener );
    }

    public static HomeTimelineFragment newInstance() {
        return new HomeTimelineFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home , container , false);
        init(view);
        return view;
    }

    private void init(View view){
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        RecyclerView mRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton mFAB = (FloatingActionButton)view.findViewById(R.id.fab);
        mFAB.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab:
                mPresenter.newStatus();
                break;
        }
    }

    @Override
    public void showUpdateStatusUi() {
        Intent intent = new Intent(getContext() , UpdateActivity.class);
        startActivity(intent);
    }

    @Override
    public void startService(Paging p) {
        Intent intent = new Intent(getContext() , FanFouService.class);
        intent.putExtra(FanFouService.EXTRA_PAGING , p);
        intent.putExtra(FanFouService.EXTRA_REQUEST , FanFouService.HOME_TIMELINE);
        getContext().startService(intent);
    }

    @Override
    public void showStatus(List<StatusModel> status) {
        mAdapter.replaceData(status);
    }

    class LocalReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            mPresenter.onReceive(context , intent);
        }
    }

    @Override
    public void showError() {
        Toast.makeText(getContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mLocalReceiver);
    }

    private class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.StatusViewHolder>{

        private List<StatusModel> mStatusList;
        private OnItemClickListener mListener;

        public TimelineAdapter(List<StatusModel> mStatusList, OnItemClickListener mListener) {
            this.mStatusList = mStatusList;
            this.mListener = mListener;
        }

        @Override
        public StatusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new StatusViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.status_item , parent ,false));
        }

        @Override
        public void onBindViewHolder(final StatusViewHolder holder, int position) {
            holder.mUsername.setText(mStatusList.get(position).getUserScreenName());
            holder.mStatusText.setText(Utils.handleSimpleText(getContext() , mStatusList.get(position).getSimpleText() ));
            String avatarUrl = mStatusList.get(position).getUserProfileImageUrl();
            Picasso.with(getContext()).load(avatarUrl).into(holder.mAvatar);
            String photoThumbUrl = mStatusList.get(position).getPhotoThumbUrl();
            if (photoThumbUrl != null){
                showPhotoThumb(photoThumbUrl , holder.mPhotoThumb);
            } else {
                hidePhotoThumb(holder.mPhotoThumb);
            }

            if (mListener != null){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onItemClick(view , holder.getLayoutPosition());
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mStatusList.size();
        }

        public void setData(List<StatusModel> data){
            mStatusList = data;
        }

        public void replaceData(List<StatusModel> data){
            setData(data);
            notifyDataSetChanged();
        }

        public StatusModel getStatus(int position){
            return mStatusList.get(position);
        }

        public void showPhotoThumb(String url , ImageView imageView){
            imageView.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(url).resize(300 , 300).centerCrop().into(imageView);
        }

        public void hidePhotoThumb(ImageView imageView){
            imageView.setVisibility(View.GONE);
        }

        class StatusViewHolder extends RecyclerView.ViewHolder{
            RoundedImageView mAvatar;
            TextView mUsername;
            TextView mStatusText;
            ImageView mPhotoThumb;
            public StatusViewHolder(View itemView) {
                super(itemView);
                mAvatar = (RoundedImageView)itemView.findViewById(R.id.iv_avatar);
                mUsername = (TextView)itemView.findViewById(R.id.tv_username);
                mStatusText = (TextView)itemView.findViewById(R.id.tv_status_text);
                mPhotoThumb = (ImageView)itemView.findViewById(R.id.iv_photo_thumbnail);
            }
        }
    }

    @Override
    public void onRefresh() {
        mPresenter.refresh();
    }

    @Override
    public void hideRefreshBar() {
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showRefreshBar() {
        if (!mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    OnItemClickListener mListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View view , int position) {
            int _id = mAdapter.getStatus(position).get_id();
            Intent intent = new Intent(getContext() , DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_STATUS_ID , _id);
            startActivity(intent);
        }

        @Override
        public void onItemLongClick(int id, int position) {

        }
    };

    public interface OnItemClickListener{
        void onItemClick(View view , int position);
        void onItemLongClick(int id , int position);
    }
}
