package com.github.runly.riforum_android.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.runly.riforum_android.R;

/**
 * Created by ranly on 17-3-23.
 */

public class SearchEntryFrag extends Fragment{

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragement_search_entry, container, false);
		return view;
	}
}
