package com.arenas.droidfan.update;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.data.db.FanFouDB;

public class UpdateActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "extra_id";
    public static final String EXTRA_ACTION_TYPE = "extra_action_type";
    public static final String EXTRA_STATUS_TYPE = "extra_status_type";

    public static final int TYPE_REPLY = 1;
    public static final int TYPE_RETWEET = 2;

    public static void start(Context context , int _id , int actionType , int statusType){
        Intent intent = new Intent(context , UpdateActivity.class);
        intent.putExtra(EXTRA_ID , _id);
        intent.putExtra(EXTRA_ACTION_TYPE, actionType);
        intent.putExtra(EXTRA_STATUS_TYPE , statusType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        UpdateFragment updateFragment = (UpdateFragment)getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (updateFragment == null){
            updateFragment = UpdateFragment.newInstance();
            Utils.addFragmentToActivity(getSupportFragmentManager() , updateFragment , R.id.content_frame);
        }

        int _id = getIntent().getIntExtra(EXTRA_ID , -1);
        int actionType = getIntent().getIntExtra(EXTRA_ACTION_TYPE, -1);
        int statusType = getIntent().getIntExtra(EXTRA_STATUS_TYPE, -1);
        new UpdatePresenter( _id , actionType , statusType , this , updateFragment);
    }
}
