package com.github.runly.riforum_android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.github.runly.riforum_android.R;
import com.github.runly.riforum_android.ui.adapter.PagerAdapter;
import com.github.runly.riforum_android.ui.fragment.SearchEntryFrag;
import com.github.runly.riforum_android.ui.fragment.SearchUserFrag;
import com.github.runly.riforum_android.utils.TxtUtils;

/**
 * Created by ranly on 17-3-23.
 */

public class SearchDetailActivity extends BaseActivity{
	private ViewPager viewPager;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_detail);
		init();
	}

	private void init() {
		initViewPagerAndTabs();
	}

	private void initViewPagerAndTabs() {
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setOffscreenPageLimit(2);
		PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
		pagerAdapter.addFragment(new SearchEntryFrag(), getString(R.string.search_tab_1));
		pagerAdapter.addFragment(new SearchUserFrag(), getString(R.string.search_tab_2));
		viewPager.setAdapter(pagerAdapter);

		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
		tabLayout.setupWithViewPager(viewPager);
		TxtUtils.setUpIndicatorWidth(this, tabLayout, 50);
	}
}
