package com.arenas.droidfan.draft;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.arenas.droidfan.R;
import com.arenas.droidfan.adapter.DraftAdapter;
import com.arenas.droidfan.adapter.OnDraftClickListener;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.Draft;
import com.arenas.droidfan.update.UpdateFragment;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentNavigableMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DraftActivity extends AppCompatActivity implements DataSource.LoadDraftCallback{

    private static final String TAG = DraftActivity.class.getSimpleName();

    @BindView(R.id.progressbar)
    ProgressBar progressBar;
    @BindView(R.id.recycler_view)
    XRecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private DraftAdapter adapter;
    private FanFouDB fanFouDB;
    private List<Draft> drafts;

    public static final String EXTRA_DRAFT = "extra_draft";

    public static void start(Activity activity , int requestCode){
        Intent intent = new Intent(activity , DraftActivity.class);
        activity.startActivityForResult(intent , requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draft);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fanFouDB = FanFouDB.getInstance(this);
        adapter = new DraftAdapter(this , new ArrayList<Draft>() , listener);

        recyclerView.setPullRefreshEnabled(false);
        recyclerView.setLoadingMoreEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fanFouDB.loadDrafts(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

    @Override
    public void onDraftLoaded(List<Draft> drafts) {
        Log.d(TAG , "onDraftLoaded ~~");
        adapter.replaceData(drafts);
    }

    @Override
    public void onDataNotAvailable() {
        Log.d(TAG , "there is no draft~");
    }

    OnDraftClickListener listener = new OnDraftClickListener() {
        @Override
        public void onDraftItemClick(Draft draft) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DRAFT , draft);
            setResult(RESULT_OK , intent);
            finish();
        }
    };
}
