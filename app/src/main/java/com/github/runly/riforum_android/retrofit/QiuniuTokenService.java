package com.github.runly.riforum_android.retrofit;

import com.github.runly.riforum_android.qiniu.QiniuToken;
import com.github.runly.riforum_android.model.ResponseBase;

import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by ranly on 17-2-15.
 */

public interface QiuniuTokenService {
    /**
     *
     * @param body
     * @return
     */
    @POST("qiniu/token")
    Observable<ResponseBase<QiniuToken>> qiniuToken(@Body Map<String, Object> body);
}
