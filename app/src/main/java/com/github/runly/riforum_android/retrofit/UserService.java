package com.github.runly.riforum_android.retrofit;

import com.github.runly.riforum_android.model.ResponseBase;
import com.github.runly.riforum_android.model.User;

import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by ranly on 17-2-13.
 */

public interface UserService {

    /**
     *
     * @param body
     * @return
     */
    @POST("user/login")
    Observable<ResponseBase<User>> login(@Body Map<String, Object> body);

    /**
     *
     * @param body
     * @return
     */
    @POST("user/sign_in")
    Observable<ResponseBase<User>> signIn(@Body Map<String, Object> body);

    /**
     *
     * @param body
     * @return
     */
    @POST("user/modify_info")
    Observable<ResponseBase<User>> modify_info(@Body Map<String, Object> body);
}
