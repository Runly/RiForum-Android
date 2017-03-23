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
import com.github.runly.riforum_android.ui.activity.SearchActivity;
import com.github.runly.riforum_android.ui.adapter.HistoryAdapter;
import com.github.runly.riforum_android.ui.adapter.RecyclerAdapter;
import com.github.runly.riforum_android.ui.view.MyDecoration;
import com.github.runly.riforum_android.utils.GoToActivity;

import java.util.ArrayList;
import java.util.List;

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
						"sharedView"
					);
				getActivity().startActivity(intent, activityOptions.toBundle());
			} else {
				GoToActivity.goTo(getActivity(), SearchActivity.class);
			}
		});
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
		RecyclerAdapter recyclerAdapter = new RecyclerAdapter(createItemList());
		recyclerView.setAdapter(recyclerAdapter);
		recyclerView.addItemDecoration(new MyDecoration(getContext(), 8, 8 , 8 , 8, true));
	}

	private void setupHistoryRecyclerView(RecyclerView recyclerView, View footer) {
		HistoryAdapter recyclerAdapter = new HistoryAdapter(getActivity(), createItemList());
		Button button = (Button) footer.findViewById(R.id.clear_history);
		button.setOnClickListener(v -> {
			recyclerAdapter.getItemList().clear();
			recyclerAdapter.notifyDataSetChanged();
		});

		recyclerAdapter.setFooterView(footer);
		recyclerView.setAdapter(recyclerAdapter);
		recyclerView.addItemDecoration(new MyDecoration(getContext(), 0, 1 , 0 , 0, true));
	}

	private List<String> createItemList() {
		List<String> itemList = new ArrayList<>();
		String[] a = {"乐天", "主播炸了", "阴阳师",  "暴走大事件", "三生三世十里桃花", "主播真会玩", "斗破苍穹"};
		for (int i = 0; i <a.length; i++) {
			itemList.add(a[i]);
		}
		return itemList;
	}
}
