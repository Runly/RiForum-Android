package com.github.runly.riforum_android.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.Constants;
import com.github.runly.riforum_android.model.Entry;
import com.github.runly.riforum_android.model.User;
import com.github.runly.riforum_android.ui.activity.DetailActivity;
import com.github.runly.riforum_android.ui.activity.UserDetailActivity;
import com.github.runly.riforum_android.utils.PlateHeaderNumUtil;
import com.github.runly.riforum_android.utils.TxtUtils;
import com.github.runly.riforum_android.utils.UnitConvert;

import java.lang.ref.WeakReference;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ranly on 17-3-8.
 */

public class ForumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;  //Header
    private static final int TYPE_FOOTER = 1;  //Footer
    private static final int TYPE_NORMAL = 2;  //Normal
    private static final int TYPE_PLATE_HEADER = 3; // plate_id header

    private Context mContext;
    private List<Entry> mItemList;
    private View mHeaderView = null;
    private View mFooterView = null;
    private int num = 1;

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public void setFooterView(View footerView) {
        mFooterView = footerView;
        notifyItemInserted(getItemCount() - 1);
    }

    public ForumAdapter(Context context, List<Entry> itemList) {
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

        if (position % 5 == 1) {
            return TYPE_PLATE_HEADER;
        }

        return TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            return new ForumAdapter.ViewHolder(mHeaderView);
        }

        if (mFooterView != null && viewType == TYPE_FOOTER) {
            return new ForumAdapter.ViewHolder(mFooterView);
        }

        if (viewType == TYPE_PLATE_HEADER) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_plate_header, parent, false);
            return new ForumAdapter.ViewHolder(view);
        }

        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_entry_item_small, parent, false);

        return new ForumAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (mItemList.size() <= 0) {
            return;
        }

        if (getItemViewType(position) == TYPE_PLATE_HEADER) {
            ViewHolder holder = (ViewHolder) viewHolder;
            Entry entry;
            if (mHeaderView == null) {
                entry = mItemList.get(position - PlateHeaderNumUtil.getPlateHeaderNumber(position));
            } else {
                entry = mItemList.get(position - 1 - PlateHeaderNumUtil.getPlateHeaderNumber(position));
            }
            if (holder != null) {
                if (holder.plateName != null) {
                    holder.plateName.setText(entry.plate.name);
                    Glide.with(mContext)
                        .load(entry.plate.icon)
                        .crossFade()
                        .into(holder.plateIcon);
                }
            }

        }

        if (getItemViewType(position) == TYPE_NORMAL) {
            ViewHolder holder = (ViewHolder) viewHolder;
            Entry itemData;
            if (mHeaderView == null) {
                itemData = mItemList.get(position - PlateHeaderNumUtil.getPlateHeaderNumber(position));
            } else {
                itemData = mItemList.get(position - 1 - PlateHeaderNumUtil.getPlateHeaderNumber(position));
            }
            if (null != itemData) {
                if (itemData.id ==  -1) {
                    return;
                }
                View.OnClickListener onClickListener = v -> {
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra("item_data", itemData);
                    mContext.startActivity(intent);
                };
                holder.viewWeakReference.get().setOnClickListener(onClickListener);

                User user = itemData.user;
                if (null != user) {
                    String avatarUrl = user.avatar + "?imageView2/1/w/" +
                        UnitConvert.dp2Px(mContext, Constants.NORMAL_AVATAR_SIZE) + "/h/" +
                        UnitConvert.dp2Px(mContext, Constants.NORMAL_AVATAR_SIZE) + "/format/webp";
                    Glide.with(mContext)
                        .load(avatarUrl)
                        .crossFade()
                        .into(holder.userAvatar);

                    holder.userName.setText(user.name);

                    if (mContext instanceof UserDetailActivity) {
                        holder.userAvatar.setOnClickListener(null);
                        holder.userName.setOnClickListener(null);
                    } else {
                        View.OnClickListener listener = v -> {
                            Intent intent = new Intent(mContext, UserDetailActivity.class);
                            intent.putExtra(Constants.INTENT_USER_DATA, user);
                            mContext.startActivity(intent);
                        };
                        holder.userAvatar.setOnClickListener(listener);
                        holder.userName.setOnClickListener(listener);
                    }

                }

//                holder.plate_id.setText(TxtUtils.getPlateNameWithId(itemData.plate_id));
                holder.title.setText(itemData.title);

                ImageView imageView = holder.imageOne;
                Drawable drawable = imageView.getDrawable();
                if (drawable instanceof BitmapDrawable) {
                    Bitmap bmp = ((BitmapDrawable) drawable).getBitmap();
                    if (bmp != null && !bmp.isRecycled()) {
                        imageView.setImageBitmap(null);
                        bmp.recycle();
                        bmp = null;
                    }
                }
                imageView.setImageDrawable(null);

                if (itemData.image.size() > 0) {
                    holder.contentText.setVisibility(View.GONE);
                    int width = (Constants.SCREEN_WIDTH - UnitConvert.dp2Px(mContext, 56)) / 2;
                    int height = UnitConvert.dp2Px(mContext, 90);
                    String url = itemData.image.get(0) +
                        "?imageView2/1/w/" + width + "/h/" + height + "/format/webp";
                    Glide.with(mContext)
                        .load(url)
                        .crossFade()
                        .into(holder.imageOne);
                } else {
                    holder.contentText.setVisibility(View.VISIBLE);
                    holder.contentText.setText(itemData.content);
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
            return mItemList == null ? 0 : mItemList.size() + 7;
        }

        //此时mHeaderView和mFooterView必有一个不为null
        if (mHeaderView == null || mFooterView == null) {
            return mItemList == null ? 8 : mItemList.size() + 8;
        }

        return mItemList == null ? 9 : mItemList.size() + 9;
    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    public List<Entry> getItemList() {
        return mItemList;
    }

    //在这里面加载ListView中的每个item的布局
    private class ViewHolder extends RecyclerView.ViewHolder {
        WeakReference<View> viewWeakReference;
        CircleImageView userAvatar;
        TextView userName;
//        TextView plate_id;
        TextView title;
        TextView contentText;
        ImageView imageOne;
        TextView time;
        TextView readNum;
        TextView commentNum;
        TextView plateName;
        ImageView plateIcon;

        ViewHolder(View itemView) {
            super(itemView);
            //如果是headerView或者是footerView,直接返回
            if (itemView == mHeaderView) {
                return;
            }
            if (itemView == mFooterView) {
                return;
            }
            if (itemView instanceof RelativeLayout) {
                plateName = (TextView) itemView.findViewById(R.id.plate_name);
                plateIcon = (ImageView) itemView.findViewById(R.id.plate_icon);
                return;
            }

            viewWeakReference = new WeakReference<>(itemView);
            userAvatar = (CircleImageView) itemView.findViewById(R.id.item_user_avatar);
            userName = (TextView) itemView.findViewById(R.id.item_user_name);
//            plate_id = (TextView) itemView.findViewById(R.id.item_plate);
            title = (TextView) itemView.findViewById(R.id.item_title);
            contentText = (TextView) itemView.findViewById(R.id.entry_item_content);
            imageOne = (ImageView) itemView.findViewById(R.id.image_one);
            time = (TextView) itemView.findViewById(R.id.item_time);
            readNum = (TextView) itemView.findViewById(R.id.read_number);
            commentNum = (TextView) itemView.findViewById(R.id.comment_number);
        }
    }
}
