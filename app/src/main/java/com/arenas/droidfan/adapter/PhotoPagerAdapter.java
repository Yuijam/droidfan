package com.arenas.droidfan.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.myinterface.ListDialogListener;
import com.arenas.droidfan.myinterface.SaveImage;
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
    private StatusModel model;
    private SaveImage saveImage;

    public PhotoPagerAdapter(Context context , List<StatusModel> datas , SaveImage saveImage){
        mDatas = datas;
        mContext = context;
        this.saveImage = saveImage;
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
//这里可以打印position看看 会发现每滑动一次 都会预先加载下一个页面 也就是这个方法会执行两遍甚至三遍
        model = mDatas.get(position);
        PhotoView photoView = new PhotoView(mContext);
        String photoUrl = model.getPhotoLargeUrl();
        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Utils.createListDialog(mContext , new String[]{"保存图片"} , listDialogListener);
                return true;
            }
        });

        photoView.enable();
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(mContext).load(photoUrl).into(photoView);
        container.addView(photoView);
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void setData(List<StatusModel> models){
        mDatas = models;
        notifyDataSetChanged();
    }

    public String getPhotoUrl(int position){
        if (position < 0 || position > mDatas.size())
            return null;

        return mDatas.get(position).getPhotoLargeUrl();
    }

    ListDialogListener listDialogListener = new ListDialogListener() {
        @Override
        public void onItemClick(int which) {
            saveImage.save();//因为只有一个保存图片，所以没有做判断，之后如果添加其他选项再改……好吧 就是懒
        }
    };
}
