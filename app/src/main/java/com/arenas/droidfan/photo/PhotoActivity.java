package com.arenas.droidfan.photo;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.data.model.StatusModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PhotoActivity extends AppCompatActivity {

    private static final String EXTRA_POSITION = "extra_position";
    private static final String EXTRA_STATUS_LIST = "extra_status_list";

    private PhotoContract.Presenter mPresenter;

    public static void start(Context context , List<StatusModel> modelList , int position){
        Intent intent = new Intent(context , PhotoActivity.class);
        intent.putExtra(EXTRA_POSITION , position);
        intent.putParcelableArrayListExtra(EXTRA_STATUS_LIST , (ArrayList<StatusModel>)modelList);
        context.startActivity(intent);
    }
    //single
    public static void start(Context context , StatusModel statusModel){
        List<StatusModel> statusModelList = new ArrayList<>();
        statusModelList.add(statusModel);
        start(context , statusModelList , 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        int position = getIntent().getIntExtra(EXTRA_POSITION , -1);
        List<StatusModel> statusModelList = getIntent().getParcelableArrayListExtra(EXTRA_STATUS_LIST);

        PhotoFragment fragment = (PhotoFragment)getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment == null){
            fragment = new PhotoFragment();
        }
        Utils.addFragmentToActivity(getSupportFragmentManager() , fragment , R.id.content_frame);
        mPresenter = new PhotoPresenter(this , fragment , statusModelList , position);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPresenter.onRequestResult(requestCode , permissions , grantResults);
    }
}
