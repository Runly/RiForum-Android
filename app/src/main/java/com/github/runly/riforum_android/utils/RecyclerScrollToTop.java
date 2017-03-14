package com.github.runly.riforum_android.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by ranly on 17-3-14.
 */

public class RecyclerScrollToTop {
    public static void scrollToTop(RecyclerView recyclerView) {
        LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
        int currentPosition = lm.findFirstVisibleItemPosition();
        if (currentPosition > 10) {
            lm.scrollToPosition(10);
        }
        lm.smoothScrollToPosition(recyclerView, null, 0);
    }
}
