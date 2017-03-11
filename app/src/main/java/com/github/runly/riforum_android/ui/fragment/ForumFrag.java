package com.github.runly.riforum_android.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.FitWindowsFrameLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.model.Entry;
import com.github.runly.riforum_android.retrofit.RetrofitFactory;
import com.github.runly.riforum_android.ui.adapter.ForumAdapter;
import com.github.runly.riforum_android.ui.view.MarginDecoration;
import com.github.runly.riforum_android.utils.PlateHeaderNumUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ranly on 17-2-7.
 */

public class ForumFrag extends Fragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;


    public static ForumFrag createInstance() {
        return new ForumFrag();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        swipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(
            R.layout.fragment_forum, container, false);
        swipeRefreshLayout.setColorSchemeResources(R.color.color_base);
        swipeRefreshLayout.setOnRefreshListener(this::fetchData);

        recyclerView = (RecyclerView) swipeRefreshLayout.findViewById(R.id.recyclerView);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(manager);
        View header = inflater.inflate(R.layout.recycler_forum_header, recyclerView, false);
        setupRecyclerView(recyclerView, header, manager);

        fetchData();

        return swipeRefreshLayout;
    }

    private void setupRecyclerView(RecyclerView recyclerView, View header, GridLayoutManager manager) {
        ForumAdapter forumAdapter = new ForumAdapter(getActivity(), new ArrayList<>());
        forumAdapter.setHeaderView(header);
//        recyclerView.addItemDecoration(new MarginDecoration(getActivity(), 4, 8, 4, 8));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(forumAdapter);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position % 5 == 1 || forumAdapter.isHeader(position)) {
                    return manager.getSpanCount();
                } else {
                    return 1;
                }
            }
        });
    }

    private void fetchData() {
        swipeRefreshLayout.setRefreshing(true);
        RetrofitFactory.getInstance()
            .getEntryService()
            .all_plate_entries()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(response -> {
                if ("1".equals(response.code)) {
                    List<Entry> list = ((ForumAdapter) recyclerView.getAdapter()).getItemList();
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
}
