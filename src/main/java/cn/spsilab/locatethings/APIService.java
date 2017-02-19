package cn.spsilab.locatethings;

import cn.spsilab.locatethings.module.ResponseResult;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Feng on 2/19/2017.
 * all request api list in here
 */

public interface APIService {

    @POST("/login")
    Call<ResponseResult> login(@Query("name") String userName, @Query("password") String password);

}
