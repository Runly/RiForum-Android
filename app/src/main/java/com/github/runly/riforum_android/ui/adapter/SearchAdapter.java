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
import com.github.runly.riforum_android.application.Constants;
import com.github.runly.riforum_android.model.Entry;
import com.github.runly.riforum_android.model.ModelBase;
import com.github.runly.riforum_android.model.User;
import com.github.runly.riforum_android.ui.activity.DetailActivity;
import com.github.runly.riforum_android.ui.activity.UserDetailActivity;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by ranly on 17-3-20.
 */

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static final int TYPE_HEADER = 0;  //Header
	private static final int TYPE_FOOTER = 1;  //Footer
	private static final int TYPE_NORMAL = 2;  //Normal

	private Context mContext;
	private String searchContent;


	private List<ModelBase> mItemList;
	private View mHeaderView = null;
	private View mFooterView = null;

	public void setHeaderView(View headerView) {
		mHeaderView = headerView;
		notifyItemInserted(0);
	}

	public void setFooterView(View footerView) {
		mFooterView = footerView;
		notifyItemInserted(getItemCount() - 1);
	}

	public SearchAdapter(Context context, List<ModelBase> itemList) {
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
			.inflate(R.layout.recycler_search_item, parent, false);

		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
		if (getItemViewType(position) == TYPE_NORMAL) {
			ViewHolder holder = (ViewHolder) viewHolder;
			ModelBase itemData;

			if (mHeaderView == null) {
				itemData = mItemList.get(position);
			} else {
				itemData = mItemList.get(position - 1);
			}

			if (itemData == null) {
				return;
			}

			if (itemData instanceof User) {
				User user = (User) itemData;
				holder.userOrEntry.setImageResource(R.mipmap.user);
				holder.nameOrTitle.setText(user.name);
				holder.viewWeakReference.get().setOnClickListener(v -> {
					Intent intent = new Intent(mContext, UserDetailActivity.class);
					intent.putExtra(Constants.INTENT_USER_DATA, user);
					mContext.startActivity(intent);
				});
			}

			if (itemData instanceof Entry) {
				Entry entry = (Entry) itemData;
				holder.userOrEntry.setImageResource(R.mipmap.entry);
				holder.nameOrTitle.setText(entry.title);
				holder.viewWeakReference.get().setOnClickListener(v -> {
					Intent intent = new Intent(mContext, DetailActivity.class);
					intent.putExtra(Constants.INTENT_ENTRY_DATA, entry);
					mContext.startActivity(intent);
				});
			}
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

	public List<ModelBase> getItemList() {
		return mItemList;
	}

	public void setSearchContent(String searchContent) {
		this.searchContent = searchContent;
	}

	//在这里面加载ListView中的每个item的布局
	private class ViewHolder extends RecyclerView.ViewHolder {
		WeakReference<View> viewWeakReference;
		ImageView userOrEntry;
		TextView nameOrTitle;

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
			userOrEntry = (ImageView) itemView.findViewById(R.id.user_or_entry);
			nameOrTitle = (TextView) itemView.findViewById(R.id.name_or_title);
		}
	}

}
