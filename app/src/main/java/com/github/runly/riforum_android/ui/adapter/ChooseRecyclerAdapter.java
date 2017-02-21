package com.github.runly.riforum_android.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.model.Plate;
import com.github.runly.riforum_android.ui.activity.ReleaseActivity;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by ranly on 17-2-9.
 */

public class ChooseRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context mContext;
    private List<Plate> mItemList;

    public ChooseRecyclerAdapter(Context context, List<Plate> itemList) {
        this.mItemList = itemList;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_choose_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        String itemText = mItemList.get(position).name;
        holder.mItemTextView.setText(itemText);
        View.OnClickListener onClickListener = v -> {
            Intent intent = new Intent(mContext, ReleaseActivity.class);
            intent.putExtra("item_data", mItemList.get(position));
            mContext.startActivity(intent);
            ((Activity)mContext).finish();
        };
        holder.viewWeakReference.get().setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    public List<Plate> getItemList() {
        return mItemList;
    }

    private class ViewHolder extends RecyclerView.ViewHolder{
        WeakReference<View> viewWeakReference;
        TextView mItemTextView;

        public ViewHolder(final View parent) {
            super(parent);
            mItemTextView = (TextView) parent.findViewById(R.id.itemTextView);
            viewWeakReference = new WeakReference<>(parent);
        }
    }
}
