package com.arenas.droidfan.main.publicstatus;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.data.db.FanFouDB;

public class PublicActivity extends AppCompatActivity {

    public static void start(Context context){
        Intent intent = new Intent(context , PublicActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        PublicFragment publicFragment = (PublicFragment)getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (publicFragment == null){
            publicFragment = new PublicFragment();
        }
        Utils.addFragmentToActivity(getSupportFragmentManager() , publicFragment , R.id.content_frame);

        new PublicPresenter(this , publicFragment);
    }
}
