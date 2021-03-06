package com.arenas.droidfan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.arenas.droidfan.R;
import com.arenas.droidfan.data.model.StatusModel;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arenas on 2016/7/27.
 */
public class PhotoAlbumAdapter extends RecyclerView.Adapter<PhotoAlbumAdapter.PhotoViewHolder> {

    private Context mContext;
    private List<StatusModel> mDatas;
    private MyOnItemClickListener mListener;
    private int screenWidth;

    public PhotoAlbumAdapter(Context mContext, List<StatusModel> mDatas, MyOnItemClickListener mListener) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        this.mListener = mListener;
        WindowManager manager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        screenWidth = manager.getDefaultDisplay().getWidth();
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoViewHolder(LayoutInflater.from(mContext).inflate(R.layout.photo_item , parent , false));
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, final int position) {
        final StatusModel model = mDatas.get(position);

        if (model.getPhotoThumbUrl() != null)
        Picasso.with(mContext).load(model.getPhotoLargeUrl()).resize(screenWidth/3 , screenWidth/3).centerCrop().into(holder.photo);
//            Glide.with(mContext).load(model.getPhotoLargeUrl()).override(screenWidth/3 , screenWidth/3).centerCrop().into(holder.photo);
        //这里只能用Picasso ， 如果用Glide的话gif会加载不出 我也不知道为什么
        if (mListener != null){
            holder.photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClick(view , model , position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void setData(List<StatusModel> data){
        mDatas = data;
    }

    public void replaceData(List<StatusModel> data){
        setData(data);
        notifyDataSetChanged();
    }

    public List<StatusModel> getPhotoData(){
        return mDatas;
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder{

        RoundedImageView photo;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            photo = (RoundedImageView)itemView.findViewById(R.id.photo);
        }
    }
}
