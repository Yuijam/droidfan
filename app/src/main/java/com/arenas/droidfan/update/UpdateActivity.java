package com.arenas.droidfan.update;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Window;

import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.CompatUtils;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.StatusModel;

import java.io.File;
import java.net.URI;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UpdateActivity extends AppCompatActivity {

    private static final String TAG = UpdateActivity.class.getSimpleName();

    public static final String EXTRA_ACTION_TYPE = "extra_action_type";
    public static final String EXTRA_STATUS_MODEL = "extra_status_model";

    public static final int TYPE_REPLY = 1;
    public static final int TYPE_RETWEET = 2;
    public static final int TYPE_FEEDBACK = 3;
    public static final int TYPE_SHARE = 4;

    public static void start(Activity activity){
        Intent intent = new Intent(activity , UpdateActivity.class);
        activity.startActivity(intent);
    }

    public static void start(Context context , StatusModel statusModel , int actionType){
        Intent intent = new Intent(context , UpdateActivity.class);
        intent.putExtra(EXTRA_STATUS_MODEL , statusModel);
        intent.putExtra(EXTRA_ACTION_TYPE, actionType);
        context.startActivity(intent);
    }

    @BindView(R.id.toolbar) Toolbar toolbar;
    private UpdateFragment updateFragment;

    private UpdateContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_update);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        updateFragment = (UpdateFragment)getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (updateFragment == null){
            updateFragment = UpdateFragment.newInstance();
            Utils.addFragmentToActivity(getSupportFragmentManager() , updateFragment , R.id.content_frame);
        }
        Intent i = getIntent();

        int actionType;
        if (i.getAction() != null && i.getAction().equals(Intent.ACTION_SEND)){
            //处理分享过来的
            actionType = TYPE_SHARE;
            Bundle shareData = i.getExtras();
            String text = shareData.getString(Intent.EXTRA_TEXT);
            Uri imageUri = shareData.getParcelable(Intent.EXTRA_STREAM);
            Log.d(TAG , "text = " + text);
            Log.d(TAG , "Uri = " + imageUri);
            String path = null;
            if (imageUri != null){
                path = CompatUtils.getPath(this , imageUri);
                Log.d(TAG , "path = " + path);
            }
            presenter = new UpdatePresenter(this , updateFragment , actionType , text , path);
        }else {
            //处理内部的动作
            actionType = i.getIntExtra(EXTRA_ACTION_TYPE, -1);
            StatusModel statusModel = getIntent().getParcelableExtra(EXTRA_STATUS_MODEL);
            presenter = new UpdatePresenter( this , updateFragment , actionType , statusModel);
        }
    }

    @Override
    public void onBackPressed() {
        updateFragment.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.onResult(this , requestCode , resultCode , data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        presenter.onPermissionRequestResult(requestCode , permissions , grantResults);
    }
}
