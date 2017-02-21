package com.github.runly.riforum_android.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.runly.richedittext.RichEditText;
import com.github.runly.richedittext.span.FakeImageSpan;
import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.model.Entry;
import com.github.runly.riforum_android.model.User;
import com.github.runly.riforum_android.ui.adapter.DetailAdapter;
import com.github.runly.riforum_android.ui.adapter.RecyclerAdapter;
import com.github.runly.riforum_android.ui.view.CircularImageView;
import com.github.runly.riforum_android.ui.view.TopBar;
import com.github.runly.riforum_android.utils.Constant;
import com.github.runly.riforum_android.utils.TxtUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ranly on 17-2-20.
 */

public class DetailActivity extends BaseActivity {
    private Entry entry;
    private User user;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        init();
    }

    private void init() {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        coordinatorLayout.setPadding(0, Constant.STATUS_HEIGHT, 0, 0);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.detail_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        View header = getLayoutInflater().inflate(R.layout.recycler_detail_header, recyclerView, false);
        setupRecyclerView(recyclerView, header);

        entry = (Entry) getIntent().getSerializableExtra("item_data");
        if (entry != null) {
            user = entry.user;
        }

        TopBar topBar = (TopBar) findViewById(R.id.detail_top_bar);
        topBar.getImgLeft().setOnClickListener(v -> finish());

        CircularImageView userAvatar = (CircularImageView) header.findViewById(R.id.detail_user_avatar);
        RichEditText richEditText = (RichEditText) header.findViewById(R.id.detail_content);
        TextView titleTV = (TextView) header.findViewById(R.id.detail_title);
        TextView timeTV = (TextView) header.findViewById(R.id.detail_time);
        TextView userTV = (TextView) header.findViewById(R.id.detail_user_name);
        TextView readNum = (TextView) header.findViewById(R.id.read_number);
        TextView commentNum = (TextView) header.findViewById(R.id.comment_number);
        TextView plate = (TextView) header.findViewById(R.id.detail_plate);

        richEditText.setKeyListener(null);
        if (null != entry) {
            topBar.getTxtCenter().setText(entry.title);
            setRichTextContent(entry.content, richEditText);
            titleTV.setText(entry.title);
            timeTV.setText(TxtUtils.getReadableTime(String.valueOf(entry.time)));
            readNum.setText(String.valueOf(entry.read_num));
            commentNum.setText(String.valueOf(entry.comment_num));
            plate.setText(TxtUtils.getPlateWithId(entry.plate));

            if (null != user) {
                Glide.with(this)
                        .load(user.avatar)
                        .into(userAvatar);
                userTV.setText(user.name);

            }
        }

    }

    private void setupRecyclerView(RecyclerView recyclerView, View header) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i ++) {
            list.add(i+1 + "楼");
        }
        DetailAdapter adapter = new DetailAdapter(this, list);
        adapter.setHeaderView(header);
        recyclerView.setAdapter(adapter);
    }

    private void setRichTextContent(String richText, RichEditText richEditText) {
        richEditText.setRichText(richText);
        FakeImageSpan[] imageSpans = richEditText.getFakeImageSpans();
        if (imageSpans == null || imageSpans.length == 0) {
            return;
        }
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        for (final FakeImageSpan imageSpan : imageSpans) {
            final String src = imageSpan.getValue();
            if (src.startsWith("http")) {
                // web images
                new AsyncTask<String, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(String... params) {
                        try {
                            InputStream is = new URL(src).openStream();
                            return BitmapFactory.decodeStream(is);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        if (bitmap == null) {
                            return;
                        }
                        richEditText.replaceDownloadedImage(imageSpan, bitmap, src);
                    }
                }.executeOnExecutor(executorService, src);
            } else {
                // local images
                richEditText.replaceLocalImage(imageSpan, src);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
