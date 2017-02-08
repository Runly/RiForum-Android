package com.github.runly.riforum_android.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.ui.fragment.FollowFrag;
import com.github.runly.riforum_android.ui.fragment.ForumFrag;
import com.github.runly.riforum_android.ui.fragment.NotifyFrag;
import com.github.runly.riforum_android.ui.fragment.RecommendFrag;
import com.github.runly.riforum_android.ui.view.TopBar;
import com.github.runly.riforum_android.utils.Constant;
import com.github.runly.riforum_android.utils.UnitConvert;
import com.pkmmte.view.CircularImageView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViewPagerAndTabs();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        TopBar topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.getImgLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        topBar.getTxtLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        CircularImageView circularImageView = (CircularImageView) findViewById(R.id.user_avatar);
        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initViewPagerAndTabs() {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        coordinatorLayout.setPadding(0, Constant.STATUS_HEIGHT, 0, 0);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(RecommendFrag.createInstance(20), getString(R.string.tab_1));
        pagerAdapter.addFragment(ForumFrag.createInstance(20), getString(R.string.tab_2));
        pagerAdapter.addFragment(FollowFrag.createInstance(20), getString(R.string.tab_3));
        pagerAdapter.addFragment(NotifyFrag.createInstance(20), getString(R.string.tab_4));
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        setUpIndicatorWidth(tabLayout);
    }

    /**
     * 通过反射修改TabLayout Indicator的宽度（仅在Android 4.2及以上生效）
     */
    private void setUpIndicatorWidth(TabLayout tabLayout) {
        Class<?> tabLayoutClass = tabLayout.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayoutClass.getDeclaredField("mTabStrip");
            tabStrip.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        LinearLayout layout = null;
        try {
            if (tabStrip != null) {
                layout = (LinearLayout) tabStrip.get(tabLayout);
            }
            if (layout != null) {
                for (int i = 0; i < layout.getChildCount(); i++) {
                    View child = layout.getChildAt(i);
                    child.setPadding(0, 0, 0, 0);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        params.setMarginStart(UnitConvert.dipToPixels(this, 18));
                        params.setMarginEnd(UnitConvert.dipToPixels(this, 18));
                    }
                    child.setLayoutParams(params);
                    child.invalidate();
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
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

//    private int getStatusBarHeight() {
//        int statusBarHeight1 = 0;
//        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
//        if (resourceId > 0) {
//            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
//        }
//        return statusBarHeight1;
//    }
}
