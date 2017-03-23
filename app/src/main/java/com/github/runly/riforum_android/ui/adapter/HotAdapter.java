package com.github.runly.riforum_android.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.App;
import com.github.runly.riforum_android.application.Constants;
import com.github.runly.riforum_android.model.Entry;
import com.github.runly.riforum_android.model.ModelBase;
import com.github.runly.riforum_android.ui.activity.DetailActivity;
import com.github.runly.riforum_android.utils.SdCardUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.R.id.list;

/**
 * Created by ranly on 17-2-7.
 */

public class HotAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private List<Entry> mItemList;
	private Context mContext;

	public HotAdapter(Context context, List<Entry> itemList) {
		this.mItemList = itemList;
		this.mContext = context;
	}


	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_hot_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
		if (mItemList.size() <= 0) {
			return;
		}

		ViewHolder holder = (ViewHolder) viewHolder;
		Entry itemData = mItemList.get(position);
		holder.textView.setText(itemData.title);
		holder.textView.setMaxWidth(Constants.SCREEN_WIDTH / 2);
		holder.viewWeakReference.get().setOnClickListener(v -> {
			List<ModelBase> list = App.getInstance().getHistoryList();
			list.add(0, itemData);
			SdCardUtil.saveHistory(mContext, list);

			Intent intent = new Intent(mContext, DetailActivity.class);
			intent.putExtra(Constants.INTENT_ENTRY_DATA, itemData);
			mContext.startActivity(intent);
		});

	}

	public List<Entry> getItemList() {
		return mItemList;
	}

	@Override
	public int getItemCount() {
		return mItemList == null ? 0 : mItemList.size();
	}

	private class ViewHolder extends RecyclerView.ViewHolder {
		WeakReference<View> viewWeakReference;
		TextView textView;

		ViewHolder(View itemView) {
			super(itemView);
			viewWeakReference = new WeakReference<>(itemView);
			textView = (TextView) itemView.findViewById(R.id.hot_text);
		}
	}

}
