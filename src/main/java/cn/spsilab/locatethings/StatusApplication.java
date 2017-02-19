package cn.spsilab.locatethings;

import android.app.Application;

/**
 * Created by Feng on 2/19/2017.
 *
 */

public class StatusApplication extends Application {

    private int loginStatus;
    private String token;

    private static StatusApplication statusApplication;


    @Override
    public void onCreate() {
        super.onCreate();
        loginStatus = R.integer.LOGOUT;
    }

    public static StatusApplication getInstance() {
        if (statusApplication == null) {
            statusApplication = new StatusApplication();
        }
        return statusApplication;
    }

    public int getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(int loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
