package cn.spsilab.locatethings;

import android.app.Application;

import cn.spsilab.locatethings.bluetooth.BluetoothService;
import cn.spsilab.locatethings.module.User;

/**
 * Created by changrq on 17-2-21.
 */

public class LocateThings extends Application {
    private int bluetoothStatus;
    private BluetoothService bluetoothService;
    private int loginStatus;
    private String token;
    private User user;

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
