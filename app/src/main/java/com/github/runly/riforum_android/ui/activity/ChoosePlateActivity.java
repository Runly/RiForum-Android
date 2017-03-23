package com.github.runly.riforum_android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.App;
import com.github.runly.riforum_android.model.Plate;
import com.github.runly.riforum_android.retrofit.RetrofitFactory;
import com.github.runly.riforum_android.ui.adapter.ChoosePlateAdapter;
import com.github.runly.riforum_android.ui.view.MyDecoration;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        initData();
    }

    private void initData() {
        List<Plate> plateList = App.getInstance().getPlateList();
        if (plateList != null && plateList.size() >= 0) {
            itemDataList = ((ChoosePlateAdapter) recyclerView.getAdapter()).getItemList();
            itemDataList.clear();
            itemDataList.addAll(plateList);
            recyclerView.getAdapter().notifyDataSetChanged();
        } else {
            fetchData();
        }
    }

    private void fetchData() {
        RetrofitFactory.getInstance().getEntryService().plate()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if ("1".equals(response.code)) {
                        itemDataList = ((ChoosePlateAdapter) recyclerView.getAdapter()).getItemList();
                        itemDataList.clear();
                        itemDataList.addAll(response.data);
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }, Throwable::printStackTrace);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        ChoosePlateAdapter recyclerAdapter = new ChoosePlateAdapter(this, itemDataList);
        recyclerView.addItemDecoration(new MyDecoration(this, 16, 8, 16, 0, true));
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        topBar.getTxtCenter().setText(getString(R.string.choose_plate_txt_left));
    }
}
