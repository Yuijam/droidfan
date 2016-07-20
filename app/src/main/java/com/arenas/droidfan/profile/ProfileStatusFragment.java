package com.arenas.droidfan.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arenas.droidfan.R;
import com.arenas.droidfan.data.db.DataSource;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.StatusModel;
import com.arenas.droidfan.main.StatusAdapter;

import java.util.List;

public class ProfileStatusFragment extends Fragment implements DataSource.LoadStatusCallback{

    public ProfileStatusFragment() {
    }

    public static ProfileStatusFragment newInstance() {
        return new ProfileStatusFragment();
    }

    private List<StatusModel> statusList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FanFouDB.getInstance(getContext()).getHomeTLStatusList(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profilestatus_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setAdapter(new StatusAdapter(getContext() , statusList , null));
        }
        return view;
    }

    @Override
    public void onStatusLoaded(List<StatusModel> status) {
        statusList = status;
    }

    @Override
    public void onDataNotAvailable() {
        Log.d("111" , "---------ERROR");
    }
}
