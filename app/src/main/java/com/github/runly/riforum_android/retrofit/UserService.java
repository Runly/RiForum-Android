package com.github.runly.riforum_android.retrofit;

import com.github.runly.riforum_android.model.Login;
import com.github.runly.riforum_android.model.SignIn;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by ranly on 17-2-13.
 */

public interface UserService {

    /**
     *
     * @param body
     * @return Observable<Login>
     */
    @POST("login")
    Observable<Login> login(@Body Map<String, String> body);

    /**
     *
     * @param body
     * @return Observable<SignIn>
     */
    @POST("sign_in")
    Observable<SignIn> siginIn(@Body Map<String, String> body);
}
