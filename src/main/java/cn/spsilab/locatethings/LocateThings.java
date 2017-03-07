package cn.spsilab.locatethings;

import android.app.Application;

import cn.spsilab.locatethings.bluetooth.BluetoothService;
import cn.spsilab.locatethings.module.User;
import cn.spsilab.locatethings.tag.TagModuleOperateService;

/**
 * Created by changrq on 17-2-21.
 */

public class LocateThings extends Application {
    private int bluetoothStatus;
    private BluetoothService bluetoothService;
    private boolean moduleInProcessing;
    private int loginStatus;
    private String token;
    private User user;

    @Override
    public void onCreate() {
        super.onCreate();
        loginStatus = R.integer.LOGOUT;
    }

    public void setModuleInProcessing(boolean moduleInProcessing) {
        this.moduleInProcessing = moduleInProcessing;
    }

    public boolean isModuleInProcessing() {
        return moduleInProcessing;
    }

    public BluetoothService getBluetoothService() {
        return bluetoothService;
    }

    public void setBluetoothService(BluetoothService bluetoothService) {
        this.bluetoothService = bluetoothService;
    }

    public int getBluetoothStatus() {
        return bluetoothStatus;
    }

    public void setBluetoothStatus(int bluetoothStatus) {
        this.bluetoothStatus = bluetoothStatus;
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
