package com.arenas.droidfan.main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.DateTimeUtils;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.detail.DetailActivity;
import com.arenas.droidfan.main.HomeTimeline.HomeTimelineFragment;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Arenas on 2016/7/18.
 */
public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder> {

    private List<StatusModel> mStatusList;
    private OnItemClickListener mListener;
    private Context mContext;

    public StatusAdapter(Context context , List<StatusModel> mStatusList , OnItemClickListener Listener) {
        this.mStatusList = mStatusList;
        this.mListener = Listener;
        mContext = context;
    }

    @Override
    public StatusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StatusViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.status_item , parent ,false));
    }

    @Override
    public void onBindViewHolder(final StatusViewHolder holder, int position) {
        holder.mUsername.setText(mStatusList.get(position).getUserScreenName());
        holder.mStatusText.setText(Utils.handleSimpleText(mContext , mStatusList.get(position).getSimpleText() ));
        String avatarUrl = mStatusList.get(position).getUserProfileImageUrl();
        Picasso.with(mContext).load(avatarUrl).into(holder.mAvatar);
        holder.mTime.setText(DateTimeUtils.getInterval(mStatusList.get(position).getTime()));
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
        Picasso.with(mContext).load(url).resize(300 , 300).centerCrop().into(imageView);
    }

    public void hidePhotoThumb(ImageView imageView){
        imageView.setVisibility(View.GONE);
    }

    class StatusViewHolder extends RecyclerView.ViewHolder{
        RoundedImageView mAvatar;
        TextView mUsername;
        TextView mStatusText;
        ImageView mPhotoThumb;
        TextView mTime;
        public StatusViewHolder(View itemView) {
            super(itemView);
            mAvatar = (RoundedImageView)itemView.findViewById(R.id.iv_avatar);
            mUsername = (TextView)itemView.findViewById(R.id.tv_username);
            mStatusText = (TextView)itemView.findViewById(R.id.tv_status_text);
            mPhotoThumb = (ImageView)itemView.findViewById(R.id.iv_photo_thumbnail);
            mTime = (TextView)itemView.findViewById(R.id.time);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View view , int position);
        void onItemLongClick(int id , int position);
    }
}
