package com.arenas.droidfan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arenas.droidfan.R;
import com.arenas.droidfan.data.model.UserModel;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Arenas on 2016/7/24.
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>{
    private List<UserModel> mUsersList;
    private OnItemClickListener mListener;
    private Context mContext;

    public UsersAdapter(Context context , List<UserModel> usersList , OnItemClickListener Listener) {
        this.mUsersList = usersList;
        this.mListener = Listener;
        mContext = context;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item , parent ,false));
    }

    @Override
    public void onBindViewHolder(final UserViewHolder holder, int position) {
        holder.username.setText(mUsersList.get(position).getScreenName());
        holder.userId.setText(mUsersList.get(position).getId());
        String text = mUsersList.get(position).getDescription();
//        if (TextUtils.isEmpty(text)){
//            holder.description.setVisibility(View.GONE);
//        }else {
            holder.description.setText(text);
//        }
        String avatarUrl = mUsersList.get(position).getProfileImageUrlLarge();
        Picasso.with(mContext).load(avatarUrl).into(holder.avatar);

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
        return mUsersList.size();
    }

    public void setData(List<UserModel> data){
        mUsersList = data;
    }

    public void replaceData(List<UserModel> data){
        setData(data);
        notifyDataSetChanged();
    }

    public UserModel getUser(int position){
        return mUsersList.get(position);
    }

    class UserViewHolder extends RecyclerView.ViewHolder{
        RoundedImageView avatar;
        TextView username;
        TextView userId;
        TextView description;
        public UserViewHolder(View itemView) {
            super(itemView);
            avatar = (RoundedImageView)itemView.findViewById(R.id.iv_avatar);
            username = (TextView)itemView.findViewById(R.id.tv_username);
            userId = (TextView)itemView.findViewById(R.id.user_id);
            description = (TextView)itemView.findViewById(R.id.description);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View view , int position);
        void onItemLongClick(int id , int position);
    }
}
