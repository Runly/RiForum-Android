package com.github.runly.riforum_android.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.model.Entry;
import com.github.runly.riforum_android.model.User;
import com.github.runly.riforum_android.ui.activity.DetailActivity;
import com.github.runly.riforum_android.ui.activity.UserDetailActivity;
import com.github.runly.riforum_android.ui.view.CircularImageView;
import com.github.runly.riforum_android.utils.TxtUtils;
import com.github.runly.riforum_android.utils.UnitConvert;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by ranly on 17-2-13.
 */

public class EntriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;  //Header
    private static final int TYPE_FOOTER = 1;  //Footer
    private static final int TYPE_NORMAL = 2;  //Normal

    private Context mContext;


    private List<Entry> mItemList;
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

    public EntriesAdapter(Context context, List<Entry> itemList) {
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
                .inflate(R.layout.recycler_entry_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            ViewHolder holder = (ViewHolder) viewHolder;
            Entry itemData;
            if (mHeaderView == null) {
                itemData = mItemList.get(position);
            } else {
                itemData = mItemList.get(position - 1);
            }
            if (null != itemData) {
                View.OnClickListener onClickListener = v -> {
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra("item_data", itemData);
                    mContext.startActivity(intent);
                };
                holder.viewWeakReference.get().setOnClickListener(onClickListener);

                User user = itemData.user;
                if (null != user) {
                    Glide.with(mContext)
                            .load(user.avatar)
                            .crossFade()
                            .into(holder.userAvatar);

                    holder.userName.setText(user.name);

                    if (mContext instanceof UserDetailActivity) {
                        holder.userAvatar.setOnClickListener(null);
                        holder.userName.setOnClickListener(null);
                    } else {
                        View.OnClickListener listener = v -> {
                            Intent intent = new Intent(mContext, UserDetailActivity.class);
                            intent.putExtra("user_data", user);
                            mContext.startActivity(intent);
                        };
                        holder.userAvatar.setOnClickListener(listener);
                        holder.userName.setOnClickListener(listener);
                    }

                }

                holder.plate.setText(TxtUtils.getPlateWithId(itemData.plate));
                holder.title.setText(itemData.title);

                for (int i = 0; i < itemData.image.size(); i++) {
                    if (i > 2)
                        break;

                    int width = UnitConvert.dipToPixels(mContext, 122);
                    int height = UnitConvert.dipToPixels(mContext, 80);

                    String url = itemData.image.get(i) +
                            "?imageView2/1/w/" + width + "/h/" + height + "/format/webp";
                    Glide.with(mContext)
                            .load(url)
                            .crossFade()
                            .into((ImageView) holder.imageLinear.getChildAt(i));

                    if (itemData.image.size() == 1 && i == 0) {
                        ((ImageView) holder.imageLinear.getChildAt(1)).setImageDrawable(null);
                        ((ImageView) holder.imageLinear.getChildAt(2)).setImageDrawable(null);
                        break;
                    }

                    if (itemData.image.size() == 2 && i == 1) {
                        ((ImageView) holder.imageLinear.getChildAt(2)).setImageDrawable(null);
                        break;
                    }
                }
                holder.time.setText(TxtUtils.getReadableTime(String.valueOf(itemData.time)));
                holder.readNum.setText(String.valueOf(itemData.read_num));
                holder.commentNum.setText(String.valueOf(itemData.comment_num));
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

    public List<Entry> getItemList() {
        return mItemList;
    }

    //在这里面加载ListView中的每个item的布局
    private class ViewHolder extends RecyclerView.ViewHolder {
        WeakReference<View> viewWeakReference;
        CircularImageView userAvatar;
        TextView userName;
        TextView plate;
        TextView title;
        LinearLayout imageLinear;
        TextView time;
        TextView readNum;
        TextView commentNum;

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
            userAvatar = (CircularImageView) itemView.findViewById(R.id.item_user_avatar);
            userName = (TextView) itemView.findViewById(R.id.item_user_name);
            plate = (TextView) itemView.findViewById(R.id.item_plate);
            title = (TextView) itemView.findViewById(R.id.item_title);
            imageLinear = (LinearLayout) itemView.findViewById(R.id.image_linear);
            time = (TextView) itemView.findViewById(R.id.item_time);
            readNum = (TextView) itemView.findViewById(R.id.read_number);
            commentNum = (TextView) itemView.findViewById(R.id.comment_number);
        }
    }
}
