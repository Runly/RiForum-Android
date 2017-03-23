package com.github.runly.riforum_android.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.Constants;
import com.github.runly.riforum_android.model.Entry;
import com.github.runly.riforum_android.retrofit.RetrofitFactory;
import com.github.runly.riforum_android.ui.activity.DetailActivity;
import com.github.runly.riforum_android.ui.activity.MainActivity;
import com.github.runly.riforum_android.ui.adapter.EntriesAdapter;
import com.github.runly.riforum_android.ui.view.MyDecoration;
import com.github.runly.riforum_android.utils.RecyclerScrollToTop;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ranly on 17-2-7.
 */

public class RecommendFrag extends Fragment {
	private RecyclerView recyclerView;
	private SwipeRefreshLayout swipeRefreshLayout;
	private boolean isFetching;
	private String message = "";
	private Banner banner;
	private List<Entry> bannerEntryList = new ArrayList<>();
	private List<String> bannerImageList = new ArrayList<>();

	public static RecommendFrag createInstance() {
		return new RecommendFrag();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		swipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(
			R.layout.fragment_rcommend, container, false);
		swipeRefreshLayout.setColorSchemeResources(R.color.color_base);
		swipeRefreshLayout.setOnRefreshListener(() -> {
			message = "";
			fetchBannerData();
			fetchData(false, System.currentTimeMillis());
		});

		recyclerView = (RecyclerView) swipeRefreshLayout.findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

		View header = inflater.inflate(R.layout.recycler_recommend_header, recyclerView, false);
		setupRecyclerView(recyclerView, header);

		swipeRefreshLayout.setRefreshing(true);
		fetchData(false, System.currentTimeMillis());

		((MainActivity) getActivity())
			.getTopBar()
			.setOnClickListener(v -> RecyclerScrollToTop.scrollToTop(recyclerView));

		((MainActivity) getActivity())
			.getViewPager()
			.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
				@Override
				public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

				}

				@Override
				public void onPageSelected(int position) {
					if (position == 0) {
						((MainActivity) getActivity())
							.getTopBar()
							.setOnClickListener(v -> RecyclerScrollToTop.scrollToTop(recyclerView));
					}
				}

				@Override
				public void onPageScrollStateChanged(int state) {

				}
			});

		return swipeRefreshLayout;
	}

	private void fetchData(boolean isMore, long page) {
		if ("end".equals(message) || isFetching) {
			return;
		}

		Map<String, Object> map = new HashMap<>();
		map.put("page", page);

		isFetching = true;

		RetrofitFactory.getInstance()
			.getEntryService()
			.recommend(map)
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

	private void setupRecyclerView(RecyclerView recyclerView, View header) {
		EntriesAdapter entriesAdapter = new EntriesAdapter(getActivity(), new ArrayList<>());
		banner = (Banner) header.findViewById(R.id.banner);
		setupBanner();
		entriesAdapter.setHeaderView(header);
		recyclerView.setAdapter(entriesAdapter);
		recyclerView.setHasFixedSize(true);
		recyclerView.addItemDecoration(new MyDecoration(getActivity(), 8, 8, 8, 0, false));
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

	private void fetchBannerData() {
		bannerImageList = new ArrayList<>();
		RetrofitFactory.getInstance()
			.getEntryService()
			.banner_entries()
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(response -> {
				if ("1".equals(response.code)) {
					bannerEntryList = response.data;
					for (Entry entry : bannerEntryList) {
						bannerImageList.add(entry.image.get(0));
					}
					banner.setImages(bannerImageList)
						.start();
				}
			}, Throwable::printStackTrace);
	}

	private void setupBanner() {
		fetchBannerData();

		banner.setImageLoader(new GlideImageLoader())
			.setIndicatorGravity(BannerConfig.RIGHT);

		banner.setOnBannerListener(position -> {
			if (bannerEntryList.size() > 0 && bannerImageList.size() > 0) {
				Intent intent = new Intent(getActivity(), DetailActivity.class);
				intent.putExtra(Constants.INTENT_ENTRY_DATA, bannerEntryList.get(position));
				getActivity().startActivity(intent);
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		swipeRefreshLayout.setRefreshing(false);
	}

	private class GlideImageLoader extends ImageLoader {
		@Override

		public void displayImage(Context context, Object path, ImageView imageView) {
			Glide.with(getActivity())
				.load(path)
				.crossFade()
				.into(imageView);
		}
	}
}
