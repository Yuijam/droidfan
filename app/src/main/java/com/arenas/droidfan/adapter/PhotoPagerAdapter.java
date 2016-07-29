package com.arenas.droidfan.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.main.message.MessageContract;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Arenas on 2016/7/28.
 */
public class PhotoPagerAdapter extends PagerAdapter {

    private static final String TAG = PhotoPagerAdapter.class.getSimpleName();

    private List<StatusModel> mDatas;
    private Context mContext;

    public PhotoPagerAdapter(Context context , List<StatusModel> datas){
        mDatas = datas;
        mContext = context;

    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        StatusModel model = mDatas.get(position);
        PhotoView view = new PhotoView(mContext);
        view.enable();
        view.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Log.d(TAG , "model.account = " + model.getAccount());
        Glide.with(mContext).load(model.getPhotoLargeUrl()).into(view);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void setData(List<StatusModel> models){
        mDatas = models;
        notifyDataSetChanged();
    }
}
