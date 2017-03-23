package com.github.runly.riforum_android.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.App;
import com.github.runly.riforum_android.application.Constants;
import com.github.runly.riforum_android.model.Entry;
import com.github.runly.riforum_android.model.ModelBase;
import com.github.runly.riforum_android.model.User;
import com.github.runly.riforum_android.ui.activity.DetailActivity;
import com.github.runly.riforum_android.ui.activity.UserDetailActivity;
import com.github.runly.riforum_android.utils.SdCardUtil;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by ranly on 17-3-21.
 */

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private static final int TYPE_HEADER = 0;  //Header
	private static final int TYPE_FOOTER = 1;  //Footer
	private static final int TYPE_NORMAL = 2;  //Normal

	private Context mContext;


	private List<ModelBase> mItemList;
	private View mHeaderView = null;
	private View mFooterView = null;


	public List<ModelBase> getItemList() {
		return mItemList;
	}

	public void setHeaderView(View headerView) {
		mHeaderView = headerView;
		notifyItemInserted(0);
	}

	public void setFooterView(View footerView) {
		mFooterView = footerView;
		notifyItemInserted(getItemCount() - 1);
	}

	public HistoryAdapter(Context context, List<ModelBase> itemList) {
		this.mItemList = itemList;
		this.mContext = context;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0 && mHeaderView != null) {
			//第一个item应该加载Header
			return TYPE_HEADER;
		}

		if (position == getItemCount() - 1 && mFooterView != null) {
			//最后一个,应该加载Footer
			return TYPE_FOOTER;
		}

		return TYPE_NORMAL;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (mHeaderView != null && viewType == TYPE_HEADER) {
			return new ViewHolder(mHeaderView);
		}

		if (mFooterView != null && viewType == TYPE_FOOTER) {
			return new ViewHolder(mFooterView);
		}

		final View view = LayoutInflater.from(parent.getContext())
			.inflate(R.layout.recycler_history_item, parent, false);

		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
		if (mItemList.size() <= 0) {
			return;
		}

		if (getItemViewType(position) == TYPE_NORMAL) {
			ViewHolder holder = (ViewHolder) viewHolder;
			ModelBase itemData = mItemList.get(position);

			if (itemData instanceof User) {
				User user = (User) itemData;
				holder.textView.setText(user.name);
				holder.viewWeakReference.get().setOnClickListener(v -> {
					Intent intent = new Intent(mContext, UserDetailActivity.class);
					intent.putExtra(Constants.INTENT_USER_DATA, user);
					mContext.startActivity(intent);
				});
			} else {
				Entry entry = (Entry) itemData;
				holder.textView.setText(entry.title);
				holder.viewWeakReference.get().setOnClickListener(v -> {
					Intent intent = new Intent(mContext, DetailActivity.class);
					intent.putExtra(Constants.INTENT_ENTRY_DATA, entry);
					mContext.startActivity(intent);
				});
			}

			holder.clearImage.setOnClickListener(v -> {
				mItemList.remove(position);
				App.getInstance().getHistoryList().remove(position);
				notifyDataSetChanged();
				if (mItemList.size() < 1) {
					SdCardUtil.removeHistory(mContext);
				} else {
					SdCardUtil.saveHistory(mContext, mItemList);
				}
			});
		}

	}

	@Override
	public int getItemCount() {
		if (mHeaderView == null && mFooterView == null) {
			return mItemList == null ? 0 : mItemList.size();
		}

		//此时mHeaderView和mFooterView必有一个不为null
		if (mHeaderView == null || mFooterView == null) {
			return mItemList == null ? 1 : mItemList.size() + 1;
		}

		return mItemList == null ? 2 : mItemList.size() + 2;
	}

	private class ViewHolder extends RecyclerView.ViewHolder {
		WeakReference<View> viewWeakReference;
		TextView textView;
		ImageView clearImage;

		ViewHolder(View itemView) {
			super(itemView);
			//如果是headerView或者是footerView,直接返回
			if (itemView == mHeaderView) {
				return;
			}
			if (itemView == mFooterView) {
				return;
			}
			viewWeakReference = new WeakReference<>(itemView);
			textView = (TextView) itemView.findViewById(R.id.history_item_text);
			clearImage = (ImageView) itemView.findViewById(R.id.history_clear);
		}
	}

}
