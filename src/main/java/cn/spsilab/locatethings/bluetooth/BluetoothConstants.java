package cn.spsilab.locatethings.bluetooth;

/**
 * Created by changrq on 17-2-19.
 */

public class BluetoothConstants {

    public static final int MESSAGE_READ = 0;
    public static final int MESSAGE_WRITE = 1;
    public static final int MESSAGE_NON_CONNECTION = 2;

    public static final int SUCCESSFUL_CONNECT = 3;
    public static final int CONNECTION_DISCONNECTED = 4;

    public static final int STATUS_CONNECTION_CLOSED = 5;
    public static final int STATUS_CONNECTION_FAILED = 6;
    public static final int STATUS_CONNECTION_LOST = 7;
    public static final int STATUS_CONNECTED_TO_RELAY_STATION = 8;

    public static final String ACTION_BLUETOOTH_RECV_DATA = "ACTION_BLUETOOTH_RECV_DATA";

    public static final String EXTRA_RECV_BYTES = "RECV_BYTES";

    public static final String EXTRA_RECV_BYTES_NUM = "RECV_BYTES_NUM";




}
