package com.arenas.droidfan.main.Notice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arenas.droidfan.Api.Paging;
import com.arenas.droidfan.R;
import com.arenas.droidfan.main.HomeTimeline.HomeTimelineFragment;
import com.arenas.droidfan.service.FanFouService;

/**
 * Created by Arenas on 2016/6/23.
 */
public class NoticeFragment extends HomeTimelineFragment implements
        SwipeRefreshLayout.OnRefreshListener , View.OnClickListener{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice , container , false);
        init(view);
        return view;
    }

    @Override
    public void startService(Paging p) {
        FanFouService.getMentions(getContext() , p);
    }
}
