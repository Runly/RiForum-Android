package com.github.runly.riforum_android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.model.User;
import com.github.runly.riforum_android.application.App;
import com.github.runly.riforum_android.ui.fragment.DiscoverFrag;
import com.github.runly.riforum_android.ui.fragment.ForumFrag;
import com.github.runly.riforum_android.ui.fragment.RecommendFrag;
import com.github.runly.riforum_android.ui.view.TopBar;
import com.github.runly.riforum_android.application.Constants;
import com.github.runly.riforum_android.utils.GoToActivity;
import com.github.runly.riforum_android.utils.SdCardUtil;
import com.github.runly.riforum_android.utils.ToastUtil;
import com.github.runly.riforum_android.utils.TxtUtils;
import com.github.runly.riforum_android.utils.UnitConvert;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.github.runly.riforum_android.R.id.fab;

public class MainActivity extends BaseActivity implements View.OnClickListener{
    private DrawerLayout drawerLayout;
    private CircleImageView userAvatar;
    private TopBar topBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        coordinatorLayout.setPadding(0, Constants.STATUS_HEIGHT, 0, 0);
//        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) coordinatorLayout.getLayoutParams();
//        params.setMargins(0, Constants.STATUS_HEIGHT, 0, 0);
//        coordinatorLayout.setLayoutParams(params);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        userAvatar = (CircleImageView) findViewById(R.id.navigation_user_avatar);
//        ImageView imageView = (ImageView) findViewById(R.id.navigation_image);
//        Glide.with(this)
//                .load(R.mipmap.navigation_bg)
//                .crossFade()
//                .into(imageView);

        topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.getImgLeft().setOnClickListener(this);
        topBar.getTxtLeft().setOnClickListener(this);
        RelativeLayout constraintLayout = (RelativeLayout) topBar.findViewById(R.id.relativeLayout_layout);
        ViewGroup.LayoutParams lp = constraintLayout.getLayoutParams();
        lp.height = UnitConvert.dipToPixels(this, Constants.MAIN_TOPBAR_HEIGHT);
        constraintLayout.setLayoutParams(lp);


        // 右下角的FloatingActionButton
        findViewById(R.id.fab).setOnClickListener(this);

        userAvatar.setOnClickListener(this);

        initViewPagerAndTabs();
    }

    private void initViewPagerAndTabs() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(RecommendFrag.createInstance(), getString(R.string.tab_1));
        pagerAdapter.addFragment(ForumFrag.createInstance(20), getString(R.string.tab_2));
        pagerAdapter.addFragment(DiscoverFrag.createInstance(20), getString(R.string.tab_3));
//        pagerAdapter.addFragment(NotifyFrag.createInstance(20), getString(R.string.tab_4));
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        TxtUtils.setUpIndicatorWidth(this, tabLayout);
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
            case fab:
                if (App.getInstance().islogin()) {
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
                if (App.getInstance().islogin()) {
                    Intent intent = new Intent(this, UserDetailActivity.class);
                    intent.putExtra("user_data", App.getInstance().getUser());
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

    @Override
    protected void onResume() {
        super.onResume();

        User user = App.getInstance().getUser();

        // 异步读取存储在Sdcard上的User对象
        if (user == null || !App.getInstance().islogin()) {
            Observable.just(this)
                    .map(SdCardUtil::loadUserFromSdCard)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(user1 -> {
                        if (user1 != null) {
                            App.getInstance().setUser(user1);
                            topBar.getTxtLeft().setText(user1.name);
                            TextView textView = (TextView) findViewById(R.id.navigation_user_name);
                            textView.setText(user1.name);
                            if (!TextUtils.isEmpty(user1.avatar)) {
                                Glide.with(this)
                                        .load(user1.avatar)
                                        .crossFade()
                                        .into(topBar.getImgLeft());

                                Glide.with(this)
                                        .load(user1.avatar)
                                        .crossFade()
                                        .into(userAvatar);
                            }
                        }
                    });
        } else {
            topBar.getTxtLeft().setText(user.name);
            TextView textView = (TextView) findViewById(R.id.navigation_user_name);
            textView.setText(user.name);
            if (!TextUtils.isEmpty(user.avatar)) {
                Glide.with(this)
                        .load(user.avatar)
                        .crossFade()
                        .into(topBar.getImgLeft());

                Glide.with(this)
                        .load(user.avatar)
                        .crossFade()
                        .into(userAvatar);
            }
        }
    }

    private class PagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }
}
