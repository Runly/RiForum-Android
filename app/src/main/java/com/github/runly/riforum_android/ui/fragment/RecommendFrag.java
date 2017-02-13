package com.github.runly.riforum_android.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.ui.adapter.RecommendAdapter;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ranly on 17-2-7.
 */

public class RecommendFrag extends Fragment {

    public final static String ITEMS_COUNT_KEY = "RecommendFrag$ItemsCount";

    private Banner banner;

    public static RecommendFrag createInstance(int itemsCount) {
        RecommendFrag partThreeFragment = new RecommendFrag();
        Bundle bundle = new Bundle();
        bundle.putInt(ITEMS_COUNT_KEY, itemsCount);
        partThreeFragment.setArguments(bundle);
        return partThreeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(
                R.layout.fragment_rcommend, container, false);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorBase);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 500);
            }
        });

        RecyclerView recyclerView = (RecyclerView) swipeRefreshLayout.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        View header = inflater.inflate(R.layout.recycler_recommend_header, recyclerView, false);
        setupRecyclerView(recyclerView, header);

        return swipeRefreshLayout;
    }

    private void setupRecyclerView(RecyclerView recyclerView, View header) {
        RecommendAdapter recommendAdapter = new RecommendAdapter(createItemList());
        List<String> imageList = new ArrayList<>();
        imageList.add("http://51tingchewei.net/wp-content/uploads/2016/06/android-app-banner-1.jpg");
        imageList.add("http://uae.dieutek.com/wp-content/uploads/2015/08/Website-Android-Development-Banner.jpg");
        imageList.add("http://techmafia.net/uploads/images/00/00/01/2015/06/30/59a3e8.jpg");
        banner = (Banner) header.findViewById(R.id.banner);
        banner.setImageLoader(new GlideImageLoader())
            .setIndicatorGravity(BannerConfig.RIGHT)
            .setImages(imageList)
            .start();
        recommendAdapter.setHeaderView(header);
        recyclerView.setAdapter(recommendAdapter);
    }

    private List<String> createItemList() {
        List<String> itemList = new ArrayList<>();
        Bundle bundle = getArguments();
        if (bundle != null) {
            int itemsCount = bundle.getInt(ITEMS_COUNT_KEY);
            for (int i = 0; i < itemsCount; i++) {
                itemList.add("用户" + i);
            }
        }
        return itemList;
    }

    class GlideImageLoader extends ImageLoader {
        @Override

        public void displayImage(Context context, Object path, ImageView imageView) {
                Glide.with(getActivity())
                        .load(path)
                        .into(imageView);
        }
    }

    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
    }

    @Override
    public void onResume() {
        super.onResume();
        banner.startAutoPlay();
    }

    @Override
    public void onPause() {
        super.onPause();
        banner.stopAutoPlay();
    }
}
