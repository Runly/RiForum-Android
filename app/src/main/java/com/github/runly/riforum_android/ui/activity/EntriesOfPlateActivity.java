package com.github.runly.riforum_android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
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
import com.github.runly.riforum_android.retrofit.RetrofitFactory;
import com.github.runly.riforum_android.ui.adapter.EntriesAdapter;
import com.github.runly.riforum_android.ui.view.MyDecoration;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enties_of_plate);

        plate = (Plate) getIntent().getSerializableExtra(Constants.INTENT_PLATE_DATA);

        init();
        fetchDta();
    }

    private void init() {
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
    }

    private void setupRecyclerView(RecyclerView recyclerView, View header) {

        EntriesAdapter adapter = new EntriesAdapter(this, new ArrayList<>());
        adapter.setHeaderView(header);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new MyDecoration(this, 8, 8, 8, 0, false));
        recyclerView.setAdapter(adapter);

    }

    private void fetchDta() {
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
                    entriesNum.setText(String.format(getString(R.string.release_num), list.size()));
                }
            }, Throwable::printStackTrace);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (plate != null) {
            topBar.getTxtCenter().setText(plate.name);
        }
    }
}
