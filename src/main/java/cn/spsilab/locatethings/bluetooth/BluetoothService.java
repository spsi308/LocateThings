package cn.spsilab.locatethings.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import cn.spsilab.locatethings.LocateThings;

/**
 * Created by changrq on 17-2-19.
 */

public class BluetoothService {
    private final static UUID HC_05_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final static String TAG = BluetoothService.class.toString();
    private final int inputBufferSize = 1024;


    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;


    private final BluetoothAdapter mAdapter;
    private final Handler mMsgHandler;
    private Context mContext;

    public BluetoothService(Context context, BluetoothAdapter adapter, Handler handler) {
        mAdapter = adapter;
        mMsgHandler = handler;
        mContext = context;
    }

    /**
     * Clear all thread and stop connecting process.
     */
    public synchronized void clearConnection() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    }

    private void connectionLost() {
        clearConnection();
        ((LocateThings) ((Activity)mContext).getApplication()).setBluetoothStatus(
                BluetoothConstants.STATUS_CONNECTION_LOST);
    }

    private void connectionFailed() {
        clearConnection();
        ((LocateThings) ((Activity)mContext).getApplication()).setBluetoothStatus(
                BluetoothConstants.STATUS_CONNECTION_FAILED);
    }

    public void stop() {
        clearConnection();

        ((LocateThings) ((Activity)mContext).getApplication()).setBluetoothStatus(
                BluetoothConstants.STATUS_CONNECTION_CLOSED);
        ((LocateThings) ((Activity)mContext).getApplication()).setBluetoothService(null);
    }

    /**
     * Run a connect thread, establish a connection to specified bluetooth device.
     * @param device
     */
    public synchronized void connct(BluetoothDevice device) {
        clearConnection();

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
    }

    /**
     * Write to bluetooth that connected. if connection not establish,
     * then send a err msg.
     * @param bytes
     */
    public void write(byte[] bytes) {
        ConnectedThread writeThread;

        synchronized (this) {
            if (mConnectedThread == null) {
                // send a msg.
                mMsgHandler.obtainMessage(BluetoothConstants.MESSAGE_NON_CONNECTION);
                return;
            }
            writeThread = mConnectedThread;
        }
        writeThread.write(bytes);
    }

    private synchronized void connected(BluetoothSocket socket) {
        clearConnection();

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
    }

    private class ConnectThread extends Thread {

        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmpSocket = null;

            try {
                // get socket.
                tmpSocket = device.createRfcommSocketToServiceRecord(HC_05_UUID);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: Failed to create bluetooth socket", e);
            }
            mmSocket = tmpSocket;
        }

        @Override
        public void run() {
            // cancel discovery to speed up connection.
            mAdapter.cancelDiscovery();

            try {
                mmSocket.connect();
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread run: Socket failed to connect.", e);
                try {
                    mmSocket.close();
                } catch (IOException e1) {
                    Log.e(TAG, "ConnectThread run: Failed to close.", e);
                }
                connectionFailed();
            }

            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }

            connected(mmSocket);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: socket failed to colse", e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;

        private InputStream mmInputStream;
        private OutputStream mmOutputStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;

            InputStream tmpInputStream = null;
            OutputStream tmpOutputStream = null;

            try {
                tmpInputStream = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "ConnectedThread: failed to get input stream.", e);
            }

            try {
                tmpOutputStream = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "ConnectedThread: failed get output stream", e);
            }

            mmInputStream = tmpInputStream;
            mmOutputStream = tmpOutputStream;
        }

        @Override
        public void run() {
            mMsgHandler.obtainMessage(BluetoothConstants.SUCCESSFUL_CONNECT,
                    -1,
                    -1,
                    null).sendToTarget();

            while (true) {
                byte[] mInputBuffer = new byte[inputBufferSize];
                int byteNum = 0;
                try {
                    // reading from input stream.
                    byteNum = mmInputStream.read(mInputBuffer);
                    Log.d(TAG, "blueService inputStream recv.");

                    // send a LocalBroadcast
                    Intent broadIntent = new Intent();
                    broadIntent.setAction(BluetoothConstants.ACTION_BLUETOOTH_RECV_DATA);

                    broadIntent.putExtra(BluetoothConstants.EXTRA_RECV_BYTES, mInputBuffer);
                    broadIntent.putExtra(BluetoothConstants.EXTRA_RECV_BYTES_NUM, byteNum);

                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(broadIntent);

                    // send message to activity
                    mMsgHandler.obtainMessage(
                            BluetoothConstants.MESSAGE_READ,
                            byteNum,
                            0,
                            mInputBuffer).sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "inputStream: failed to read.", e);

                    connectionLost();
                    mMsgHandler.obtainMessage(
                            BluetoothConstants.CONNECTION_DISCONNECTED,
                            -1,
                            -1,
                            null).sendToTarget();
                    break;
                }
            }
        }

        public void write(byte[] bytes) {

            try {
                mmOutputStream.write(bytes);

                // echo write bytes.
                mMsgHandler.obtainMessage(
                        BluetoothConstants.MESSAGE_WRITE,
                        -1,
                        -1,
                        bytes).sendToTarget();

            } catch (IOException e) {
                Log.e(TAG, "write: output stream write bytes error.", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: failed to close socket.", e);
            }
        }
    }
}

