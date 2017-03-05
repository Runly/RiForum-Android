package com.github.runly.riforum_android.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.interfaces.OnCommentedListener;
import com.github.runly.riforum_android.model.Comment;
import com.github.runly.riforum_android.model.User;
import com.github.runly.riforum_android.ui.activity.UserDetailActivity;
import com.github.runly.riforum_android.ui.view.CommentDialog;
import com.github.runly.riforum_android.utils.TxtUtils;

import java.lang.ref.WeakReference;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ranly on 17-2-20.
 */

public class DetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;  //Header
    private static final int TYPE_FOOTER = 1;  //Footer
    private static final int TYPE_NORMAL = 2;  //Normal

    private Context mContext;
    private List<Comment> mItemList;
    private View mHeaderView = null;
    private View mFooterView = null;
    private EditText commentEdit;
    private CommentDialog commentDialog;
    private OnCommentedListener onCommentedListener;

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public void setFooterView(View footerView) {
        mFooterView = footerView;
        notifyItemInserted(getItemCount() - 1);
    }

    public DetailAdapter(Context context, List<Comment> itemList, EditText editText, OnCommentedListener listener) {
        this.mItemList = itemList;
        this.mContext = context;
        this.commentEdit = editText;
        this.onCommentedListener = listener;
        this.commentDialog = new CommentDialog(mContext);
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
            Comment itemData;
            Comment commented;
            if (mHeaderView == null) {
                itemData = mItemList.get(position);
            } else {
                itemData = mItemList.get(position - 1);
            }

            if (null != itemData) {
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


                    holder.weakReference.get().setOnClickListener(v -> {
                        commentDialog.show();
                        commentDialog.setReplayButtonOnClickListener(v1 -> {
                            commentDialog.cancel();
                            commentEdit.requestFocus();
                            commentEdit.setHint("回复:@" + itemData.user.name);
                            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                        });
                        onCommentedListener.onCommented(itemData, position);
                    });
                }

                commented = itemData.commented;
                if (commented != null && itemData.comment_id != -1) {
                    User userCommented = commented.user;
                    if (userCommented != null) {
                        String linkText = "@" + userCommented.name;
                        SpannableString spStr = new SpannableString(linkText);
                        ClickableSpan clickSpan = new OnLineClickSpan(spStr.toString()) {
                            @Override
                            public void onClick(View widget) {
                                Intent intent = new Intent(mContext, UserDetailActivity.class);
                                intent.putExtra("user_data", userCommented);
                                mContext.startActivity(intent);
                            }
                        }; //设置超链接
                        spStr.setSpan(clickSpan, 0, spStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        holder.content.setText("");
                        holder.content.append("回复" + " ");
                        holder.content.append(spStr);
                        holder.content.append(": " + itemData.content);
                        holder.content.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                } else {
                    holder.content.setText(itemData.content);
                }
                holder.time.setText(TxtUtils.getReadableTime(String.valueOf(itemData.time)));
            }
            holder.floor.setText(position + "楼");
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
        WeakReference<View> weakReference;
        CircleImageView userAvatar;
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

            weakReference = new WeakReference<>(itemView);
            userAvatar = (CircleImageView) itemView.findViewById(R.id.comment_user_avatar);
            userName = (TextView) itemView.findViewById(R.id.comment_user_name);
            floor = (TextView) itemView.findViewById(R.id.comment_floor);
            content = (TextView) itemView.findViewById(R.id.comment_content);
            time = (TextView) itemView.findViewById(R.id.comment_time);
        }
    }

    private abstract class OnLineClickSpan extends ClickableSpan {
        String text;

        OnLineClickSpan(String text) {
            super();
            this.text = text;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ContextCompat.getColor(mContext, R.color.colorBase));
            ds.setUnderlineText(false); //去掉下划线
        }
    }
}
