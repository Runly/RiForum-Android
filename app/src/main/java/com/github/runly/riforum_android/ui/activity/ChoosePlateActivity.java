package com.github.runly.riforum_android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.ui.adapter.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ranly on 17-2-9.
 */

public class ChoosePlateActivity extends TopBarActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_choose_plate);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        setupRecyclerView(recyclerView);

    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChoosePlateActivity.this, ReleaseActivity.class);
                startActivity(intent);
            }
        };
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(createItemList(), onClickListener);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private List<String> createItemList() {
        List<String> itemList = new ArrayList<>();
            int itemsCount = 30;
            for (int i = 0; i < itemsCount; i++) {
                itemList.add("Item " + i);
            }
        return itemList;
    }


    @Override
    protected void onResume() {
        super.onResume();
        topBar.getTxtLeft().setText(getString(R.string.choose_plate_txt_left));
    }
}
