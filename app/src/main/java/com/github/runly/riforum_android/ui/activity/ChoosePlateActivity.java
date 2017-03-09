package com.github.runly.riforum_android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.model.Plate;
import com.github.runly.riforum_android.retrofit.RetrofitFactory;
import com.github.runly.riforum_android.ui.adapter.ChooseRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.R.attr.dependency;

/**
 * Created by ranly on 17-2-9.
 */

public class ChoosePlateActivity extends TopBarActivity {
    private List<Plate> itemDataList = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_choose_plate);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setHasFixedSize(true);
        setupRecyclerView(recyclerView);
        fetchData();
    }

    private void fetchData() {
        RetrofitFactory.getInstance().getEntryService().plate()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if ("1".equals(response.code)) {
                        itemDataList = ((ChooseRecyclerAdapter) recyclerView
                                .getAdapter()).getItemList();
                        itemDataList.clear();
                        itemDataList.addAll(response.data);
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }, Throwable::printStackTrace);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        ChooseRecyclerAdapter recyclerAdapter = new ChooseRecyclerAdapter(this, itemDataList);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    @Override
    protected void onStart() {
        super.onStart();
        topBar.getTxtCenter().setText(getString(R.string.choose_plate_txt_left));
    }
}
