package com.arenas.droidfan.detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.data.db.FanFouDB;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_STATUS_ID = "extra_status_id";
    public static final String EXTRA_STATUS_TYPE = "extra_status_type";
    public static final String EXTRA_POSITION = "extra_position";

    public static final int TYPE_HOME = 1;
    public static final int TYPE_MENTIONS = 2;
    public static final int TYPE_PUBLIC = 3;
    public static final int TYPE_PROFILE = 4;
    public static final int TYPE_FAVORITES = 5;

    public static final int REQUEST_DETAIL = 6;

    public static final int RESULT_DELETE = 7;

    public static void start(Activity activity , int type , int _id , int position){
        Intent intent = new Intent(activity , DetailActivity.class);
        intent.putExtra(EXTRA_STATUS_ID , _id);
        intent.putExtra(EXTRA_STATUS_TYPE , type);
        intent.putExtra(EXTRA_POSITION , position);
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

        int _id = getIntent().getIntExtra(EXTRA_STATUS_ID , -1 );
        int type = getIntent().getIntExtra(EXTRA_STATUS_TYPE , -1);
        int position = getIntent().getIntExtra(EXTRA_POSITION , -1);
        new DetailPresenter(_id , type , this , detailFragment , position);
    }
}
