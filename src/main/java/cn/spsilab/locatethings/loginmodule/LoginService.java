package cn.spsilab.locatethings.loginmodule;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.internal.LinkedTreeMap;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import cn.spsilab.locatethings.APIService;
import cn.spsilab.locatethings.LocateThings;
import cn.spsilab.locatethings.NetworkService;
import cn.spsilab.locatethings.R;
import cn.spsilab.locatethings.module.ResponseResult;
import cn.spsilab.locatethings.module.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Feng on 2/26/2017.
 * some method about login,regist and change password
 */

public class LoginService {

    private static final String TAG = "Login Service";

    public static boolean checkIsLogin(Context context) {

        LocateThings statusApplication = (LocateThings) context.getApplicationContext();
        if (statusApplication.getLoginStatus() == context.getResources().getInteger(R.integer.LOGIN)) {
            Log.d(TAG, "status application login success");
            return true;
        } else {
            // if application don't have, but sharepreference login status is login,
            // set application login status to login
            SharedPreferences sh = context.getSharedPreferences(context.getString(R.string.PREFERENCE_FILE_KEY), Context.MODE_PRIVATE);
            int status = sh.getInt(context.getString(R.string.LOGIN_STATUS), 0);
            int login = context.getResources().getInteger(R.integer.LOGIN);
            Log.d(TAG, "login status:" + status);
            if (status == login) {
                // from sharedprefrence get all info
                String name = sh.getString(context.getString(R.string.USER_NAME), null);
                String phone = sh.getString(context.getString(R.string.PHONE), null);
                String token = sh.getString(context.getString(R.string.TOKEN), null);
                String photo = sh.getString(context.getString(R.string.PHOTO), null);
                long id = sh.getLong(context.getString(R.string.USER_ID), 0);
                int networkNo = sh.getInt(context.getString(R.string.NETWORK_NO), 0);
                int channelNo = sh.getInt(context.getString(R.string.CHANNEL_NO), 0);
                User user = new User();
                user.setId(id);
                user.setName(name);
                user.setPhoto(photo);
                user.setPhone(phone);
                user.setToken(token);
                user.setNetworkNo(networkNo);
                user.setChannelNo(channelNo);
                statusApplication.setUser(user);
                statusApplication.setToken(token);
                statusApplication.setLoginStatus(login);
                return true;
            }
            return false;
        }
    }


    /**
     * 登录方法
     *
     * @param userName id/name/phone
     * @param password pass
     * @param context
     * @return return result ps:
     * {
     * status : 200,
     * msg : 'login success',
     * data :
     * {
     * token : 'fdas',
     * id : '',
     * name : '',
     * photo : ''
     * }
     * }
     */
    public void login(final String userName, final String password, final Context context) {

        APIService apiService = NetworkService.getInstance().getService(APIService.class);

        if (!(context instanceof NetworkService.NetworkCallback)) {
            throw new RuntimeException("context must implements LoginUtil.LoginCallback interface");
        }
        final NetworkService.NetworkCallback loginCallback = (NetworkService.NetworkCallback) context;
        Call<ResponseResult<Map<String, Object>>> call = apiService.login(userName, password);
        call.enqueue(new Callback<ResponseResult<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ResponseResult<Map<String, Object>>> call,
                                   Response<ResponseResult<Map<String, Object>>> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "login failed, the server may occur some error");
                    loginCallback.onFailure(
                            new ResponseResult<>(
                                    idTOInt(context, R.integer.LOGIN_FAILED),
                                    "login failed",
                                    response.errorBody()),
                            new RuntimeException("login failed,maybe you input a wrong username or password"));
                    return;
                }

                ResponseResult<Map<String, Object>> result = response.body();
                if (result.getStatus() == idTOInt(context, R.integer.LOGIN_SUCCESS)) {
                    //login success
                    Log.d(TAG, "login success");
                    save(result, password, context);
                    loginCallback.onSuccess(new ResponseResult<ResponseResult>(idTOInt(context, R.integer.LOGIN_SUCCESS), "login success", result));
                } else {
                    loginCallback.onFailure(
                            new ResponseResult<>(
                                    idTOInt(context, R.integer.LOGIN_FAILED),
                                    "login failed, your user or password is wrong",
                                    result),
                            new RuntimeException("login failed,maybe you input a wrong username or password"));
                }
            }

            @Override
            public void onFailure(Call<ResponseResult<Map<String, Object>>> call, Throwable t) {
                loginCallback.onFailure(new ResponseResult<>(idTOInt(context, R.integer.NO_CONNECTION), "no connect, please check your network"), t);
            }
        });


    }


    /**
     * logout
     */
    public void logout(Context context) {
        LocateThings statusApplication = (LocateThings) context.getApplicationContext();
        statusApplication.setLoginStatus(idTOInt(context, R.integer.LOGOUT));
        statusApplication.setToken(null);
        statusApplication.setUser(null);
        SharedPreferences sh = context.getSharedPreferences(idToString(context,
                R.string.PREFERENCE_FILE_KEY), Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sh.edit();
        edit.remove(idToString(context, R.string.USER_NAME));
        edit.remove(idToString(context, R.string.PASSWORD));
        edit.remove(idToString(context, R.string.TOKEN));
        edit.remove(idToString(context, R.string.LOGIN_STATUS));
        edit.remove(idToString(context, R.string.PHOTO));
        edit.commit();
        Log.d(TAG, "logout success");
    }

    /**
     * handler in login activity
     */
    public void regist(final String userName, final String phone, final String password, final Context context) {
        APIService apiService = NetworkService.getInstance().getService(APIService.class);

        if (!(context instanceof NetworkService.NetworkCallback)) {
            throw new RuntimeException("context must implements LoginUtil.LoginCallback interface");
        }
        final NetworkService.NetworkCallback loginCallback = (NetworkService.NetworkCallback) context;
        Call<ResponseResult<Map<String, Object>>> call = apiService.regist(userName, phone, password);
        call.enqueue(new Callback<ResponseResult<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ResponseResult<Map<String, Object>>> call, Response<ResponseResult<Map<String, Object>>> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "regist failed");
                    loginCallback.onFailure(
                            new ResponseResult<>(idTOInt(context, R.integer.REGIST_FAILED),
                                    "regist failed, please check your input is true",
                                    response.errorBody()),
                            new RuntimeException("login failed,maybe you input a wrong username or password"));
                    return;
                }
                ResponseResult<Map<String, Object>> result = response.body();
                if (result.getStatus() == idTOInt(context, R.integer.REGIST_SUCCESS)) {
                    //regist success
                    Log.d(TAG, "regist success");
                    loginCallback.onSuccess(new ResponseResult<ResponseResult>(idTOInt(context, R.integer.REGIST_SUCCESS), "regist success", result));
                } else {
                    Log.d(TAG, "regist failed " + result);
                    loginCallback.onFailure(
                            new ResponseResult<>(
                                    idTOInt(context, R.integer.REGIST_FAILED),
                                    "regist failed",
                                    result),
                            new RuntimeException("login failed,maybe you input a wrong username or password"));
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
     *
     * @param classType  convert object type
     * @param jsonObject json object
     * @param <T>        return object type
     * @return the converted object
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
                        // Gson will convert number to double
                        if (Double.class.equals(args.getClass())) {
                            Log.d(TAG, f.getType().toString());
                            if (f.getType() == Integer.TYPE) {
                                args = ((Number) args).intValue();
                            } else if (f.getType() == Long.TYPE) {
                                args = ((Number) args).longValue();
                            } else if (f.getType() == Byte.TYPE) {
                                args = ((Number) args).byteValue();
                            } else if (f.getType() == Float.TYPE) {
                                args = ((Number) args).floatValue();
                            }
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

    /**
     * save user in application
     *
     * @param result
     * @param password
     * @param context
     */
    private void save(ResponseResult<Map<String, Object>> result, String password, Context context) {

        LinkedTreeMap data = (LinkedTreeMap) result.getData();
        User u = convertToUser(User.class, data);
        if (u == null) {
            return;
        }
        Log.d(TAG, u.toString());
        saveInSharedPeference(u, password, context);
        LocateThings statusApplication = (LocateThings) context.getApplicationContext();
        statusApplication.setLoginStatus(idTOInt(context, R.integer.LOGIN));
        statusApplication.setToken(u.getToken());
        statusApplication.setUser(u);
    }


    /**
     * save user,password in sharedpeference for auto login
     */
    private void saveInSharedPeference(User user, String password, Context context) {
        SharedPreferences sh = context.getSharedPreferences(idToString(context, R.string.PREFERENCE_FILE_KEY), Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sh.edit();
        edit.putLong(idToString(context, R.string.USER_ID), user.getId());
        edit.putString(idToString(context, R.string.USER_NAME), user.getName());
        edit.putString(idToString(context, R.string.PASSWORD), password);
        edit.putString(idToString(context, R.string.TOKEN), user.getToken());
        edit.putString(idToString(context, R.string.PHOTO), user.getPhoto());
        edit.putInt(idToString(context, R.string.LOGIN_STATUS), idTOInt(context, R.integer.LOGIN));
        edit.commit();
    }

    /**
     * convert id to string
     */
    private String idToString(Context context, int id) {
        return context.getString(id);
    }

    /**
     * convert id to int
     */
    private int idTOInt(Context context, int id) {
        return context.getResources().getInteger(id);
    }


}
