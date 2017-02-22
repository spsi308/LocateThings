package cn.spsilab.locatethings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.internal.LinkedTreeMap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import cn.spsilab.locatethings.module.ResponseResult;
import cn.spsilab.locatethings.module.User;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
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

    private static NetworkService networkService;
    private final String TAG = "NetworkService";
    private Retrofit retrofit;
    private APIService apiService;
    private OkHttpClient.Builder httpClient;

    private NetworkService() {

        final String BASE_URL = "http://192.168.0.100:8080/";
        httpClient = new OkHttpClient.Builder();

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

    /**
     * 判断是否登陆
     *
     * @param context context
     * @return login/logout
     */
    public static boolean checkIsLogin(Context context) {

        StatusApplication statusApplication = (StatusApplication) context.getApplicationContext();
        return statusApplication.getLoginStatus() == context.getResources().getInteger(R.integer.LOGIN);
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
        // TODO: 2/20/2017 判断token是否过期，是否有效，
        {}
        login(userName, password, context);

    }

    /**
     * 登录方法
     * @param userName 用户名(可以使id/手机号/用户名)
     * @param password 密码
     * @param context 上下文，用于获取shared preference
     * @return 登陆的结果 ps:
     *                   {
     *                     status : 200,
     *                     msg : 'login success',
     *                     data :
     *                     {
     *                            token : 'fdas',
     *                            id : '',
     *                            name : '',
     *                            photo : ''
     *                     }
     *                   }
     */
    public void login(final String userName, final String password, final Context context) {

        getService(APIService.class);

        if(!(context instanceof NetworkCallback)) {
            throw new RuntimeException("context must implements LoginUtil.LoginCallback interface");
        }
        final NetworkCallback loginCallback = (NetworkCallback) context;
        Call<ResponseResult<Map<String, Object>>> call = apiService.login(userName, password);
        call.enqueue(new Callback<ResponseResult<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ResponseResult<Map<String, Object>>> call, Response<ResponseResult<Map<String, Object>>> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "login failed");
                    loginCallback.onFailure(new ResponseResult<>(idTOInt(context, R.integer.LOGIN_FAILED), "login failed", response.errorBody()), new RuntimeException("login failed,maybe you input a wrong username or password"));
                    return;
                }

                ResponseResult<Map<String, Object>> result = response.body();
                if (result.getStatus() == idTOInt(context, R.integer.LOGIN_SUCCESS)) {
                    //login success
                    Log.d(TAG, "login success");
                    save(result, password, context);
                    loginCallback.onSuccess(new ResponseResult<ResponseResult>(idTOInt(context, R.integer.LOGIN_SUCCESS), "login success", result));
                } else {
                    loginCallback.onFailure(new ResponseResult<>(idTOInt(context, R.integer.LOGIN_FAILED), "login failed", result), new RuntimeException("login failed,maybe you input a wrong username or password"));
                }
            }

            @Override
            public void onFailure(Call<ResponseResult<Map<String, Object>>> call, Throwable t) {
                loginCallback.onFailure(new ResponseResult<>(idTOInt(context, R.integer.NO_CONNECTION), "connect server failed"), t);
            }
        });


    }


    /**
     * 退出登录
     * @param context
     */
    public void logout(Context context) {
        StatusApplication statusApplication = (StatusApplication)context.getApplicationContext();
        statusApplication.setLoginStatus(idTOInt(context, R.integer.LOGIN));
        statusApplication.setToken(null);
        statusApplication.setUser(null);
        SharedPreferences sh = context.getSharedPreferences(idToString(context, R.string.PREFERENCE_FILE_KEY),Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sh.edit();
        edit.remove(idToString(context, R.string.USER_NAME));
        edit.remove(idToString(context, R.string.PASSWORD));
        edit.remove(idToString(context, R.string.TOKEN));
        edit.commit();
        Log.d(TAG, "logout success");
    }

    /**
     *
     *
     */
    public void regist(final String userName, final String phone, final String password, final Context context) {
        getService(APIService.class);

        if (!(context instanceof NetworkCallback)) {
            throw new RuntimeException("context must implements LoginUtil.LoginCallback interface");
        }
        final NetworkCallback loginCallback = (NetworkCallback) context;
        Call<ResponseResult<Map<String, Object>>> call = apiService.regist(userName, phone, password);
        call.enqueue(new Callback<ResponseResult<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ResponseResult<Map<String, Object>>> call, Response<ResponseResult<Map<String, Object>>> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "regist failed");
                    loginCallback.onFailure(new ResponseResult<>(idTOInt(context, R.integer.REGIST_FAILED), "regist failed", response.errorBody()), new RuntimeException("login failed,maybe you input a wrong username or password"));
                    return;
                }
                ResponseResult<Map<String, Object>> result = response.body();
                if (result.getStatus() == idTOInt(context, R.integer.REGIST_SUCCESS)) {
                    //regist success
                    Log.d(TAG, "regist success");
                    loginCallback.onSuccess(new ResponseResult<ResponseResult>(idTOInt(context, R.integer.REGIST_SUCCESS), "login success", result));
                } else {
                    Log.d(TAG, "regist failed " + result);
                    loginCallback.onFailure(new ResponseResult<>(idTOInt(context, R.integer.REGIST_FAILED), "login failed", result), new RuntimeException("login failed,maybe you input a wrong username or password"));
                }
            }

            @Override
            public void onFailure(Call<ResponseResult<Map<String, Object>>> call, Throwable t) {
                loginCallback.onFailure(new ResponseResult<>(idTOInt(context, R.integer.NO_CONNECTION), "connect server failed"), t);
            }
        });
    }



    /**
     * convert json object to bean
     * <h2>the double value will be cast to long</h2>
     * <hr>
     * @param classType convert object type
     * @param jsonObject json object
     * @param <T> return object type
     * @return the converted object
     *
     */
    private <T> T convertToUser(Class<T> classType, LinkedTreeMap jsonObject) {
        Field[] fields = classType.getDeclaredFields();
        T res = null;
        try {
            res = classType.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
        for (Field f : fields) {
            if (Modifier.isPrivate(f.getModifiers())) {
                String param = f.getName();
                Object args = jsonObject.get(param);
                if (args != null) {
                    try {
                        char[] chars = param.toCharArray();
                        chars[0] = (char) (chars[0] - 32);
                        Method m = classType.getMethod("set" + new String(chars), f.getType());

                        if (Modifier.isPrivate(m.getModifiers())) {
                            m.setAccessible(true);
                        }
                        // Gson will convert long to double
                        if (Double.class.equals(args.getClass())) {
                            args = ((Number) args).longValue();
                        }
                        m.invoke(res, args);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return res;
    }

    /**********************************************************************/

    private void save(ResponseResult<Map<String, Object>> result, String password, Context context) {

        LinkedTreeMap data = (LinkedTreeMap) result.getData();
        User u = convertToUser(User.class, data);
        if (u == null) {
            return;
        }
        Log.d(TAG, u.toString());
/*        String token = (String) result.getData().get("token");
//        long id = Long.parseLong((double)(result.getData().get("id")));
        long id = (long) (double) (result.getData().get("id"));
        String name = (String) result.getData().get("name");
        String photo = (String) result.getData().get("photo");
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setToken(token);
        user.setPhoto(photo);*/
        saveInSharedPeference(u, password, context);
        StatusApplication statusApplication = (StatusApplication) context.getApplicationContext();
        statusApplication.setLoginStatus(idTOInt(context, R.integer.LOGIN));
        statusApplication.setToken(u.getToken());
        statusApplication.setUser(u);
    }


    /**
     * 将状态存入sharedpeference
     */
    private void saveInSharedPeference(User user, String password, Context context) {
        //全局共享
        SharedPreferences sh = context.getSharedPreferences(idToString(context, R.string.PREFERENCE_FILE_KEY),Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sh.edit();
        edit.putLong(idToString(context, R.string.USER_ID), user.getId());
        edit.putString(idToString(context, R.string.USER_NAME), user.getName());
        edit.putString(idToString(context, R.string.PASSWORD), password);
        edit.putString(idToString(context, R.string.TOKEN), user.getToken());
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
     * if your url is /images/12.jpg, the request url will be baseurl + url
     * if url is http://ab.xyz/23.jpg the request url is url
     */
    public void getPicture(final String url, final ImageView show, final int defaultImgId) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        String[] us = url.split("/");
        String name = us[us.length - 1];
        String path = Environment.getExternalStorageDirectory() + "/image/" + name;
        Log.d(TAG, "save path:" + path);
        final File file = new File(path);

        if (!file.exists()) {
            getService(APIService.class);
            Call<ResponseBody> resp = apiService.getPicture(url);
            resp.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (file.createNewFile()) {
                            InputStream is = response.body().byteStream();
                            // save
                            BufferedInputStream bufferedInputStream = new BufferedInputStream(is);
                            try {
                                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
                                byte[] bytes = new byte[1024];
                                int l = 0;
                                while ((l = bufferedInputStream.read(bytes)) != -1) {
                                    bufferedOutputStream.write(bytes, 0, l);
                                }
                                bufferedOutputStream.flush();
                                bufferedOutputStream.close();
                                bufferedInputStream.close();
                            } catch (java.io.IOException e) {
                                e.printStackTrace();
                            }

                            Bitmap img = BitmapFactory.decodeFile(file.getAbsolutePath());
                            if (img == null) {
                                show.setImageResource(defaultImgId);
                            } else {
                                show.setImageBitmap(img);
                            }
                        }
                    } catch (IOException e) {
                        show.setImageResource(defaultImgId);
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    show.setImageResource(defaultImgId);
                }
            });

        } else {
            Bitmap img = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (img == null) {
                show.setImageResource(defaultImgId);
            } else {
                show.setImageBitmap(img);
            }
        }


    }


    /**
     *
     */
    public interface NetworkCallback {
        void onSuccess(ResponseResult result);
        void onFailure(ResponseResult result, Throwable t);
    }
}
