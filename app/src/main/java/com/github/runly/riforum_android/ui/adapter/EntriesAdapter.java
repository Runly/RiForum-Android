package com.github.runly.riforum_android.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.runly.letteravatar.LetterAvatar;
import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.Constants;
import com.github.runly.riforum_android.model.Entry;
import com.github.runly.riforum_android.model.User;
import com.github.runly.riforum_android.ui.activity.DetailActivity;
import com.github.runly.riforum_android.ui.activity.EntriesOfPlateActivity;
import com.github.runly.riforum_android.ui.activity.UserDetailActivity;
import com.github.runly.riforum_android.utils.TxtUtils;
import com.github.runly.riforum_android.utils.UnitConvert;

import java.lang.ref.WeakReference;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.bitmap;
import static android.R.attr.color;
import static android.R.attr.offset;

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
                    intent.putExtra(Constants.INTENT_ENTRY_DATA, itemData);
                    mContext.startActivity(intent);
                };
                holder.viewWeakReference.get().setOnClickListener(onClickListener);

                User user = itemData.user;
                if (null != user) {
                    if (!TextUtils.isEmpty(user.avatar)) {
                        String avatarUrl = user.avatar + "?imageView2/1/w/" +
                            UnitConvert.dp2Px(mContext, Constants.NORMAL_AVATAR_SIZE) + "/h/" +
                            UnitConvert.dp2Px(mContext, Constants.NORMAL_AVATAR_SIZE) + "/format/webp";
                        Glide.with(mContext)
                            .load(avatarUrl)
                            .crossFade()
                            .into(holder.userAvatar);
                    } else {
                        LetterAvatar.with(mContext)
                            .canvasSizeDIP(Constants.NORMAL_AVATAR_SIZE, Constants.NORMAL_AVATAR_SIZE)
                            .letterSizeDIP(Constants.NORMAL_AVATAR_SIZE / 2)
                            .chineseFirstLetter(user.name, true)
                            .letterColorResId(R.color.comment_bar_dictionary)
                            .backgroundColorResId(R.color.item_dividing)
                            .into(holder.userAvatar);
                    }

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

                if (mContext instanceof EntriesOfPlateActivity) {
                    holder.plate.setVisibility(View.GONE);
                } else {
                    holder.plate.setText(itemData.plate.name);
                    holder.plate.setOnClickListener(v -> {
                        Intent intent = new Intent(mContext, EntriesOfPlateActivity.class);
                        intent.putExtra(Constants.INTENT_PLATE_DATA, itemData.plate);
                        mContext.startActivity(intent);
                    });
                }

                holder.title.setText(itemData.title);

                if (itemData.image.size() < 1) {
                    holder.contentText.setText(itemData.content);
                    holder.imageLinear.setVisibility(View.GONE);
                } else {
                    holder.contentText.setText("");
                    holder.imageLinear.setVisibility(View.VISIBLE);
                }

                for (int i = 0; i < 3; i++) {
                    ImageView imageView = ((ImageView) holder.imageLinear.getChildAt(i));
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
                }

                ColorDrawable mDrawable = new ColorDrawable(
                    ContextCompat.getColor(mContext, R.color.item_dividing));
                int width, height;
                if (itemData.image.size() == 1) {
                    holder.imageLinear.getChildAt(1).setVisibility(View.GONE);
                    holder.imageLinear.getChildAt(2).setVisibility(View.GONE);
                    width = (Constants.SCREEN_WIDTH - UnitConvert.dp2Px(mContext, 32));
                    height = width / 16 * 9;
                } else if (itemData.image.size() == 2) {
                    holder.imageLinear.getChildAt(1).setVisibility(View.VISIBLE);
                    holder.imageLinear.getChildAt(2).setVisibility(View.GONE);
                    width = ((Constants.SCREEN_WIDTH - UnitConvert.dp2Px(mContext, 40)) / 2);
                    height = width / 4 * 3;
                } else {
                    holder.imageLinear.getChildAt(1).setVisibility(View.VISIBLE);
                    holder.imageLinear.getChildAt(2).setVisibility(View.VISIBLE);
                    width = (Constants.SCREEN_WIDTH - UnitConvert.dp2Px(mContext, 48)) / 3;
                    height = UnitConvert.dp2Px(mContext, 80);
                }

                for (int i = 0; i < itemData.image.size(); i++) {
                    if (i > 2)
                        break;
                    ImageView imageView = (ImageView) holder.imageLinear.getChildAt(i);
                    ViewGroup.LayoutParams params = imageView.getLayoutParams();
                    params.width = width;
                    params.height = height;
                    imageView.setLayoutParams(params);
                    String url = itemData.image.get(i) +
                        "?imageView2/1/w/" + width + "/h/" + height + "/format/webp";
                    Glide.with(mContext)
                        .load(url)
                        .crossFade()
                        .placeholder(mDrawable)
                        .into((ImageView) holder.imageLinear.getChildAt(i));
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
        CircleImageView userAvatar;
        TextView userName;
        TextView plate;
        TextView title;
        LinearLayout imageLinear;
        TextView contentText;
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
            userAvatar = (CircleImageView) itemView.findViewById(R.id.item_user_avatar);
            userName = (TextView) itemView.findViewById(R.id.item_user_name);
            plate = (TextView) itemView.findViewById(R.id.item_plate);
            title = (TextView) itemView.findViewById(R.id.item_title);
            contentText = (TextView) itemView.findViewById(R.id.entry_item_content);
            imageLinear = (LinearLayout) itemView.findViewById(R.id.image_linear);
            time = (TextView) itemView.findViewById(R.id.item_time);
            readNum = (TextView) itemView.findViewById(R.id.read_number);
            commentNum = (TextView) itemView.findViewById(R.id.comment_number);
        }
    }
}
