package com.arenas.droidfan.main;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;
import com.arenas.droidfan.detail.DetailActivity;
import com.arenas.droidfan.main.hometimeline.HomeTimelineContract;
import com.arenas.droidfan.main.hometimeline.HomeTimelineFragment;
import com.arenas.droidfan.main.hometimeline.HomeTimelinePresenter;
import com.arenas.droidfan.main.message.MessageFragment;
import com.arenas.droidfan.main.message.MessagePresenter;
import com.arenas.droidfan.main.notice.NoticeFragment;
import com.arenas.droidfan.main.notice.NoticePresenter;
import com.arenas.droidfan.main.publicstatus.PublicActivity;
import com.arenas.droidfan.notify.PushService;
import com.arenas.droidfan.profile.ProfileActivity;
import com.arenas.droidfan.setting.SettingsActivity;
import com.arenas.droidfan.update.UpdateActivity;
import com.arenas.droidfan.update.UpdatePresenter;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.tab_layout) TabLayout tabLayout;

    @OnClick(R.id.fab) void onFabClick(){
        UpdateActivity.start(this);
    }

    TabFragmentAdapter fragmentAdapter;
    private HomeTimelineContract.View homeTimelineView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        List<String> tabList = new ArrayList<>();
        tabList.add(getString(R.string.home_page));
        tabList.add(getString(R.string.notice));
        tabList.add(getString(R.string.message));
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new HomeTimelineFragment());
        fragments.add(new NoticeFragment());
        fragments.add(new MessageFragment());
        viewPager.setOffscreenPageLimit(2);

        tabLayout.addTab(tabLayout.newTab().setText(tabList.get(0)));//添加tab
        tabLayout.addTab(tabLayout.newTab().setText(tabList.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(tabList.get(2)));
        fragmentAdapter = new TabFragmentAdapter(getSupportFragmentManager(), tabList , fragments);
        viewPager.setAdapter(fragmentAdapter);//给ViewPager设置适配器
        tabLayout.setupWithViewPager(viewPager);//将TabLayout和ViewPager关联起来。
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        initNavHeader(navigationView.getHeaderView(0));

        homeTimelineView = (HomeTimelineFragment)fragmentAdapter.getItem(0);

        new HomeTimelinePresenter(this , (HomeTimelineFragment)fragmentAdapter.getItem(0));
        new NoticePresenter(this , (NoticeFragment)fragmentAdapter.getItem(1));
        new MessagePresenter(this , (MessageFragment)fragmentAdapter.getItem(2));

        if (PushService.shouldStartAlarm(this)){
            PushService.setServiceAlarm(this);
        }

        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(1);
        manager.cancel(2);

        getResources().getColor(R.color.colorPrimary);
    }

    private void initNavHeader(View view){
        RoundedImageView avatar = (RoundedImageView)view.findViewById(R.id.header_avatar);
        TextView screenNameView = (TextView)view.findViewById(R.id.screen_name);
        TextView userIdView = (TextView)view.findViewById(R.id.user_id);

        Picasso.with(this).load(AppContext.getAvatarUrl()).into(avatar);
        screenNameView.setText(AppContext.getScreenName());
        userIdView.setText("@" + AppContext.getAccount());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.profile) {
            ProfileActivity.start(this , AppContext.getAccount());
        } else if (id == R.id.nav_public_timeline) {
            PublicActivity.start(this);
        } else if (id == R.id.nav_setting) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(this , SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG , "onActivityResult --- > resultCode = " + resultCode + " , requestCode = " + requestCode);
        if (requestCode == DetailActivity.REQUEST_DETAIL){
            if (resultCode == DetailActivity.RESULT_DELETE){
                int position = data.getIntExtra(DetailActivity.EXTRA_POSITION , -1);
                int type = data.getIntExtra(DetailActivity.EXTRA_STATUS_TYPE , -1);
                if (type == DetailActivity.TYPE_HOME){
                    homeTimelineView.removeStatusItem(position);
                }
            }
        }
    }
}
