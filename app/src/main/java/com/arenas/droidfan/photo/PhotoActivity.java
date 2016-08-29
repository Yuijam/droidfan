package com.arenas.droidfan.photo;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.Utils;

public class PhotoActivity extends AppCompatActivity {

    private static final String EXTRA_USER_ID = "extra_user_id";
    private static final String EXTRA_ID = "extra_id";
    private static final String EXTRA_TABLE = "extra_table";
    private static final String EXTRA_POSITION = "extra_position";

    private PhotoContract.Presenter mPresenter;

    public static void start(Context context , int _id , String table , String userId , int position){
        Intent intent = new Intent(context , PhotoActivity.class);
        intent.putExtra(EXTRA_ID , _id);
        intent.putExtra(EXTRA_TABLE , table);
        intent.putExtra(EXTRA_USER_ID , userId);
        intent.putExtra(EXTRA_POSITION , position);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        String userId = getIntent().getStringExtra(EXTRA_USER_ID);
        String table = getIntent().getStringExtra(EXTRA_TABLE);
        int _id = getIntent().getIntExtra(EXTRA_ID , -1);
        int position = getIntent().getIntExtra(EXTRA_POSITION , -1);

        PhotoFragment fragment = (PhotoFragment)getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment == null){
            fragment = new PhotoFragment();
        }
        Utils.addFragmentToActivity(getSupportFragmentManager() , fragment , R.id.content_frame);

        mPresenter = new PhotoPresenter(this , fragment , table , _id , userId , position);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPresenter.onRequestResult(requestCode , permissions , grantResults);
    }
}
