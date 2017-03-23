package com.github.runly.riforum_android.retrofit;

import com.github.runly.riforum_android.model.Entry;
import com.github.runly.riforum_android.model.Plate;
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
     * @param body
     * @return
     */
    @POST("entry/recommend")
    Observable<ResponseBase<List<Entry>>> recommend(@Body Map<String, Object> body);

    /**
     * @param body
     * @return
     */
    @POST("entry/user_release")
    Observable<ResponseBase<List<Entry>>> user_release(@Body Map<String, Object> body);

    /**
     *
     * @return
     */
    @GET("entry/all_plate_entries")
    Observable<ResponseBase<List<Entry>>> all_plate_entries();

    /**
     *
     * @return
     */
    @POST("entry/plate_entries")
    Observable<ResponseBase<List<Entry>>> plate_entries(@Body Map<String, Object> body);

    /**
     *
     * @return
     */
    @GET("entry/plate")
    Observable<ResponseBase<List<Plate>>> plate();

    /**
     *
     * @return
     */
    @GET("entry/banner_entries")
    Observable<ResponseBase<List<Entry>>> banner_entries();


    @POST("entry/search")
    Observable<ResponseBase<Entry>> search(@Body Map<String, Object> body);

}
