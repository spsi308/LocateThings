package cn.spsilab.locatethings;

import java.util.Map;

import cn.spsilab.locatethings.module.ResponseResult;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Feng on 2/19/2017.
 * all request api list in here
 */

public interface APIService {

    @POST("/api/login")
    Call<ResponseResult<Map<String, Object>>> login(@Query("name") String userName, @Query("password") String password);

    @POST("/api/regist")
    Call<ResponseResult<Map<String, Object>>> regist(@Query("name") String userName, @Query("phone") String phone, @Query("password") String password);

    @GET
    Call<ResponseBody> getPicture(@Url String url);
}
