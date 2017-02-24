package com.github.runly.riforum_android.interfaces;

import com.github.runly.riforum_android.model.Comment;

/**
 * Created by ranly on 17-2-24.
 */

public interface OnCommentedListener {
    void onCommented(Comment comment, int position);
}
