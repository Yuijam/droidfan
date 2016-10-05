package com.arenas.droidfan.detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.StatusModel;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_STATUS_TYPE = "extra_status_type";
    public static final String EXTRA_POSITION = "extra_position";
    public static final String EXTRA_STATUS_MODEL = "extra_status_model";

    public static final int TYPE_PROFILE = 4;
    public static final int TYPE_FAVORITES = 5;

    public static final int REQUEST_DETAIL = 6;

    public static final int RESULT_DELETE = 7;

    public static void start(Fragment activity , StatusModel statusModel , int position){
        Intent intent = new Intent(activity.getContext() , DetailActivity.class);
        intent.putExtra(EXTRA_POSITION , position);
        intent.putExtra(EXTRA_STATUS_MODEL , statusModel);
        activity.startActivityForResult(intent , REQUEST_DETAIL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        DetailFragment detailFragment = (DetailFragment)getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (detailFragment == null){
            detailFragment = DetailFragment.newInstance();
        }
        Utils.addFragmentToActivity(getSupportFragmentManager() , detailFragment , R.id.content_frame);

        int position = getIntent().getIntExtra(EXTRA_POSITION , -1);
        StatusModel statusModel = getIntent().getParcelableExtra(EXTRA_STATUS_MODEL);
        new DetailPresenter(this , detailFragment , statusModel , position);
    }
}
