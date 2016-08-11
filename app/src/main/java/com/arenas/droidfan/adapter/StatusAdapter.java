package com.arenas.droidfan.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.DateTimeUtils;
import com.arenas.droidfan.Util.StatusUtils;
import com.arenas.droidfan.data.model.Photo;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.photo.PhotoActivity;
import com.arenas.droidfan.profile.ProfileActivity;
import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Arenas on 2016/7/18.
 */
public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder> {

    private List<StatusModel> mStatusList;
    private MyOnItemClickListener mListener;
    private Context mContext;
    private OnStatusImageClickListener imageClickListener;

    public StatusAdapter(Context context , List<StatusModel> mStatusList ,
                         MyOnItemClickListener Listener , OnStatusImageClickListener imageClickListener) {
        this.mStatusList = mStatusList;
        this.mListener = Listener;
        this.imageClickListener = imageClickListener;
        mContext = context;
    }

    @Override
    public StatusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StatusViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.status_item , parent ,false));
    }

    @Override
    public void onBindViewHolder(final StatusViewHolder holder, int position) {
        final StatusModel model = mStatusList.get(position);
        holder.mUsername.setText(model.getUserScreenName());
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
        final String photoImageUrl = model.getPhotoLargeUrl();

        if (photoImageUrl != null){
            showPhotoThumb(photoImageUrl , holder.mPhotoThumb);
            holder.mPhotoThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    PhotoActivity.start(mContext , model.get_id() , null );
                    imageClickListener.onImageClick(model.get_id());
                }
            });
        } else {
            hidePhotoThumb(holder.mPhotoThumb);
        }

        if (mListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClick(view , model.get_id());
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return true;
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
//        Picasso.with(mContext).load(url).resize(300 , 300).centerCrop().into(imageView);
        Glide.with(mContext).load(url).override(300 , 300).centerCrop().crossFade().into(imageView);
    }

    public void hidePhotoThumb(ImageView imageView ){
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
}
