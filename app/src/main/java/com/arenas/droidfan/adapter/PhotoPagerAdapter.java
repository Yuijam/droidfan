package com.arenas.droidfan.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.arenas.droidfan.R;
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

    private Activity activity;
    private AppBarLayout appBarLayout;
    private boolean isFull;

    public PhotoPagerAdapter(Context context , List<StatusModel> datas , Activity activity){
        mDatas = datas;
        mContext = context;

        this.activity = activity;
        appBarLayout = (AppBarLayout) activity.findViewById(R.id.app_bar);
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
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                full(!isFull);
//                hide();
            }
        });
        view.enable();
        view.setScaleType(ImageView.ScaleType.FIT_CENTER);
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

    private void full(boolean enable) {
        Log.d(TAG , "full~~~");
        if (enable) {
//            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
//            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//            activity.getWindow().setAttributes(lp);
//            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            appBarLayout.setVisibility(View.INVISIBLE);
            isFull = true;
        } else {
//            WindowManager.LayoutParams attr = activity.getWindow().getAttributes();
//            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            activity.getWindow().setAttributes(attr);
//            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            appBarLayout.setVisibility(View.VISIBLE);
            isFull = false;
        }
    }

//    private void hide(){
//        int i = toolbar.getSystemUiVisibility();
//        if (i == View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) {//2
//            toolbar.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//        } else if (i == View.SYSTEM_UI_FLAG_VISIBLE) {//0
//            toolbar.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
//        } else if (i == View.SYSTEM_UI_FLAG_LOW_PROFILE) {//1
//            toolbar.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        }
//    }
}
