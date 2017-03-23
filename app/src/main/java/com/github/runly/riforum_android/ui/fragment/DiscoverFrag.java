package com.github.runly.riforum_android.ui.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.App;
import com.github.runly.riforum_android.model.Entry;
import com.github.runly.riforum_android.model.ModelBase;
import com.github.runly.riforum_android.retrofit.RetrofitFactory;
import com.github.runly.riforum_android.ui.activity.SearchActivity;
import com.github.runly.riforum_android.ui.adapter.HistoryAdapter;
import com.github.runly.riforum_android.ui.adapter.HotAdapter;
import com.github.runly.riforum_android.ui.view.MyDecoration;
import com.github.runly.riforum_android.utils.GoToActivity;
import com.github.runly.riforum_android.utils.SdCardUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.github.runly.riforum_android.R.id.recyclerView;

/**
 * Created by ranly on 17-2-7.
 */

public class DiscoverFrag extends Fragment {
	private RecyclerView hotRecyclerView;
	private RecyclerView historyRecyclerView;

	public static DiscoverFrag createInstance() {
		return new DiscoverFrag();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_discover, container, false);
		hotRecyclerView = (RecyclerView) view.findViewById(R.id.hot_recyclerView);
		setupHotRecyclerView(hotRecyclerView);
		historyRecyclerView = (RecyclerView) view.findViewById(R.id.history_recyclerView);
		historyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		View footer = inflater.inflate(R.layout.recycler_history_footer, historyRecyclerView, false);
		setupHistoryRecyclerView(historyRecyclerView, footer);
		EditText editText = (EditText) view.findViewById(R.id.search_edit_text);
		editText.setKeyListener(null);
		editText.setOnClickListener(v -> {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				Intent intent = new Intent(getActivity(), SearchActivity.class);
				ActivityOptions activityOptions =
					ActivityOptions.makeSceneTransitionAnimation(
						getActivity(),
						view.findViewById(R.id.linear),
						getString(R.string.search_bar_share)
					);
				getActivity().startActivity(intent, activityOptions.toBundle());
			} else {
				GoToActivity.goTo(getActivity(), SearchActivity.class);
			}
		});

		fetchData();

		return view;
	}

	private void setupHotRecyclerView(RecyclerView recyclerView) {
		ChipsLayoutManager chipsLayoutManager = ChipsLayoutManager.newBuilder(getActivity())
			.setChildGravity(Gravity.CENTER)
			.setMaxViewsInRow(4)
			.setOrientation(ChipsLayoutManager.HORIZONTAL)
			.setRowStrategy(ChipsLayoutManager.STRATEGY_FILL_VIEW)
			.withLastRow(false)
			.build();

		recyclerView.setLayoutManager(chipsLayoutManager);
		HotAdapter hotAdapter = new HotAdapter(getContext(), new ArrayList<>());
		recyclerView.setAdapter(hotAdapter);
		recyclerView.addItemDecoration(new MyDecoration(getContext(), 8, 8, 8, 8, true));
	}

	private void setupHistoryRecyclerView(RecyclerView recyclerView, View footer) {
		HistoryAdapter recyclerAdapter = new HistoryAdapter(getActivity(), new ArrayList<>());
		List<ModelBase> list = recyclerAdapter.getItemList();
		list.addAll(App.getInstance().getHistoryList());
		recyclerAdapter.notifyDataSetChanged();

		Button button = (Button) footer.findViewById(R.id.clear_history);
		button.setOnClickListener(v -> {
			recyclerAdapter.getItemList().clear();
			recyclerAdapter.notifyDataSetChanged();
			SdCardUtil.removeHistory(getActivity());
		});

		recyclerAdapter.setFooterView(footer);
		recyclerView.setAdapter(recyclerAdapter);
		recyclerView.addItemDecoration(new MyDecoration(getContext(), 0, 1, 0, 0, true));
	}

	private void fetchData() {
		RetrofitFactory.getInstance()
			.getEntryService()
			.search_recommend()
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(respone -> {
				if ("1".equals(respone.code)) {
					HotAdapter hotAdapter = ((HotAdapter) hotRecyclerView.getAdapter());
					List<Entry> list = hotAdapter.getItemList();
					list.clear();
					list.addAll(respone.data);
					hotAdapter.notifyDataSetChanged();
				}
			});
	}

	@Override
	public void onResume() {
		super.onResume();
		List<ModelBase> list = ((HistoryAdapter) historyRecyclerView.getAdapter()).getItemList();
		list.clear();
		list.addAll(App.getInstance().getHistoryList());
		historyRecyclerView.getAdapter().notifyDataSetChanged();
	}
}
