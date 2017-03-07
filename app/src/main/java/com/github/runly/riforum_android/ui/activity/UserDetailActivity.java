package com.github.runly.riforum_android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.Constants;
import com.github.runly.riforum_android.model.Entry;
import com.github.runly.riforum_android.model.User;
import com.github.runly.riforum_android.retrofit.RetrofitFactory;
import com.github.runly.riforum_android.ui.adapter.EntriesAdapter;
import com.github.runly.riforum_android.ui.view.MyDecoration;
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
    private TextView nameText;
    private ImageView genderImg;
    private User user;
    private CircleImageView avatar;

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
        numText = (TextView) findViewById(R.id.num);

        if (null != user) {
            TextView userInfoText = (TextView) findViewById(R.id.user_info);
            userInfoText.setOnClickListener(v -> goToUserInfoAct());

            avatar = (CircleImageView) findViewById(R.id.user_detail_avatar);
            avatar.setOnClickListener(v -> goToUserInfoAct());
            if (!TextUtils.isEmpty(user.avatar)) {
                String avatarUrl = user.avatar + "?imageView2/1/w/" +
                        UnitConvert.dipToPixels(this, Constants.USER_INFO_AVATAR_SIZE) + "/h/" +
                        UnitConvert.dipToPixels(this, Constants.USER_INFO_AVATAR_SIZE) + "/format/webp";
                Glide.with(this)
                        .load(avatarUrl)
                        .crossFade()
                        .into(avatar);
            }

            nameText = (TextView) findViewById(R.id.user_detail_name);
            nameText.setText(user.name);

            genderImg = (ImageView) findViewById(R.id.gender);
            setGenderImgSrc(user.gender);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupRecyclerView(recyclerView);
        fetchDta();
    }

    private void goToUserInfoAct() {
        Intent intent = new Intent(this, UserInfoActivity.class);
        intent.putExtra("user_data", user);
        startActivityForResult(intent, Constants.START_USER_INFO);
    }

    private void setGenderImgSrc(int gender) {
        if (0 == gender) {
            genderImg.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.male));
        } else if (1 == gender){
            genderImg.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.female));
        } else {
            genderImg.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.no_gender));
        }
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
                }, Throwable::printStackTrace);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        EntriesAdapter entriesAdapter = new EntriesAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(entriesAdapter);
        recyclerView.addItemDecoration(new MyDecoration(this, 8));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == Constants.START_USER_INFO) {
                user = (User) data.getSerializableExtra(Constants.INTENT_USER_DATA);
                if (user != null) {
                    nameText.setText(user.name);
                    setGenderImgSrc(user.gender);
                    if (!TextUtils.isEmpty(user.avatar)) {
                        String avatarUrl = user.avatar + "?imageView2/1/w/" +
                                UnitConvert.dipToPixels(this, Constants.USER_INFO_AVATAR_SIZE) + "/h/" +
                                UnitConvert.dipToPixels(this, Constants.USER_INFO_AVATAR_SIZE) + "/format/webp";
                        Glide.with(this)
                                .load(avatarUrl)
                                .crossFade()
                                .into(avatar);
                    }
                }
            }

        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        genderImg.setTranslationX(nameText.getWidth()/2 + UnitConvert.dipToPixels(this, 16));
    }
}
