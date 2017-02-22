package cn.spsilab.locatethings;

import java.util.Map;

import cn.spsilab.locatethings.module.ResponseResult;
import cn.spsilab.locatethings.module.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Feng on 2/19/2017.
 * all request api list in here
 */

public interface APIService {

    // user
    @POST("/api/login")
    Call<ResponseResult<Map<String, Object>>> login(@Query("name") String userName, @Query("password") String password);

    @POST("/api/regist")
    Call<ResponseResult<Map<String, Object>>> regist(@Query("name") String userName, @Query("phone") String phone, @Query("password") String password);

    @GET("/api/user/info/{id}")
    Call<ResponseResult<Map<String, Object>>> getUserInfo(@Path("id") long id);

    @POST("/api/user/info/update")
    Call<ResponseResult<Map<String, Object>>> updateUserInfo(@Body User user);

/*    // Item
    @POST("/api/item/add")
    Call<ResponseResult<Map<String, Object>>> addItem(@Body User user);

    @GET("/api/item/info/{id}")
    Call<ResponseResult<Map<String, Object>>> getItem(@Path("id") int id);

    @POST("/api/item/update")
    Call<ResponseResult<Map<String, Object>>> updateItem(@Body User user);

    @POST("/api/item/delete/{id}")
    Call<ResponseResult<Map<String, Object>>> deleteItem(@Path("id") int id);

    // tag
    @POST("/api/tag/add")
    Call<ResponseResult<Map<String, Object>>> addTag(@Body User user);

    @GET("/api/tag/info/{id}")
    Call<ResponseResult<Map<String, Object>>> getTag(@Path("id") int id);

    @POST("/api/item/delete/{id}")
    Call<ResponseResult<Map<String, Object>>> deleteTag(@Path("id") int id);*/

    @GET
    Call<ResponseBody> getPicture(@Url String url);
}
