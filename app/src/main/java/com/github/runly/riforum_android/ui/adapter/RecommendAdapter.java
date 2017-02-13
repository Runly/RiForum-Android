package com.github.runly.riforum_android.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.runly.riforum_android.R;

import java.util.List;

/**
 * Created by ranly on 17-2-13.
 */

public class RecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;  //说明是带有Header的
    private static final int TYPE_FOOTER = 1;  //说明是带有Footer的
    private static final int TYPE_NORMAL = 2;  //说明是不带有header和footer的


    private List<String> mItemList;
    private View.OnClickListener onClickListener;
    private View mHeaderView = null;
    private View mFooterView = null;

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public void setFooterView(View footerView) {
        mFooterView = footerView;
        notifyItemInserted(getItemCount()-1);
    }

    public RecommendAdapter(List<String> itemList) {
        this.mItemList = itemList;
    }

    public RecommendAdapter(List<String> itemList, View.OnClickListener onClickListener) {
        this.mItemList = itemList;
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mHeaderView != null){
            //第一个item应该加载Header
            return TYPE_HEADER;
        }

        if (position == getItemCount()-1 && mFooterView != null){
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

        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_entry_item, parent, false);
        if (onClickListener != null) {
            view.setOnClickListener(onClickListener);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            ViewHolder holder = (ViewHolder) viewHolder;
            String itemText;
            if (mHeaderView == null) {
                itemText = mItemList.get(position);
            } else {
                itemText = mItemList.get(position - 1);
            }
            holder.setItemText(itemText);
        }
    }

    @Override
    public int getItemCount() {
        if(mHeaderView == null && mFooterView == null){
            return mItemList == null ? 0 : mItemList.size();
        }

        //此时mHeaderView和mFooterView必有一个不为null
        if(mHeaderView == null || mFooterView == null){
            return mItemList == null ? 1 : mItemList.size() + 1;
        }

        return mItemList == null ? 2 : mItemList.size() + 2;
    }

    //在这里面加载ListView中的每个item的布局
    private class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mItemTextView;
        ViewHolder(View itemView) {
            super(itemView);
            //如果是headerView或者是footerView,直接返回
            if (itemView == mHeaderView){
                return;
            }
            if (itemView == mFooterView){
                return;
            }
            mItemTextView = (TextView) itemView.findViewById(R.id.user_name);
        }

        void setItemText(String text) {
            mItemTextView.setText(text);
        }
    }
}
