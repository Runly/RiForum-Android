package com.github.runly.riforum_android.retrofit;

import com.github.runly.riforum_android.model.Entry;
import com.github.runly.riforum_android.model.ResponseBase;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by ranly on 17-2-16.
 */

public interface EntryService {
    /**
     *
     * @param body
     * @return
     */
    @POST("entry/release")
    Observable<ResponseBase<Entry>> release(@Body Map<String, Object> body);

    /**
     *
     * @return
     */
    @GET("entry/recommend")
    Observable<ResponseBase<List<Entry>>> recommend();
}
