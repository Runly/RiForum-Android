package com.github.runly.riforum_android.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.Constants;
import com.github.runly.riforum_android.model.ModelBase;
import com.github.runly.riforum_android.retrofit.RetrofitFactory;
import com.github.runly.riforum_android.ui.adapter.SearchAdapter;
import com.github.runly.riforum_android.ui.view.MyDecoration;
import com.github.runly.riforum_android.utils.MyOnGlobalLayoutListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ranly on 17-3-22.
 */

public class SearchActivity extends BaseActivity {
	private RecyclerView recyclerView;
	private boolean isSoftKeyBoardShow = false;
	private String content = "";

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		init();
	}

	private void init() {
		final View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
		MyOnGlobalLayoutListener listener =
			new MyOnGlobalLayoutListener(rootView, isShow -> isSoftKeyBoardShow = isShow);
		rootView.getViewTreeObserver().addOnGlobalLayoutListener(listener);

		findViewById(R.id.search_root).setOnClickListener(v -> {
			closeSoftKeyBoard();
			new Handler().postDelayed(this::onBackPressed, 300);
		});

		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_ctrl_height);
		ViewGroup.LayoutParams params = linearLayout.getLayoutParams();
		params.height = Constants.SCREEN_HEIGHT / 2;
		linearLayout.setLayoutParams(params);

		recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		setupRecyclerView(recyclerView);

		EditText editText = (EditText) findViewById(R.id.search_edit_text);
		editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				content = s.toString();
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (TextUtils.isEmpty(content)) {
					((SearchAdapter) recyclerView.getAdapter()).getItemList().clear();
					recyclerView.getAdapter().notifyDataSetChanged();
				} else {
					fetchData(content);
					((SearchAdapter) recyclerView.getAdapter()).setSearchContent(content);
				}


			}
		});

		ImageView toSearch = (ImageView) findViewById(R.id.to_search);
		toSearch.setOnClickListener(v -> {
		});

	}


	private void setupRecyclerView(RecyclerView recyclerView) {
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		SearchAdapter searchAdapter = new SearchAdapter(this, new ArrayList<>());
		recyclerView.setAdapter(searchAdapter);
		recyclerView.setHasFixedSize(true);
		recyclerView.addItemDecoration(new MyDecoration(this, 0, 1, 0, 0, true));
//		LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
//		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//			@Override
//			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//				super.onScrollStateChanged(recyclerView, newState);
//			}
//
//			@Override
//			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//				super.onScrolled(recyclerView, dx, dy);
//
//				int itemCount = lm.getItemCount();
//				int currentPosition = lm.findFirstVisibleItemPosition();
//				if (currentPosition >= itemCount / 2) {
//					if (!isFetching) {
//						List<Entry> list = ((EntriesAdapter) recyclerView.getAdapter()).getItemList();
//						if (list.size() > 0) {
//							Entry lastEntry = list.get(list.size() - 1);
//							fetchData(true, lastEntry.time);
//						}
//					}
//				}
//			}
//		});
	}

	private void fetchData(String content) {
//		if ("end".equals(message) || isFetching) {
//			return;
//		}

		if (TextUtils.isEmpty(content)) {
			return;
		}

		Map<String, Object> map = new HashMap<>();
		map.put("content", content);

//		isFetching = true;

		RetrofitFactory.getInstance()
			.getEntryService()
			.search(map)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(response -> {
				if ("1".equals(response.code)) {
					List<ModelBase> list = ((SearchAdapter) recyclerView.getAdapter()).getItemList();
//					if (!isMore) {
//						list.clear();
//					}
					list.clear();
					list.addAll(response.user_list);
					list.addAll(response.entry_list);
					recyclerView.getAdapter().notifyDataSetChanged();
//					message = response.message;
				}
//				isFetching = false;
			}, throwable -> {
				throwable.printStackTrace();
//				isFetching = false;
			});
	}


	private void closeSoftKeyBoard() {
		if (isSoftKeyBoardShow) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(0, InputMethodManager.RESULT_HIDDEN);
		}
	}

}
