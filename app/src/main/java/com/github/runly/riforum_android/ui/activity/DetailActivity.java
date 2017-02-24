package com.github.runly.riforum_android.ui.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.runly.richedittext.RichEditText;
import com.github.runly.richedittext.span.FakeImageSpan;
import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.interfaces.OnCommentedListener;
import com.github.runly.riforum_android.model.Comment;
import com.github.runly.riforum_android.model.Entry;
import com.github.runly.riforum_android.model.User;
import com.github.runly.riforum_android.retrofit.RetrofitFactory;
import com.github.runly.riforum_android.ui.adapter.DetailAdapter;
import com.github.runly.riforum_android.ui.application.App;
import com.github.runly.riforum_android.ui.view.CircularImageView;
import com.github.runly.riforum_android.ui.view.CommentDialog;
import com.github.runly.riforum_android.ui.view.TopBar;
import com.github.runly.riforum_android.utils.ToastUtil;
import com.github.runly.riforum_android.utils.TxtUtils;
import com.github.runly.riforum_android.utils.UnitConvert;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ranly on 17-2-20.
 */

public class DetailActivity extends BaseActivity implements View.OnClickListener{

    private final static int LAYOUT_HIGHER =
            UnitConvert.dipToPixels(App.getInstance(), 32) * 2 + UnitConvert.dipToPixels(App.getInstance(), 16);
    private final static int LAYOUT_HIGH = UnitConvert.dipToPixels(App.getInstance(), 48);
    private final static int EDIT_HIGHER = UnitConvert.dipToPixels(App.getInstance(), 32) * 2;
    private final static int EDIT_HIGH = UnitConvert.dipToPixels(App.getInstance(), 32);

    private EditText commentEdit;
    private RecyclerView recyclerView;
    private Entry entry;
    private List<Comment> commentList = new ArrayList<>();
    private Comment commented;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
            coordinatorLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBase));
        }

        commentEdit = (EditText) findViewById(R.id.comment_edit_text);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relative_layout);

        final View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        // 软键盘是否弹出监听
        MyOnGlobalLayoutListener listener = new MyOnGlobalLayoutListener(rootView,
                isShow -> {
                    ViewGroup.LayoutParams relativeParams = relativeLayout.getLayoutParams();
                    ViewGroup.LayoutParams editParams = commentEdit.getLayoutParams();
                    if (isShow) {
                        if (relativeParams.height != LAYOUT_HIGHER) {
                            relativeParams.height = LAYOUT_HIGHER;
                            relativeLayout.setLayoutParams(relativeParams);

                            editParams.height = EDIT_HIGHER;
                            commentEdit.setLayoutParams(editParams);
                            commentEdit.setMinLines(3);
                            commentEdit.setGravity(Gravity.TOP);
                        }
                    } else {
                        if (relativeParams.height != LAYOUT_HIGH) {
                            relativeParams.height = LAYOUT_HIGH;
                            relativeLayout.setLayoutParams(relativeParams);

                            editParams.height = EDIT_HIGH;
                            commentEdit.setLayoutParams(editParams);
                            commentEdit.setMinLines(1);
                            commentEdit.setGravity(Gravity.CENTER_VERTICAL);

                            commentEdit.setText("");
                            commentEdit.setHint(getString(R.string.comment_hint));
                            commented = null;
                        }
                    }
                });

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(listener);

        recyclerView = (RecyclerView) findViewById(R.id.detail_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        View header = getLayoutInflater().inflate(R.layout.recycler_detail_header, recyclerView, false);
        setupRecyclerView(recyclerView, header);

        TopBar topBar = (TopBar) findViewById(R.id.detail_top_bar);
        topBar.getImgLeft().setOnClickListener(v -> finish());
        topBar.setOnClickListener(v -> recyclerView.scrollToPosition(0));

        CircularImageView userAvatar = (CircularImageView) header.findViewById(R.id.detail_user_avatar);
        RichEditText richEditText = (RichEditText) header.findViewById(R.id.detail_content);
        richEditText.setKeyListener(null);
        richEditText.setFocusable(false); // 设置为false是为了图片加载出来后布局不往上移
        TextView titleTV = (TextView) header.findViewById(R.id.detail_title);
        TextView timeTV = (TextView) header.findViewById(R.id.detail_time);
        TextView userTV = (TextView) header.findViewById(R.id.detail_user_name);
        TextView readNum = (TextView) header.findViewById(R.id.read_number);
        TextView commentNum = (TextView) header.findViewById(R.id.comment_number);
        TextView plate = (TextView) header.findViewById(R.id.detail_plate);

        entry = (Entry) getIntent().getSerializableExtra("item_data");
        User user = null;
        if (entry != null) {
            user = entry.user;
        }
        if (null != entry) {
            topBar.getTxtCenter().setText(entry.title);
            setRichTextContent(entry.content, richEditText);
            titleTV.setText(entry.title);
            timeTV.setText(TxtUtils.getReadableTime(String.valueOf(entry.time)));
            readNum.setText(String.valueOf(entry.read_num));
            commentNum.setText(String.valueOf(entry.comment_num));
            plate.setText(TxtUtils.getPlateWithId(entry.plate));

            if (null != user) {
                Glide.with(this)
                        .load(user.avatar)
                        .crossFade()
                        .into(userAvatar);
                userTV.setText(user.name);

            }
        }

        ImageButton imageButton = (ImageButton) findViewById(R.id.send_comment);
        imageButton.setOnClickListener(this);

        fetchData();
    }

    private void setupRecyclerView(RecyclerView recyclerView, View header) {

        OnCommentedListener listener =
                (comment, position) -> commented = comment;
        DetailAdapter adapter = new DetailAdapter(this, commentList, commentEdit, listener);

        adapter.setHeaderView(header);
        recyclerView.setAdapter(adapter);
    }

    private void fetchData() {
        Map<String, Object> map = new  HashMap<>();
        map.put("page", 0);
        map.put("entry_id", entry.id);
        RetrofitFactory.getInstance().getCommentService().comment_list(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if ("1".equals(response.code)) {
                        commentList.clear();
                        commentList.addAll(response.data);
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }, Throwable::printStackTrace);
    }

    private void sendComment() {
        User user = App.getInstance().getUser();
        if (!App.getInstance().islogin() || user == null) {
            ToastUtil.makeShortToast(this, getString(R.string.not_login));
            return;
        }

        Map<String, Object> map = new HashMap<>();
        String content = commentEdit.getText().toString();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.makeShortToast(this, getString(R.string.comment_empty));
            return;
        }

        map.put("token", user.token);
        map.put("uid", user.id);
        map.put("content", content);
        map.put("plate_id", entry.plate);
        map.put("entry_id", entry.id);
        if (commented != null) {
            map.put("comment_id", commented.id);
        } else {
            map.put("comment_id", -1);
        }

        RetrofitFactory.getInstance().getCommentService().comment(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if ("1".equals(response.code)) {
                        commentList.add(response.data);
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        boolean isOpen=imm.isActive();//isOpen若返回true，则表示输入法打开
                        recyclerView.getAdapter().notifyItemChanged(commentList.size());
                        recyclerView.scrollToPosition(commentList.size());
                        if (isOpen) {
                            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    ToastUtil.makeShortToast(this, getString(R.string.comment_failed));
                });

        commentEdit.setText("");
    }

    private void setRichTextContent(String richText, RichEditText richEditText) {
        richEditText.setRichText(richText);
        FakeImageSpan[] imageSpans = richEditText.getFakeImageSpans();
        if (imageSpans == null || imageSpans.length == 0) {
            return;
        }
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        for (final FakeImageSpan imageSpan : imageSpans) {
            final String src = imageSpan.getValue();
            if (src.startsWith("http")) {
                // web images
                new AsyncTask<String, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(String... params) {
                        try {
                            InputStream is = new URL(src).openStream();
                            return BitmapFactory.decodeStream(is);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        if (bitmap == null) {
                            return;
                        }
                        richEditText.replaceDownloadedImage(imageSpan, bitmap, src);
                    }
                }.executeOnExecutor(executorService, src);
            } else {
                // local images
                richEditText.replaceLocalImage(imageSpan, src);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_comment:
                sendComment();
                break;

        }
    }


    public interface OnSoftKeyWordShowListener {
        void hasShow(boolean isShow);
    }

    /**
     * 判断软键盘是否弹出
     */
    private class MyOnGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        private OnSoftKeyWordShowListener listener;
        private View rootView;

        MyOnGlobalLayoutListener(View rootView, OnSoftKeyWordShowListener listener) {
            this.listener = listener;
            this.rootView = rootView;
        }

        @Override
        public void onGlobalLayout() {
            final Rect rect = new Rect();
            rootView.getWindowVisibleDisplayFrame(rect);
            final int screenHeight = rootView.getRootView().getHeight();
            final int heightDifference = screenHeight - rect.bottom;
            boolean visible = heightDifference > screenHeight / 3;

            if (listener != null)
                listener.hasShow(visible);
        }
    }

}
