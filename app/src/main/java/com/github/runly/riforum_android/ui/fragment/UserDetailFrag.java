package com.github.runly.riforum_android.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.model.Entry;
import com.github.runly.riforum_android.retrofit.RetrofitFactory;
import com.github.runly.riforum_android.ui.adapter.EntriesAdapter;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ranly on 17-2-28.
 */

public class UserDetailFrag extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static UserDetailFrag createInstance() {
        return new UserDetailFrag();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        swipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(
                R.layout.fragment_rcommend, container, false);
        swipeRefreshLayout.setColorSchemeResources(R.color.color_base);
        swipeRefreshLayout.setOnRefreshListener(this::fetchDta);

        recyclerView = (RecyclerView) swipeRefreshLayout.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setupRecyclerView(recyclerView);
        fetchDta();

        return swipeRefreshLayout;
    }

    private void fetchDta() {
        swipeRefreshLayout.setRefreshing(true);
        Map<String, Object> map = new HashMap<>();
        map.put("page", System.currentTimeMillis());
        RetrofitFactory.getInstance().getEntryService().recommend(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if ("1".equals(response.code)) {
                        List<Entry> list = ((EntriesAdapter) recyclerView.getAdapter()).getItemList();
                        list.clear();
                        list.addAll(response.data);
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                    swipeRefreshLayout.setRefreshing(false);
                }, throwable -> {
                    throwable.printStackTrace();
                    swipeRefreshLayout.setRefreshing(false);
                });
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        EntriesAdapter entriesAdapter = new EntriesAdapter(getActivity(), new ArrayList<>());
        recyclerView.setAdapter(entriesAdapter);
    }
}
