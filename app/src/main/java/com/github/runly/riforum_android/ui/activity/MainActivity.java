package com.github.runly.riforum_android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.runly.letteravatar.LetterAvatar;
import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.application.App;
import com.github.runly.riforum_android.application.Constants;
import com.github.runly.riforum_android.model.User;
import com.github.runly.riforum_android.retrofit.RetrofitFactory;
import com.github.runly.riforum_android.ui.adapter.PagerAdapter;
import com.github.runly.riforum_android.ui.fragment.DiscoverFrag;
import com.github.runly.riforum_android.ui.fragment.ForumFrag;
import com.github.runly.riforum_android.ui.fragment.RecommendFrag;
import com.github.runly.riforum_android.ui.view.TopBar;
import com.github.runly.riforum_android.utils.BitmapUtil;
import com.github.runly.riforum_android.utils.GoToActivity;
import com.github.runly.riforum_android.utils.RegisterCheck;
import com.github.runly.riforum_android.utils.SdCardUtil;
import com.github.runly.riforum_android.utils.SharedPreferencesUtil;
import com.github.runly.riforum_android.utils.ToastUtil;
import com.github.runly.riforum_android.utils.TxtUtils;
import com.github.runly.riforum_android.utils.UnitConvert;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements View.OnClickListener {
	private DrawerLayout drawerLayout;
	private ViewPager viewPager;
	private CircleImageView navigationAvatar;
	private TextView navigationName;
	private FloatingActionButton fab;
	private TopBar topBar;
	private User user;
	private SwitchButton switchButton;
	private boolean isLogging;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setMode();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();
	}

	private void init() {
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.placeholder_layout);
		linearLayout.setPadding(0, Constants.STATUS_HEIGHT, 0, 0);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		ImageView navigationBg = (ImageView) findViewById(R.id.navigation_image);
		navigationBg.setImageBitmap(BitmapUtil.createScaledBitmap(this, R.mipmap.navigation_bg));
		setNavigationListener();
		navigationAvatar = (CircleImageView) findViewById(R.id.navigation_user_avatar);
		navigationName = (TextView) findViewById(R.id.navigation_user_name);
		switchButton = (SwitchButton) findViewById(R.id.switch_button);
		switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (isChecked) {
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
				recreate();
				SharedPreferencesUtil.saveValue(Constants.WHICH_MODE, Constants.NIGHT_MODE);
			} else {
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
				recreate();
				SharedPreferencesUtil.saveValue(Constants.WHICH_MODE, Constants.DAY_MODE);
			}
		});

		topBar = (TopBar) findViewById(R.id.top_bar);
		topBar.getImgLeft().setOnClickListener(this);
		topBar.getTxtLeft().setOnClickListener(this);
		RelativeLayout constraintLayout = (RelativeLayout) topBar.findViewById(R.id.relativeLayout_layout);
		ViewGroup.LayoutParams lp = constraintLayout.getLayoutParams();
		lp.height = UnitConvert.dp2Px(this, Constants.MAIN_TOPBAR_HEIGHT);
		constraintLayout.setLayoutParams(lp);


		// 右下角的FloatingActionButton
		fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(this);

		navigationAvatar.setOnClickListener(this);

		initViewPagerAndTabs();
	}

	private void initViewPagerAndTabs() {
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setOffscreenPageLimit(2);
		PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
		pagerAdapter.addFragment(RecommendFrag.createInstance(), getString(R.string.tab_1));
		pagerAdapter.addFragment(ForumFrag.createInstance(), getString(R.string.tab_2));
		pagerAdapter.addFragment(DiscoverFrag.createInstance(), getString(R.string.tab_3));
		viewPager.setAdapter(pagerAdapter);

		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
		tabLayout.setupWithViewPager(viewPager);
		TxtUtils.setUpIndicatorWidth(this, tabLayout, 35);
	}

	private void openDrawer() {
		drawerLayout.openDrawer(GravityCompat.START);
	}

	private void closeDrawer() {
		drawerLayout.closeDrawer(GravityCompat.START, false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.fab:
				if (App.getInstance().isLogin()) {
					GoToActivity.goTo(this, ChoosePlateActivity.class);
				} else {
					ToastUtil.makeShortToast(this, getString(R.string.not_login));
				}
				break;

			case R.id.img_left:
			case R.id.txt_left:
				openDrawer();
				break;

			case R.id.navigation_user_avatar:
				if (App.getInstance().isLogin()) {
					Intent intent = new Intent(this, UserDetailActivity.class);
					intent.putExtra(Constants.INTENT_USER_DATA, App.getInstance().getUser());
					startActivity(intent);
				} else {
					GoToActivity.goTo(this, LoginActivity.class);
					closeDrawer();
				}
				break;

			default:
				break;
		}
	}

	private void setSwitchButton() {
		int mode = SharedPreferencesUtil.getInt(Constants.WHICH_MODE);
		if (mode == Constants.DAY_MODE) {
			switchButton.setChecked(false);
		} else {
			switchButton.setChecked(true);
		}
	}

	private void setMode() {
		int mode = SharedPreferencesUtil.getInt(Constants.WHICH_MODE);
		if (mode == Constants.DAY_MODE) {
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
		} else {
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
		}
	}

	private void setNavigationListener() {
		LinearLayout linearPerson = (LinearLayout) findViewById(R.id.navigation_user_info);
		linearPerson.setOnClickListener(v -> {
			if (user != null) {
				Intent intent = new Intent(this, UserDetailActivity.class);
				intent.putExtra(Constants.INTENT_USER_DATA, user);
				startActivity(intent);
			} else {
				GoToActivity.goTo(this, LoginActivity.class);
			}
		});

		LinearLayout linearSetting = (LinearLayout) findViewById(R.id.navigation_setting);
		linearSetting.setOnClickListener(v -> {
			Intent intent = new Intent(this, SettingsActivity.class);
			intent.putExtra(Constants.INTENT_USER_DATA, user);
			startActivity(intent);
		});

		LinearLayout linearAbout = (LinearLayout) findViewById(R.id.navigation_about);
		linearAbout.setOnClickListener(v -> GoToActivity.goTo(this, AboutActivity.class));

	}

	private void setAvatars() {
		if (user == null) {
			Glide.with(App.getInstance())
				.load(R.mipmap.avatar_default)
				.crossFade()
				.into(topBar.getImgLeft());

			Glide.with(App.getInstance())
				.load(R.mipmap.avatar_default)
				.crossFade()
				.into(navigationAvatar);

			return;
		}

		if (TextUtils.isEmpty(user.avatar)) {

			LetterAvatar.with(this)
				.canvasSizeDIP(Constants.NORMAL_AVATAR_SIZE, Constants.NORMAL_AVATAR_SIZE)
				.letterSizeDIP(Constants.NORMAL_AVATAR_SIZE / 2)
				.chineseFirstLetter(user.name, true)
				.letterColorResId(R.color.comment_bar_dictionary)
				.backgroundColorResId(R.color.item_dividing)
				.into(topBar.getImgLeft());

			LetterAvatar.with(this)
				.canvasSizeDIP(Constants.NAVIGATION_AVATAR_SIZE, Constants.NAVIGATION_AVATAR_SIZE)
				.letterSizeDIP(Constants.NAVIGATION_AVATAR_SIZE / 2)
				.chineseFirstLetter(user.name, true)
				.letterColorResId(R.color.comment_bar_dictionary)
				.backgroundColorResId(R.color.item_dividing)
				.into(navigationAvatar);

			return;
		}

		String avatarUrl = user.avatar + "?imageView2/1/w/" +
			UnitConvert.dp2Px(this, Constants.NORMAL_AVATAR_SIZE) + "/h/" +
			UnitConvert.dp2Px(this, Constants.NORMAL_AVATAR_SIZE) + "/format/webp";
		Glide.with(App.getInstance())
			.load(avatarUrl)
			.crossFade()
			.into(topBar.getImgLeft());

		avatarUrl = user.avatar + "?imageView2/1/w/" +
			UnitConvert.dp2Px(this, Constants.NAVIGATION_AVATAR_SIZE) + "/h/" +
			UnitConvert.dp2Px(this, Constants.NAVIGATION_AVATAR_SIZE) + "/format/webp";
		Glide.with(App.getInstance())
			.load(avatarUrl)
			.crossFade()
			.into(navigationAvatar);
	}

	private void login() {
		String account = SharedPreferencesUtil.getString(Constants.USER_ACCOUNT);
		String password = SharedPreferencesUtil.getString(Constants.USER_PASSWORD);

		Map<String, Object> map = new HashMap<>();
		//判断是否为邮箱
		if (RegisterCheck.isEmail(account)) {
			map.put("email", account);
		} else if (RegisterCheck.isPhone(account)) {
			map.put("phone", account);
		} else {
			return;
		}
		if (!TextUtils.isEmpty(password)) {
			map.put("password", password);
		} else {
			return;
		}

		isLogging = true;

		RetrofitFactory.getInstance().getUserService().login(map)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(response -> {
				if ("1".equals(response.code)) {
					user = response.data;
					if (null != user) {
						App.getInstance().setUser(user);
						SdCardUtil.saveUserToSdCard(this, user);
						topBar.getTxtLeft().setText(user.name);
						navigationName.setText(user.name);
						setAvatars();
					}
				}
				isLogging = false;
			}, throwable -> {
				throwable.printStackTrace();
				isLogging = false;
			});
	}

	@Override
	protected void onResume() {
		super.onResume();
		user = App.getInstance().getUser();
		if (user == null || !App.getInstance().isLogin()) {
			// 在io线程中写文件
			Observable.just(this)
				.map(SdCardUtil::loadUserFromSdCard)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(user1 -> {
					if (user1 != null) {
						App.getInstance().setUser(user1);
						user = user1;
						topBar.getTxtLeft().setText(user1.name);
						navigationName.setText(user1.name);
					} else {
						topBar.getTxtLeft().setText(R.string.not_login);
						navigationName.setText(R.string.click_to_login);
					}
				}, throwable -> {
					topBar.getTxtLeft().setText(R.string.not_login);
					navigationName.setText(R.string.click_to_login);
				}, () -> {
					setAvatars();
					// 最终都要登录一次，更新用户数据
					if (!isLogging) {
						login();
					}
				});
		} else {
			topBar.getTxtLeft().setText(user.name);
			navigationName.setText(user.name);
			setAvatars();
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		setSwitchButton();
	}

	public TopBar getTopBar() {
		return topBar;
	}

	public ViewPager getViewPager() {
		return viewPager;
	}
}
