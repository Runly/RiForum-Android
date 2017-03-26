package com.github.runly.riforum_android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.runly.letteravatar.LetterAvatar;
import com.github.runly.richedittext.RichEditText;
import com.github.runly.richedittext.span.FakeImageSpan;
import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.App;
import com.github.runly.riforum_android.application.Constants;
import com.github.runly.riforum_android.interfaces.OnCommented;
import com.github.runly.riforum_android.model.Comment;
import com.github.runly.riforum_android.model.Entry;
import com.github.runly.riforum_android.model.User;
import com.github.runly.riforum_android.retrofit.RetrofitFactory;
import com.github.runly.riforum_android.ui.adapter.CommentAdapter;
import com.github.runly.riforum_android.ui.view.DeleteDialog;
import com.github.runly.riforum_android.ui.view.MyDecoration;
import com.github.runly.riforum_android.ui.view.TopBar;
import com.github.runly.riforum_android.utils.MyOnGlobalLayoutListener;
import com.github.runly.riforum_android.utils.RecyclerScrollToTop;
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

import de.hdodenhof.circleimageview.CircleImageView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ranly on 17-2-20.
 */

public class DetailActivity extends BaseActivity implements View.OnClickListener {

	private final static int LAYOUT_HIGHER =
		UnitConvert.dp2Px(App.getInstance(), 32) * 2 + UnitConvert.dp2Px(App.getInstance(), 16);
	private final static int LAYOUT_HIGH = UnitConvert.dp2Px(App.getInstance(), 48);
	private final static int EDIT_HIGHER = UnitConvert.dp2Px(App.getInstance(), 32) * 2;
	private final static int EDIT_HIGH = UnitConvert.dp2Px(App.getInstance(), 32);

	private EditText commentEdit;
	private RecyclerView recyclerView;
	private Entry entry;
	private List<Comment> commentList = new ArrayList<>();
	private Comment commented;
	private boolean isFetching;
	private String message = "";

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		entry = (Entry) getIntent().getSerializableExtra(Constants.INTENT_ENTRY_DATA);
		init();
	}

	private void init() {
		TopBar topBar = (TopBar) findViewById(R.id.top_bar);
		topBar.getTxtLeft().setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		topBar.getImgLeft().setOnClickListener(v -> finish());
		topBar.setOnClickListener(v -> recyclerView.scrollToPosition(0));
		topBar.setOnClickListener(v -> RecyclerScrollToTop.scrollToTop(recyclerView));

		View view = LayoutInflater.from(this).inflate(R.layout.popup_window_detail, null);
		PopupWindow popupWindow = new PopupWindow(this);
		popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
		popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		popupWindow.setContentView(view);
		popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		topBar.getImgRight().setOnClickListener(v -> {
			// 108dp 是 R.layout.popup_window_detail 的宽度
			int x = UnitConvert.dp2Px(this, 108 / 2 + 8) + topBar.getImgRight().getWidth() / 2;
			int y = UnitConvert.dp2Px(this, 4);
			popupWindow.showAsDropDown(topBar.getImgRight(), -x, y);
		});
		LinearLayout deleteLinear = (LinearLayout) view.findViewById(R.id.linear_delete);
		deleteLinear.setOnClickListener(v -> {
			DeleteDialog deleteDialog = new DeleteDialog(this);
			deleteDialog.show();
			deleteDialog.setPositiveListener(v1 -> {
				if (entry != null) {
					Map<String, Object> map = new HashMap<>();
					map.put("entry_id", entry.id);
					map.put("uid", entry.user.id);
					RetrofitFactory.getInstance()
						.getEntryService()
						.delete(map)
						.subscribeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(response -> {
							ToastUtil.makeShortToast(this, getString(R.string.delete_successfully));
							App.getInstance().setDeleteEntry(entry);
							deleteDialog.cancel();
							finish();
						}, throwable -> {
							throwable.printStackTrace();
							ToastUtil.makeShortToast(this, getString(R.string.delete_failed));
							deleteDialog.cancel();
						});

				} else {
					ToastUtil.makeShortToast(this, getString(R.string.delete_failed));
					deleteDialog.cancel();
				}
			});
			popupWindow.dismiss();
		});

		commentEdit = (EditText) findViewById(R.id.search_edit_text);
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
						commentEdit.requestFocus();
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
						commentEdit.clearFocus();
					}
				}
			});

		rootView.getViewTreeObserver().addOnGlobalLayoutListener(listener);

		recyclerView = (RecyclerView) findViewById(R.id.detail_recycler);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		View header = getLayoutInflater().inflate(R.layout.recycler_detail_header, recyclerView, false);
		setupRecyclerView(recyclerView, header);

		CircleImageView userAvatar = (CircleImageView) header.findViewById(R.id.detail_user_avatar);
		RichEditText richEditText = (RichEditText) header.findViewById(R.id.detail_content);
		richEditText.setKeyListener(null);
		richEditText.setFocusable(false); // 设置为false是为了图片加载出来后布局不往上移
		TextView titleTV = (TextView) header.findViewById(R.id.detail_title);
		TextView timeTV = (TextView) header.findViewById(R.id.detail_time);
		TextView userTV = (TextView) header.findViewById(R.id.detail_user_name);
		TextView readNum = (TextView) header.findViewById(R.id.read_number);
		TextView commentNum = (TextView) header.findViewById(R.id.comment_number);
		TextView plate = (TextView) header.findViewById(R.id.detail_plate);

		if (null != entry) {
			topBar.getTxtCenter().setText(entry.title);
			if (App.getInstance().getUser() != null) {
				if (App.getInstance().getUser().id != entry.user.id) {
					topBar.getImgRight().setVisibility(View.GONE);
				}
			}

			User user = entry.user;
			setRichTextContent(entry.content, richEditText);
			titleTV.setText(entry.title);
			timeTV.setText(TxtUtils.getReadableTime(String.valueOf(entry.time)));
			readNum.setText(String.valueOf(entry.read_num));
			commentNum.setText(String.valueOf(entry.comment_num));
			plate.setText(entry.plate.name);
			plate.setOnClickListener(v -> {
				Intent intent = new Intent(this, EntriesOfPlateActivity.class);
				intent.putExtra(Constants.INTENT_PLATE_DATA, entry.plate);
				startActivity(intent);
			});

			if (null != user) {

				if (!TextUtils.isEmpty(user.avatar)) {
					String avatarUrl = user.avatar + "?imageView2/1/w/" +
						UnitConvert.dp2Px(this, Constants.NORMAL_AVATAR_SIZE) + "/h/" +
						UnitConvert.dp2Px(this, Constants.NORMAL_AVATAR_SIZE) + "/format/webp";
					Glide.with(this)
						.load(avatarUrl)
						.crossFade()
						.into(userAvatar);
				} else {
					LetterAvatar.with(this)
						.canvasSizeDIP(Constants.NORMAL_AVATAR_SIZE, Constants.NORMAL_AVATAR_SIZE)
						.letterSizeDIP(Constants.NORMAL_AVATAR_SIZE / 2)
						.chineseFirstLetter(user.name, true)
						.letterColorResId(R.color.comment_bar_dictionary)
						.backgroundColorResId(R.color.item_dividing)
						.into(userAvatar);
				}

				userTV.setText(user.name);

				View.OnClickListener clickListener = v -> {
					Intent intent = new Intent(this, UserDetailActivity.class);
					intent.putExtra(Constants.INTENT_USER_DATA, user);
					startActivity(intent);
				};
				userAvatar.setOnClickListener(clickListener);
				userTV.setOnClickListener(clickListener);
			}
		}

		ImageButton imageButton = (ImageButton) findViewById(R.id.send_comment);
		imageButton.setOnClickListener(this);

		fetchData(false, 0);
	}

	private void setupRecyclerView(RecyclerView recyclerView, View header) {

		OnCommented onCommented =
			(comment, position) -> commented = comment;
		CommentAdapter adapter = new CommentAdapter(this, commentList, commentEdit, onCommented);

		adapter.setHeaderView(header);
		recyclerView.setHasFixedSize(true);
		recyclerView.addItemDecoration(new MyDecoration(this, 0, 0, 0, 1, false));
		recyclerView.setAdapter(adapter);
		LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);

				int itemCount = lm.getItemCount();
				int currentPosition = lm.findFirstVisibleItemPosition();
				if (currentPosition >= itemCount / 2) {
					if (!isFetching) {
						List<Comment> list = ((CommentAdapter) recyclerView.getAdapter()).getItemList();
						if (list.size() > 0) {
							Comment lastComment = list.get(list.size() - 1);
							fetchData(true, lastComment.time);
						}
					}
				}
			}
		});
	}

	private void fetchData(boolean isMore, long page) {
		if ("end".equals(message) || isFetching) {
			return;
		}

		Map<String, Object> map = new HashMap<>();
		map.put("page", page);
		map.put("entry_id", entry.id);

		isFetching = true;

		RetrofitFactory.getInstance()
			.getCommentService()
			.comment_list(map)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(response -> {
				if ("1".equals(response.code)) {
					if (!isMore) {
						commentList.clear();
					}
					commentList.addAll(response.data);
					recyclerView.getAdapter().notifyDataSetChanged();
					message = response.message;
				}
				isFetching = false;
			}, throwable -> {
				throwable.printStackTrace();
				isFetching = false;
			});
	}

	private void sendComment() {
		User user = App.getInstance().getUser();
		if (!App.getInstance().isLogin() || user == null) {
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
		map.put("plate_id", entry.plate_id);
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
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					boolean isOpen = imm.isActive();//isOpen若返回true，则表示输入法打开
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
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.zhan_wei);
			richEditText.replaceLocalBitmap(imageSpan, bitmap, src);
		}

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
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.send_comment:
				sendComment();
				break;

		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		addStatusBarView();
	}

	private void addStatusBarView() {
		View view = new View(this);
		view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT, Constants.STATUS_HEIGHT);
		ViewGroup decorView = (ViewGroup) findViewById(android.R.id.content);
		decorView.addView(view, params);
	}
}
