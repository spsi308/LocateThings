package cn.spsilab.locatethings;

import java.util.Map;

import cn.spsilab.locatethings.module.ResponseResult;
import cn.spsilab.locatethings.module.User;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by Feng on 2/19/2017.
 * all request api list in here
 */

public interface APIService {


    @FormUrlEncoded
    @POST("/api/login")
    Call<ResponseResult<Map<String, Object>>> login(@Field("name") String userName, @Field("password") String password);

    @FormUrlEncoded
    @POST("/api/regist")
    Call<ResponseResult<Map<String, Object>>> regist(
            @Field("name") String userName,
            @Field("phone") String phone,
            @Field("password") String password);

    @GET("/api/user/{id}")
    Call<ResponseResult<Map<String, Object>>> getUserInfo(@Path("id") long id);

    @POST("/api/user/update")
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

    /**
     * upload image result contain the image url
     *
     * @param file
     * @return
     */
    @Multipart
    @POST("/api/image/upload")
    Call<ResponseResult<Map<String, Object>>> uploadImage(@Part MultipartBody.Part file);

    @Multipart
    @POST("/api/image/change")
    Call<ResponseResult<Map<String, Object>>> upload(
            @Part("id") RequestBody id,
            @Part("type") RequestBody type,
            @Part MultipartBody.Part file);




}
