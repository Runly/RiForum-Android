package com.github.runly.riforum_android.retrofit;

import com.github.runly.riforum_android.model.Comment;
import com.github.runly.riforum_android.model.ResponseBase;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by ranly on 17-2-23.
 */

public interface CommentService {

    /**
     *
     * @param body
     * @return
     */
    @POST("comment/comment")
    Observable<ResponseBase<Comment>> comment(@Body Map<String, Object> body);

    /**
     *
     * @param body
     * @return
     */
    @POST("comment/comment_list")
    Observable<ResponseBase<List<Comment>>>comment_list(@Body Map<String, Object> body);
}
