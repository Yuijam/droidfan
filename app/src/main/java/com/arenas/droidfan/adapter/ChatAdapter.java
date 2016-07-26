package com.arenas.droidfan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;
import com.arenas.droidfan.data.model.DirectMessageModel;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Arenas on 2016/7/26.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private Context mContext;
    private List<DirectMessageModel> mDatas;
    private OnItemClickListener mListener;

    public ChatAdapter(Context mContext, List<DirectMessageModel> mDatas, OnItemClickListener mListener) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        this.mListener = mListener;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_item , parent ,false));
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        DirectMessageModel model = mDatas.get(position);
        String senderName = model.getSenderScreenName();
        if (senderName.equals(AppContext.getScreenName())){
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.rightText.setText(model.getText());
            Picasso.with(mContext).load(model.getSenderProfileImageUrl()).into(holder.rightAvatar);
        }else {
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.leftText.setText(model.getText());
            Picasso.with(mContext).load(model.getSenderProfileImageUrl()).into(holder.leftAvatar);
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void setData(List<DirectMessageModel> data){
        mDatas = data;
    }

    public void replaceData(List<DirectMessageModel> data){
        setData(data);
        notifyDataSetChanged();
    }

    public DirectMessageModel getDM(int position){
        return mDatas.get(position);
    }

    class ChatViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftLayout;
        LinearLayout rightLayout;
        RoundedImageView leftAvatar;
        RoundedImageView rightAvatar;
        TextView leftText;
        TextView rightText;

        public ChatViewHolder(View itemView) {
            super(itemView);
            leftLayout = (LinearLayout)itemView.findViewById(R.id.left_layout);
            rightLayout = (LinearLayout)itemView.findViewById(R.id.right_layout);
            leftText = (TextView)itemView.findViewById(R.id.left_msg);
            rightText = (TextView)itemView.findViewById(R.id.right_msg);
            leftAvatar = (RoundedImageView)itemView.findViewById(R.id.iv_left_avatar);
            rightAvatar = (RoundedImageView)itemView.findViewById(R.id.iv_right_avatar);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View view , int position);
        void onItemLongClick(int id , int position);
    }
}
