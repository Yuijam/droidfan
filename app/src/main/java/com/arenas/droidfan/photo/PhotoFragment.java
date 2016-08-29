package com.arenas.droidfan.photo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.arenas.droidfan.R;
import com.arenas.droidfan.adapter.PhotoPagerAdapter;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.main.TabFragmentAdapter;
import com.arenas.droidfan.myinterface.ListDialogListener;
import com.arenas.droidfan.myinterface.SaveImage;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Arenas on 2016/7/28.
 */
public class PhotoFragment extends Fragment implements PhotoContract.View {

    private static final String TAG = PhotoFragment.class.getSimpleName();

    private PhotoContract.Presenter mPresenter;

    @BindView(R.id.view_pager) ViewPager mPager;
    private PhotoPagerAdapter mAdapter;

    @Override
    public void setPresenter(Object presenter) {
        mPresenter = (PhotoContract.Presenter)presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    SaveImage saveImage = new SaveImage() {
        @Override
        public void save() {
            mPresenter.savePhoto(getActivity() , mAdapter.getPhotoUrl(mPager.getCurrentItem()));
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new PhotoPagerAdapter(getContext() , new ArrayList<StatusModel>() , saveImage);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo , container , false);

        ButterKnife.bind(this , view);
        mPager.setAdapter(mAdapter);

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                getActivity().finish();
                break;
        }
        return true;
    }

    @Override
    public void showPhoto(List<StatusModel> models) {
        mAdapter.setData(models);
    }

    @Override
    public void showError(String text) {
        Toast.makeText(getContext() , text , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setCurrentPage(int position) {
        mPager.setCurrentItem(position , true);
    }

}
