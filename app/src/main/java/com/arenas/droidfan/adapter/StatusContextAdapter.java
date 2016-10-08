package com.arenas.droidfan.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.DateTimeUtils;
import com.arenas.droidfan.Util.NetworkUtils;
import com.arenas.droidfan.Util.StatusUtils;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.api.Api;
import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.api.ApiFactory;
import com.arenas.droidfan.data.FavoritesColumns;
import com.arenas.droidfan.data.HomeStatusColumns;
import com.arenas.droidfan.data.NoticeColumns;
import com.arenas.droidfan.data.ProfileColumns;
import com.arenas.droidfan.data.PublicStatusColumns;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.detail.DetailActivity;
import com.arenas.droidfan.profile.ProfileActivity;
import com.arenas.droidfan.update.UpdateActivity;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.crypto.spec.IvParameterSpec;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Arenas on 2016/10/3.
 */

public class StatusContextAdapter extends RecyclerView.Adapter<StatusContextAdapter.StatusViewHolder>{

    private List<StatusModel> mStatusList;
    private Context mContext;
    private Fragment fragment;

    public StatusContextAdapter(Fragment fragment , List<StatusModel> mStatusList ) {
        this.mStatusList = mStatusList;
        this.fragment = fragment;
        mContext = fragment.getContext();
    }

    @Override
    public StatusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StatusViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.status_context_item , parent ,false));
    }

    @Override
    public void onBindViewHolder(final StatusViewHolder holder, final int position) {
        final StatusModel model = mStatusList.get(position);
        holder.mUsername.setText(model.getUserScreenName());
//        if (model.getUserId().equals(AppContext.getAccount())){
//            holder.mDelete.setVisibility(View.VISIBLE);
//            holder.mReply.setVisibility(View.GONE);
//        }else {
//            holder.mReply.setVisibility(View.VISIBLE);
//            holder.mDelete.setVisibility(View.GONE);
//        }
        StatusUtils.setItemStatus(holder.mStatusText , model.getSimpleText());
        String avatarUrl = model.getUserProfileImageUrl();

        Picasso.with(mContext).load(avatarUrl).into(holder.mAvatar);
        holder.mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileActivity.start(mContext , model.getUserId());
            }
        });
        holder.mTime.setText(DateTimeUtils.getInterval(model.getTime()));

        holder.mItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailActivity.start(fragment , model , position);
            }
        });
//        holder.mReply.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                UpdateActivity.start(mContext , model , UpdateActivity.TYPE_REPLY);
//            }
//        });
//
//        holder.mRetweet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                UpdateActivity.start(mContext , model , UpdateActivity.TYPE_RETWEET);
//            }
//        });
//
//        holder.mDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showDeleteDialog(model.getId() , position);
//            }
//        });
//
//        holder.mFavorite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mStatusList.size();
    }

    private void setData(List<StatusModel> data){
        mStatusList = data;
    }

    public void replaceData(List<StatusModel> data){
        setData(data);
        notifyDataSetChanged();
    }

    public void removeData(int position){
        mStatusList.remove(position);
        notifyDataSetChanged();
    }

    public StatusModel getStatus(int position){
        return mStatusList.get(position);
    }

    class StatusViewHolder extends RecyclerView.ViewHolder{
        RoundedImageView mAvatar;
        TextView mUsername;
        TextView mStatusText;
        RoundedImageView mPhotoThumb;
        TextView mTime;
        RelativeLayout mItemLayout;
//        ImageView mReply;
//        ImageView mRetweet;
//        ImageView mFavorite;
//        ImageView mMessage;
//        ImageView mDelete;
        StatusViewHolder(View itemView) {
            super(itemView);
            mAvatar = (RoundedImageView)itemView.findViewById(R.id.iv_avatar);
            mUsername = (TextView)itemView.findViewById(R.id.tv_username);
            mStatusText = (TextView)itemView.findViewById(R.id.tv_status_text);
            mPhotoThumb = (RoundedImageView)itemView.findViewById(R.id.iv_photo_thumbnail);
            mTime = (TextView)itemView.findViewById(R.id.time);
            mItemLayout = (RelativeLayout)itemView.findViewById(R.id.context_item_layout);
//            mReply = (ImageView)itemView.findViewById(R.id.reply);
//            mRetweet = (ImageView)itemView.findViewById(R.id.retweet);
//            mFavorite = (ImageView)itemView.findViewById(R.id.favorite);
//            mMessage = (ImageView)itemView.findViewById(R.id.message);
//            mDelete = (ImageView)itemView.findViewById(R.id.delete);
        }
    }
}
