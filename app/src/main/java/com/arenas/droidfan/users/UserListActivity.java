package com.arenas.droidfan.users;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.arenas.droidfan.R;

public class UserListActivity extends AppCompatActivity {

    public static final int TYPE_FOLLOWING = 1;
    public static final int TYPE_FOLLOWERS = 2;

    public static final String EXTRA_TYPE = "extra_type";
    public static final String EXTRA_USER_ID = "extra_user_id";

    public static void start(Context context , String userId , int type){
        Intent intent = new Intent(context , UserListActivity.class);
        intent.putExtra(EXTRA_TYPE , type);
        intent.putExtra(EXTRA_USER_ID , userId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // TODO: 2016/7/24  new presenter()
    }

}
