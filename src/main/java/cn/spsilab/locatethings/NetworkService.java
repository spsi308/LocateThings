package cn.spsilab.locatethings;

import java.util.concurrent.TimeUnit;

import cn.spsilab.locatethings.module.ResponseResult;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * some method about network
 * Created by Feng on 2/19/2017.
 */

public class NetworkService {

    private static NetworkService networkService;
    private final String TAG = "NetworkService";
    private Retrofit retrofit;
    private APIService apiService;
    private OkHttpClient.Builder httpClient;

    private NetworkService() {

        final String BASE_URL = "http://192.168.1.84:5000/";
        httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(3000, TimeUnit.MILLISECONDS);
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
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
     * getApiService
     */
    public <S> S getService(Class<S> serviceClass) {
        // cache
        if (serviceClass == APIService.class && apiService != null) {
            return (S) apiService;
        }
        if (serviceClass == APIService.class) {
            apiService = (APIService) retrofit.create(serviceClass);
            return (S) apiService;
        }
        return retrofit.create(serviceClass);
    }


    /**
     *
     */
    public interface NetworkCallback {
        void onSuccess(ResponseResult result);

        void onFailure(ResponseResult result, Throwable t);
    }
}
