package com.github.runly.riforum_android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.ui.view.CircularImageView;

import java.util.List;

/**
 * Created by ranly on 17-2-20.
 */

public class DetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;  //Header
    private static final int TYPE_FOOTER = 1;  //Footer
    private static final int TYPE_NORMAL = 2;  //Normal

    private Context mContext;


    private List<String> mItemList;
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

    public DetailAdapter(Context context, List<String> itemList) {
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
            return new DetailAdapter.ViewHolder(mHeaderView);
        }

        if (mFooterView != null && viewType == TYPE_FOOTER) {
            return new DetailAdapter.ViewHolder(mFooterView);
        }

        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_comment_item, parent, false);

        return new DetailAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            DetailAdapter.ViewHolder holder = (DetailAdapter.ViewHolder) viewHolder;
            String itemData;
            if (mHeaderView == null) {
                itemData = mItemList.get(position);
            } else {
                itemData = mItemList.get(position - 1);
            }
            holder.floor.setText(itemData);
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


    //在这里面加载ListView中的每个item的布局
    private class ViewHolder extends RecyclerView.ViewHolder {
        CircularImageView userAvatar;
        TextView userName;
        TextView time;
        TextView floor;
        TextView content;


        ViewHolder(View itemView) {
            super(itemView);
            //如果是headerView或者是footerView,直接返回
            if (itemView == mHeaderView) {
                return;
            }
            if (itemView == mFooterView) {
                return;
            }

            userAvatar = (CircularImageView) itemView.findViewById(R.id.comment_user_avatar);
            userName = (TextView) itemView.findViewById(R.id.comment_user_name);
            floor = (TextView) itemView.findViewById(R.id.comment_floor);
            content = (TextView) itemView.findViewById(R.id.comment_content);
            time = (TextView) itemView.findViewById(R.id.comment_time);
        }
    }
}
