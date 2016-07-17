package com.arenas.droidfan.detail;

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

        new DetailPresenter(getIntent().getIntExtra(EXTRA_STATUS_ID , -1) , FanFouDB.getInstance(this) , detailFragment);
    }
}
