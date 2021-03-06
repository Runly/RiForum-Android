package com.github.runly.riforum_android.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.Constants;
import com.github.runly.riforum_android.model.Plate;
import com.github.runly.riforum_android.ui.activity.ChoosePlateActivity;
import com.github.runly.riforum_android.ui.activity.EntriesOfPlateActivity;
import com.github.runly.riforum_android.ui.activity.MainActivity;
import com.github.runly.riforum_android.ui.activity.ReleaseActivity;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by ranly on 17-2-9.
 */

public class ChoosePlateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private Context mContext;
	private List<Plate> mItemList;

	public ChoosePlateAdapter(Context context, List<Plate> itemList) {
		this.mItemList = itemList;
		this.mContext = context;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_plate_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
		if (mItemList.size() <= 0) {
			return;
		}

		ViewHolder holder = (ViewHolder) viewHolder;
		Plate plate = mItemList.get(position);
		if (plate != null) {
			String itemText = plate.name;
			holder.plateName.setText(itemText);
			String iconUrl = plate.icon;
			Glide.with(mContext)
				.load(iconUrl)
				.crossFade()
				.into(holder.plateIcon);

			if (mContext instanceof MainActivity) {
				View.OnClickListener onClickListener = v -> {
					Intent intent = new Intent(mContext, EntriesOfPlateActivity.class);
					intent.putExtra(Constants.INTENT_PLATE_DATA, plate);
					mContext.startActivity(intent);
				};
				holder.viewWeakReference.get().setOnClickListener(onClickListener);
			} else if (mContext instanceof ChoosePlateActivity) {
				View.OnClickListener onClickListener = v -> {
					Intent intent = new Intent(mContext, ReleaseActivity.class);
					intent.putExtra(Constants.INTENT_PLATE_DATA, plate);
					mContext.startActivity(intent);
					((Activity) mContext).finish();
				};
				holder.viewWeakReference.get().setOnClickListener(onClickListener);
			}
		}
	}

	@Override
	public int getItemCount() {
		return mItemList == null ? 0 : mItemList.size();
	}

	public List<Plate> getItemList() {
		return mItemList;
	}

	private class ViewHolder extends RecyclerView.ViewHolder {
		WeakReference<View> viewWeakReference;
		TextView plateName;
		ImageView plateIcon;

		ViewHolder(final View parent) {
			super(parent);
			plateName = (TextView) parent.findViewById(R.id.plate_name);
			plateIcon = (ImageView) parent.findViewById(R.id.plate_icon);
			viewWeakReference = new WeakReference<>(parent);
		}
	}
}
