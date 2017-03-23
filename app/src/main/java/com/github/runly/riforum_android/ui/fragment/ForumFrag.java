package com.github.runly.riforum_android.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.App;
import com.github.runly.riforum_android.model.Entry;
import com.github.runly.riforum_android.model.Plate;
import com.github.runly.riforum_android.retrofit.RetrofitFactory;
import com.github.runly.riforum_android.ui.activity.MainActivity;
import com.github.runly.riforum_android.ui.adapter.ChoosePlateAdapter;
import com.github.runly.riforum_android.ui.adapter.ForumAdapter;
import com.github.runly.riforum_android.ui.view.MarginDecoration;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.github.runly.riforum_android.R.id.recyclerView;

/**
 * Created by ranly on 17-2-7.
 */

public class ForumFrag extends Fragment {
	private RecyclerView entryRecyclerView;
	private RecyclerView plateRecyclerView;
	private SwipeRefreshLayout swipeRefreshLayout;


	public static ForumFrag createInstance() {
		return new ForumFrag();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		swipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(
			R.layout.fragment_forum, container, false);
		swipeRefreshLayout.setColorSchemeResources(R.color.color_base);
		swipeRefreshLayout.setOnRefreshListener(this::fetchPlate);

		entryRecyclerView = (RecyclerView) swipeRefreshLayout.findViewById(recyclerView);
		GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
		entryRecyclerView.setLayoutManager(manager);
		View header = inflater.inflate(R.layout.recycler_forum_header, entryRecyclerView, false);
		setupHeader(header);
		setupRecyclerView(entryRecyclerView, header, manager);
		fetchPlate();

		((MainActivity) getActivity())
			.getViewPager()
			.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
				@Override
				public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

				}

				@Override
				public void onPageSelected(int position) {
					if (position == 1) {
						((MainActivity) getActivity())
							.getTopBar()
							.setOnClickListener(v -> {
								int currentPosition = manager.findFirstVisibleItemPosition();
								if (currentPosition > 25) {
									manager.scrollToPosition(25);
								}
								manager.smoothScrollToPosition(entryRecyclerView, null, 0);
							});
					}
				}

				@Override
				public void onPageScrollStateChanged(int state) {

				}
			});

		return swipeRefreshLayout;
	}

	private void setupHeader(View header) {
		plateRecyclerView = (RecyclerView) header.findViewById(recyclerView);
		GridLayoutManager manager = new GridLayoutManager(getActivity(), 4);
		plateRecyclerView.setLayoutManager(manager);
		ChoosePlateAdapter adapter = new ChoosePlateAdapter(getActivity(), new ArrayList<>());
//        plateRecyclerView.addItemDecoration(new MyDecoration(getActivity(), 8, 16, 16, 8, true));
		plateRecyclerView.setHasFixedSize(true);
		plateRecyclerView.setAdapter(adapter);
	}

	private void setupRecyclerView(RecyclerView recyclerView, View header, GridLayoutManager manager) {
		ForumAdapter forumAdapter = new ForumAdapter(getActivity(), new ArrayList<>());
		forumAdapter.setHeaderView(header);
		recyclerView.addItemDecoration(new MarginDecoration(getActivity(), 4, 8, 4, 8));
		recyclerView.setHasFixedSize(true);
		recyclerView.setAdapter(forumAdapter);
		manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
			@Override
			public int getSpanSize(int position) {
				if (position % 5 == 1 || forumAdapter.isHeader(position)) {
					return manager.getSpanCount();
				} else {
					return 1;
				}
			}
		});
	}

	private void fetchPlate() {
		swipeRefreshLayout.setRefreshing(true);
		RetrofitFactory.getInstance().getEntryService().plate()
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(response -> {
				if ("1".equals(response.code)) {
					fetchData();
					List<Plate> itemDataList =
						((ChoosePlateAdapter) plateRecyclerView.getAdapter()).getItemList();
					itemDataList.clear();
					itemDataList.addAll(response.data);
					plateRecyclerView.getAdapter().notifyDataSetChanged();
					App.getInstance().setPlateList(itemDataList);
				}
			}, throwable -> {
				throwable.printStackTrace();
				swipeRefreshLayout.setRefreshing(false);
			});
	}

	private void fetchData() {
		RetrofitFactory.getInstance()
			.getEntryService()
			.all_plate_entries()
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(response -> {
				if ("1".equals(response.code)) {
					List<Entry> list = ((ForumAdapter) entryRecyclerView.getAdapter()).getItemList();
					list.clear();
					list.addAll(response.data);
					entryRecyclerView.getAdapter().notifyDataSetChanged();
				}
				swipeRefreshLayout.setRefreshing(false);
			}, throwable -> {
				throwable.printStackTrace();
				swipeRefreshLayout.setRefreshing(false);
			});
	}

	@Override
	public void onPause() {
		super.onPause();
		swipeRefreshLayout.setRefreshing(false);
	}
}
