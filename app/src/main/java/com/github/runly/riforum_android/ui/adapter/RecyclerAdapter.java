package com.github.runly.riforum_android.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.runly.riforum_android.R;

import java.util.List;

/**
 * Created by ranly on 17-2-7.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> mItemList;
    private View.OnClickListener onClickListener;

    public RecyclerAdapter(List<String> itemList) {
        this.mItemList = itemList;
    }

    public RecyclerAdapter(List<String> itemList, View.OnClickListener onClickListener) {
        this.mItemList = itemList;
        this.onClickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        if (onClickListener != null) {
            view.setOnClickListener(onClickListener);
        }
        return RecyclerViewHolder.newInstance(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        RecyclerViewHolder holder = (RecyclerViewHolder) viewHolder;
        String itemText = mItemList.get(position);
        holder.setItemText(itemText);
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }
}
