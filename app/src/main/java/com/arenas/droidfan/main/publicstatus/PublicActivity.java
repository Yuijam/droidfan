package com.arenas.droidfan.main.publicstatus;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Window;

import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.photo.PhotoContract;
import com.arenas.droidfan.profile.favorite.FavoriteFragment;
import com.arenas.droidfan.profile.photoalbum.PhotoAlbumFragment;
import com.arenas.droidfan.profile.profilestatus.ProfileStatusFragment;
import com.flask.floatingactionmenu.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PublicActivity extends AppCompatActivity {

    private static final String TAG = PublicActivity.class.getSimpleName();

    public static void start(Activity context){
        Intent intent = new Intent(context , PublicActivity.class);
        context.startActivity(intent , ActivityOptions.makeSceneTransitionAnimation(context).toBundle());
    }

    @OnClick(R.id.refresh_public_timeline) void goToTop(){
        if (presenter != null){
            presenter.refresh();
        }
    }

    PublicFragment publicFragment;
    PublicPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        publicFragment = (PublicFragment)getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (publicFragment == null){
            publicFragment = new PublicFragment();
        }
        Utils.addFragmentToActivity(getSupportFragmentManager() , publicFragment , R.id.content_frame);

        presenter = new PublicPresenter(this , publicFragment);
    }
}
