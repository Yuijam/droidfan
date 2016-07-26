package com.arenas.droidfan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;
import com.arenas.droidfan.data.model.DirectMessageModel;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Arenas on 2016/7/25.
 */
public class ConversationListAdapter extends RecyclerView.Adapter<ConversationListAdapter.MessageViewHolder> {

    private List<DirectMessageModel> mMessage;
    private OnItemClickListener mListener;
    private Context mContext;

    public ConversationListAdapter(Context context , List<DirectMessageModel> message , OnItemClickListener Listener) {
        this.mMessage = message;
        this.mListener = Listener;
        mContext = context;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dm_list_item , parent ,false));
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {
        holder.username.setText(getConversationUsername(mMessage.get(position)));
        Picasso.with(mContext).load(getProfileAvatarUrl(mMessage.get(position))).into(holder.avatar);
        holder.content.setText(getContentToShow(mMessage.get(position)));

        if (mListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClick(view , holder.getLayoutPosition());
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
        return mMessage.size();
    }

    public String getConversationUsername(DirectMessageModel messageModel){
        return messageModel.getSenderScreenName().equals(AppContext.getScreenName())
                ? messageModel.getRecipientScreenName() : messageModel.getSenderScreenName();
    }

    public String getProfileAvatarUrl(DirectMessageModel messageModel){
        return messageModel.getSenderProfileImageUrl().equals(AppContext.getAvatarUrl())
                ? messageModel.getRecipientProfileImageUrl() : messageModel.getSenderProfileImageUrl();
    }

    public String getContentToShow(DirectMessageModel messageModel){
        return messageModel.getSenderScreenName().equals(AppContext.getScreenName())
                ? "我：" + messageModel.getText() : messageModel.getText();
    }

    public void setData(List<DirectMessageModel> data){
        mMessage = data;
    }

    public void replaceData(List<DirectMessageModel> data){
        setData(data);
        notifyDataSetChanged();
    }

    public DirectMessageModel getDM(int position){
        return mMessage.get(position);
    }

    class MessageViewHolder extends RecyclerView.ViewHolder{
        RoundedImageView avatar;
        TextView username;
//        TextView userId;
        TextView content;
        public MessageViewHolder(View itemView) {
            super(itemView);
            avatar = (RoundedImageView)itemView.findViewById(R.id.iv_avatar);
            username = (TextView)itemView.findViewById(R.id.tv_username);
//            userId = (TextView)itemView.findViewById(R.id.user_id);
            content = (TextView)itemView.findViewById(R.id.tv_content);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View view , int position);
        void onItemLongClick(int id , int position);
    }
}
