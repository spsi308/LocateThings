package cn.spsilab.locatethings;

import android.app.Application;

import cn.spsilab.locatethings.module.User;

/**
 * Created by Feng on 2/19/2017.
 *
 */

public class StatusApplication extends Application {

    private static StatusApplication statusApplication;
    private int loginStatus;
    private String token;
    private User user;

    public static StatusApplication getInstance() {
        if (statusApplication == null) {
            statusApplication = new StatusApplication();
        }
        return statusApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        loginStatus = R.integer.LOGOUT;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
