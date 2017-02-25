package cn.spsilab.locatethings;

import android.app.Application;

import cn.spsilab.locatethings.bluetooth.BluetoothService;

/**
 * Created by changrq on 17-2-21.
 */

public class LocateThings extends Application {
    private int bluetoothStatus;
    private BluetoothService bluetoothService;

    public void setBluetoothService(BluetoothService bluetoothService) {
        this.bluetoothService = bluetoothService;
    }

    public void setBluetoothStatus(int bluetoothStatus) {
        this.bluetoothStatus = bluetoothStatus;
    }

    public BluetoothService getBluetoothService() {
        return bluetoothService;
    }

    public int getBluetoothStatus() {
        return bluetoothStatus;
    }


}
