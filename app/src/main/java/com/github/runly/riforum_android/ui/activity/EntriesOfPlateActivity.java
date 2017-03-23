package com.github.runly.riforum_android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.Constants;
import com.github.runly.riforum_android.model.Entry;
import com.github.runly.riforum_android.model.Plate;
import com.github.runly.riforum_android.model.ResponseBase;
import com.github.runly.riforum_android.retrofit.RetrofitFactory;
import com.github.runly.riforum_android.ui.adapter.EntriesAdapter;
import com.github.runly.riforum_android.ui.view.MyDecoration;
import com.github.runly.riforum_android.utils.RecyclerScrollToTop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ranly on 17-3-12.
 */

public class EntriesOfPlateActivity extends TopBarActivity {
    private RecyclerView recyclerView;
    private TextView entriesNum;
    private Plate plate;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isFetching;
    private String message = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enties_of_plate);

        plate = (Plate) getIntent().getSerializableExtra(Constants.INTENT_PLATE_DATA);

        init();
    }

    private void init() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            message = "";
            fetchData(false, System.currentTimeMillis());
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.color_base);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        View header = getLayoutInflater().inflate(R.layout.recycler_entries_of_plate_header, recyclerView, false);
        TextView plateName = (TextView) header.findViewById(R.id.plate_name);
        ImageView plateIcon = (ImageView) header.findViewById(R.id.plate_icon);
        if (plate != null) {
            plateName.setText(plate.name);
            Glide.with(this)
                .load(plate.icon)
                .crossFade()
                .into(plateIcon);
        }
        entriesNum = (TextView) header.findViewById(R.id.entries_number);

        setupRecyclerView(recyclerView, header);

        swipeRefreshLayout.setRefreshing(true);
        fetchData(false, System.currentTimeMillis());
    }

    private void setupRecyclerView(RecyclerView recyclerView, View header) {

        EntriesAdapter adapter = new EntriesAdapter(this, new ArrayList<>());
        adapter.setHeaderView(header);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new MyDecoration(this, 8, 8, 8, 0, false));
        recyclerView.setAdapter(adapter);
        LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int itemCount = lm.getItemCount();
                int currentPosition = lm.findFirstVisibleItemPosition();
                if (currentPosition >= itemCount / 2) {
                    if (!isFetching) {
                        List<Entry> list = ((EntriesAdapter) recyclerView.getAdapter()).getItemList();
                        if (list.size() > 0) {
                            Entry lastEntry = list.get(list.size() - 1);
                            fetchData(true, lastEntry.time);
                        }
                    }
                }
            }
        });
    }

    private void fetchData(boolean isMore, long page) {
        if ("end".equals(message) || isFetching) {
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        map.put("plate_id", plate.id);

        isFetching = true;

        RetrofitFactory.getInstance()
            .getEntryService()
            .plate_entries(map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(response -> {
                if ("1".equals(response.code)) {
                    List<Entry> list = ((EntriesAdapter) recyclerView.getAdapter()).getItemList();
                    if (!isMore) {
                        list.clear();
                    }
                    list.addAll(response.data);
                    recyclerView.getAdapter().notifyDataSetChanged();
                    entriesNum.setText(String.format(getString(R.string.release_num), response.entry_number));
                    message = response.message;
                }
                swipeRefreshLayout.setRefreshing(false);
                isFetching = false;
            }, throwable -> {
                throwable.printStackTrace();
                swipeRefreshLayout.setRefreshing(false);
                isFetching = false;
            });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (plate != null) {
            topBar.getTxtCenter().setText(plate.name);
        }
        topBar.setOnClickListener(v -> RecyclerScrollToTop.scrollToTop(recyclerView));
    }

    @Override
    public void onPause() {
        super.onPause();
        swipeRefreshLayout.setRefreshing(false);
    }
}
