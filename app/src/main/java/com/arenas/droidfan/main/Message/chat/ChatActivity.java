package com.arenas.droidfan.main.message.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.detail.DetailFragment;

public class ChatActivity extends AppCompatActivity {

    public static final String EXTRA_USERID = "extra_userid";
    public static final String EXTRA_USERNAME = "extra_username";

    public static void start(Context context , String userId , String username){
        Intent intent = new Intent(context , ChatActivity.class);
        intent.putExtra(EXTRA_USERID , userId);
        intent.putExtra(EXTRA_USERNAME , username);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String userId = getIntent().getStringExtra(EXTRA_USERID);
        String username = getIntent().getStringExtra(EXTRA_USERNAME);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(username);

        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);


        ChatFragment chatFragment = (ChatFragment)getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (chatFragment == null){
            chatFragment = new ChatFragment();
        }
        Utils.addFragmentToActivity(getSupportFragmentManager() , chatFragment , R.id.content_frame);

        new ChatPresenter(userId , username , chatFragment , this);
    }
}
