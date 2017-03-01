package com.github.runly.riforum_android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.model.Entry;
import com.github.runly.riforum_android.model.User;
import com.github.runly.riforum_android.retrofit.RetrofitFactory;
import com.github.runly.riforum_android.ui.adapter.EntriesAdapter;
import com.github.runly.riforum_android.utils.GoToActivity;
import com.github.runly.riforum_android.utils.UnitConvert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ranly on 17-2-27.
 */

public class UserDetailActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private TextView numText;
    private TextView name;
    private ImageView gender;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        user = (User) getIntent().getSerializableExtra("user_data");
        init();
    }

    private void init() {
        ImageView imageView = (ImageView) findViewById(R.id.toolbar_back);
        imageView.setOnClickListener(v -> finish());

        TextView userInfoText = (TextView) findViewById(R.id.user_info);
        userInfoText.setOnClickListener(v -> GoToActivity.goTo(this, UserInfoActivity.class));

        numText = (TextView) findViewById(R.id.num);

        if (null != user) {
            CircleImageView avatar = (CircleImageView) findViewById(R.id.user_detail_avatar);
            Glide.with(this)
                    .load(user.avatar)
                    .crossFade()
                    .into(avatar);

            name = (TextView) findViewById(R.id.user_detail_name);
            name.setText(user.name);

            gender = (ImageView) findViewById(R.id.gender);
            if (0 == user.gender) {
                gender.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.male));
            } else {
                gender.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.female));
            }

        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupRecyclerView(recyclerView);
        fetchDta();
    }


    private void fetchDta() {
        Map<String, Object> map = new HashMap<>();
        if (user != null) {
            map.put("uid", user.id);
        } else {
            return;
        }
        map.put("page", System.currentTimeMillis());
        RetrofitFactory.getInstance().getEntryService().user_release(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if ("1".equals(response.code)) {
                        List<Entry> list = ((EntriesAdapter) recyclerView.getAdapter()).getItemList();
                        list.clear();
                        list.addAll(response.data);
                        recyclerView.getAdapter().notifyDataSetChanged();
                        numText.append(String.valueOf(list.size()));
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                });
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        EntriesAdapter entriesAdapter = new EntriesAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(entriesAdapter);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        gender.setTranslationX(name.getWidth()/2 + UnitConvert.dipToPixels(this, 16));
    }
}
