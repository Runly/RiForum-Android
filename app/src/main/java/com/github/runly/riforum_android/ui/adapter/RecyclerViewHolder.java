package com.github.runly.riforum_android.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.runly.riforum_android.R;

/**
 * Created by ranly on 17-2-7.
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    private final TextView mItemTextView;

    public RecyclerViewHolder(final View parent, TextView itemTextView) {
        super(parent);
        mItemTextView = itemTextView;
    }

    public static RecyclerViewHolder newInstance(View parent) {
        TextView itemTextView = (TextView) parent.findViewById(R.id.itemTextView);
        return new RecyclerViewHolder(parent, itemTextView);
    }

    public void setItemText(CharSequence text) {
        mItemTextView.setText(text);
    }
}
