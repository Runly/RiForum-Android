package com.github.runly.riforum_android.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.ui.adapter.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.github.runly.riforum_android.R.id.recyclerView;
import static com.github.runly.riforum_android.R.id.withText;

/**
 * Created by ranly on 17-2-7.
 */

public class FollowFrag extends Fragment {
    public final static String ITEMS_COUNT_KEY = "FollowFrag$ItemsCount";

    public static FollowFrag createInstance(int itemsCount) {
        FollowFrag partThreeFragment = new FollowFrag();
        Bundle bundle = new Bundle();
        bundle.putInt(ITEMS_COUNT_KEY, itemsCount);
        partThreeFragment.setArguments(bundle);
        return partThreeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(
                R.layout.fragment_follow, container, false);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorBase);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },500);
            }
        });
        RecyclerView recyclerView = (RecyclerView) swipeRefreshLayout.findViewById(R.id.recyclerView);
        setupRecyclerView(recyclerView);
        return swipeRefreshLayout;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(createItemList());
        recyclerView.setAdapter(recyclerAdapter);
    }

    private List<String> createItemList() {
        List<String> itemList = new ArrayList<>();
        Bundle bundle = getArguments();
        if(bundle!=null) {
            int itemsCount = bundle.getInt(ITEMS_COUNT_KEY);
            for (int i = 0; i < itemsCount; i++) {
                itemList.add("Item " + i);
            }
        }
        return itemList;
    }
}
