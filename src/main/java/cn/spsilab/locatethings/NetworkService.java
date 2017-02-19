package cn.spsilab.locatethings;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;

import cn.spsilab.locatethings.module.ResponseResult;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Feng on 2/19/2017.
 *
 */

public class NetworkService {

    private Retrofit retrofit;
    private APIService apiService;
    private static NetworkService networkService;
    private final String TAG = "NetworkService";


    private NetworkService() {

        final String BASE_URL = "http://192.168.0.100:8080/";
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        // TODO: 2/19/2017 change the log level
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(loggingInterceptor);

         retrofit = builder.client(httpClient.build()).build();
    }

    public static NetworkService getInstance() {
        if (networkService == null) {
            networkService = new NetworkService();
        }
        return networkService;
    }

    public <S> S getService(Class<S> serviceClass) {
        // cache
        if (serviceClass == APIService.class && apiService != null) {
            return (S) apiService;
        }
        if (serviceClass == APIService.class) {
            apiService = (APIService) retrofit.create(serviceClass);
            return (S)apiService;
        }
        return retrofit.create(serviceClass);
    }

    /**
     * 判断是否登陆
     * @param context context
     * @return login/logout
     */
    public static boolean checkIsLogin(Context context) {

        StatusApplication statusApplication = (StatusApplication)context.getApplicationContext();
        return statusApplication.getLoginStatus() == context.getResources().getInteger(R.integer.LOGIN);
    }

    /**
     * 自动登录
     * @param context
     * @throws RuntimeException 未实现接口
     */
    public void autoLogin(Context context) throws RuntimeException{
        if(!(context instanceof NetworkCallback)) {
            throw new RuntimeException("context must implements LoginUtil.LoginCallback interface");
        }
        NetworkCallback loginCallback = (NetworkCallback) context;
        if (!checkIsLogin(context)) {
            loginCallback.onSuccess(ResponseResult.build(idTOInt(context, R.integer.LOGIN_SUCCESS), "Login success"));
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(idToString(context, R.string.PREFERENCE_FILE_KEY), Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString(idToString(context, R.string.USER_NAME), null);
        String password = sharedPreferences.getString(idToString(context, R.string.PASSWORD), null);
        if (userName == null || password == null) {
            loginCallback.onFailure(ResponseResult.build(idTOInt(context, R.integer.LOGIN_FAILED), "Need login"), new RuntimeException("not found saved user info"));
        }
        //todo:判断token是否过期，是否有效，
        {}
        login(userName, password, context);

    }

    /**
     * 登录方法
     * @param userName 用户名
     * @param password 密码
     * @param context 上下文，用于获取shared preference
     * @return 登陆的结果
     */
    public void login(final String userName, final String password, final Context context) {

        getService(APIService.class);

        if(!(context instanceof NetworkCallback)) {
            throw new RuntimeException("context must implements LoginUtil.LoginCallback interface");
        }
        final NetworkCallback loginCallback = (NetworkCallback) context;
        // TODO: 2/19/2017
        Call<ResponseResult> call = apiService.login(userName, password);
        call.enqueue(new Callback<ResponseResult>() {
            @Override
            public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "login failed");
                    loginCallback.onFailure(new ResponseResult<>(idTOInt(context, R.integer.LOGIN_FAILED), "login failed", response.errorBody()), new RuntimeException("login failed,maybe you input a wrong username or password"));
                    return;
                }
                ResponseResult<Map<String, String>> result = response.body();
                if (result.getStatus() == idTOInt(context, R.integer.LOGIN_SUCCESS)) {
                    //login success
                    Log.d(TAG, "login success");
                    //todo: 需要协商
                    String token = result.getData().get("token");
                    saveInSharedPeference(userName, password, token, context);

                    StatusApplication statusApplication = (StatusApplication)context.getApplicationContext();
                    statusApplication.setLoginStatus(idTOInt(context, R.integer.LOGIN));
                    statusApplication.setToken(token);
                    loginCallback.onSuccess(new ResponseResult<ResponseResult>(idTOInt(context, R.integer.LOGIN_SUCCESS), "login success", result));
                } else {
                    loginCallback.onFailure(new ResponseResult<>(idTOInt(context, R.integer.NO_CONNECTION), "login failed", result), new RuntimeException("login failed,maybe you input a wrong username or password"));
                }
            }

            @Override
            public void onFailure(Call<ResponseResult> call, Throwable t) {
                loginCallback.onFailure(new ResponseResult<>(idTOInt(context, R.integer.NO_CONNECTION), "connect server failed"), t);
            }
        });


    }

    /**
     * 退出登录
     * @param context
     * TODO
     */
    public void logout(Context context) {
        StatusApplication statusApplication = (StatusApplication)context.getApplicationContext();
        statusApplication.setLoginStatus(idTOInt(context, R.integer.LOGIN));
        statusApplication.setToken(null);
        SharedPreferences sh = context.getSharedPreferences(idToString(context, R.string.PREFERENCE_FILE_KEY),Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sh.edit();
        edit.remove(idToString(context, R.string.USER_NAME));
        edit.remove(idToString(context, R.string.PASSWORD));
        edit.remove(idToString(context, R.string.TOKEN));
        edit.commit();
        Log.d(TAG, "logout success");
    }

    /**
     * 将状态存入sharedpeference
     * todo : 将存入内容换为bean
     */
    private void saveInSharedPeference(String userName, String password, String token, Context context) {
        //全局共享
        SharedPreferences sh = context.getSharedPreferences(idToString(context, R.string.PREFERENCE_FILE_KEY),Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sh.edit();
        edit.putString(idToString(context, R.string.USER_NAME), userName);
        edit.putString(idToString(context, R.string.PASSWORD), password);
        edit.putString(idToString(context, R.string.TOKEN), token);
        edit.commit();
    }

    /**
     * convert id to string
     */
    private String idToString(Context context, int id){
        return context.getString(id);
    }

    /**
     * convert id to int
     */
    private int idTOInt(Context context, int id) {
        return context.getResources().getInteger(id);
    }




    /**
     *
     */
    public interface NetworkCallback {
        void onSuccess(ResponseResult result);
        void onFailure(ResponseResult result, Throwable t);
    }
}
