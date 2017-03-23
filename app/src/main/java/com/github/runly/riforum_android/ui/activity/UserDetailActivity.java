package com.github.runly.riforum_android.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.runly.letteravatar.LetterAvatar;
import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.Constants;
import com.github.runly.riforum_android.model.Entry;
import com.github.runly.riforum_android.model.User;
import com.github.runly.riforum_android.retrofit.RetrofitFactory;
import com.github.runly.riforum_android.ui.adapter.EntriesAdapter;
import com.github.runly.riforum_android.ui.view.MyDecoration;
import com.github.runly.riforum_android.utils.BitmapUtil;
import com.github.runly.riforum_android.utils.RecyclerScrollToTop;
import com.github.runly.riforum_android.utils.UnitConvert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ranly on 17-2-27.
 */

public class UserDetailActivity extends BaseActivity {
	private RecyclerView recyclerView;
	private TextView numText;
	private TextView nameText;
	private ImageView genderImg;
	private User user;
	private CircleImageView avatar;
	private boolean isFetching;
	private String message = "";

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_detail);

		user = (User) getIntent().getSerializableExtra(Constants.INTENT_USER_DATA);
		init();
	}

	private void init() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
		params.setMargins(0, Constants.STATUS_HEIGHT, 0, 0);
		toolbar.setLayoutParams(params);
		toolbar.setOnClickListener(v -> RecyclerScrollToTop.scrollToTop(recyclerView));

		ImageView bgImage = (ImageView) findViewById(R.id.bg_image);
		Bitmap bitmap = BitmapUtil.createScaledBitmap(this, R.mipmap.user_detail_bg);
		bgImage.setImageBitmap(bitmap);

		ImageView imageView = (ImageView) findViewById(R.id.toolbar_back);
		imageView.setOnClickListener(v -> finish());

		if (null != user) {
			TextView userInfoText = (TextView) findViewById(R.id.user_info);
			userInfoText.setOnClickListener(v -> goToUserInfoAct());
			numText = (TextView) findViewById(R.id.num);
			numText.setText(String.format(getString(R.string.release_num), user.entry_number));

			avatar = (CircleImageView) findViewById(R.id.user_detail_avatar);
			avatar.setOnClickListener(v -> goToUserInfoAct());
			if (!TextUtils.isEmpty(user.avatar)) {
				String avatarUrl = user.avatar + "?imageView2/1/w/" +
					UnitConvert.dp2Px(this, Constants.USER_INFO_AVATAR_SIZE) + "/h/" +
					UnitConvert.dp2Px(this, Constants.USER_INFO_AVATAR_SIZE) + "/format/webp";
				Glide.with(this)
					.load(avatarUrl)
					.crossFade()
					.into(avatar);
			} else {

				LetterAvatar.with(this)
					.canvasSizeDIP(Constants.USER_INFO_AVATAR_SIZE, Constants.USER_INFO_AVATAR_SIZE)
					.letterSizeDIP(Constants.USER_INFO_AVATAR_SIZE / 2)
					.chineseFirstLetter(user.name, true)
					.letterColorResId(R.color.comment_bar_dictionary)
					.backgroundColorResId(R.color.item_dividing)
					.into(avatar);
			}

			nameText = (TextView) findViewById(R.id.user_detail_name);
			nameText.setText(user.name);

			genderImg = (ImageView) findViewById(R.id.gender);
			setGenderImgSrc(user.gender);
		}

		recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		setupRecyclerView(recyclerView);
		fetchData(false, System.currentTimeMillis());
	}

	private void goToUserInfoAct() {
		Intent intent = new Intent(this, UserInfoActivity.class);
		intent.putExtra(Constants.INTENT_USER_DATA, user);
		startActivityForResult(intent, Constants.START_USER_INFO);
	}

	private void setGenderImgSrc(int gender) {
		if (0 == gender) {
			genderImg.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.male));
		} else if (1 == gender) {
			genderImg.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.female));
		} else {
			genderImg.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.no_gender));
		}
	}

	private void fetchData(boolean isMore, long page) {
		if ("end".equals(message) || isFetching) {
			return;
		}

		Map<String, Object> map = new HashMap<>();
		if (user != null) {
			map.put("uid", user.id);
		} else {
			return;
		}
		map.put("page", page);

		isFetching = true;

		RetrofitFactory.getInstance()
			.getEntryService()
			.user_release(map)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(response -> {
				if ("1".equals(response.code)) {
					List<Entry> list = ((EntriesAdapter) recyclerView.getAdapter()).getItemList();
					if (!isMore) {
						list.clear();
					}
					list.addAll(response.data);
					recyclerView.getAdapter().notifyDataSetChanged();
					message = response.message;
				}
				isFetching = false;
			}, throwable -> {
				throwable.printStackTrace();
				isFetching = false;
			});
	}

	private void setupRecyclerView(RecyclerView recyclerView) {
		EntriesAdapter entriesAdapter = new EntriesAdapter(this, new ArrayList<>());
		recyclerView.setAdapter(entriesAdapter);
		recyclerView.setHasFixedSize(true);
		recyclerView.addItemDecoration(new MyDecoration(this, 8, 8, 8, 0, true));
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
						List<Entry> list = ((EntriesAdapter) recyclerView.getAdapter()).getItemList();
						if (list.size() > 0) {
							Entry lastEntry = list.get(list.size() - 1);
							fetchData(true, lastEntry.time);
						}
					}
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {

			if (requestCode == Constants.START_USER_INFO) {
				user = (User) data.getSerializableExtra(Constants.INTENT_USER_DATA);
				if (user != null) {
					nameText.setText(user.name);
					setGenderImgSrc(user.gender);
					if (!TextUtils.isEmpty(user.avatar)) {
						String avatarUrl = user.avatar + "?imageView2/1/w/" +
							UnitConvert.dp2Px(this, Constants.USER_INFO_AVATAR_SIZE) + "/h/" +
							UnitConvert.dp2Px(this, Constants.USER_INFO_AVATAR_SIZE) + "/format/webp";
						Glide.with(this)
							.load(avatarUrl)
							.crossFade()
							.into(avatar);
					}
				}
			}

		}
	}
}
