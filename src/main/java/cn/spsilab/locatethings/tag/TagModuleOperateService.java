package cn.spsilab.locatethings.tag;

import android.app.Activity;
import android.app.Application;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;

import cn.spsilab.locatethings.LocateThings;
import cn.spsilab.locatethings.bluetooth.BluetoothConstants;
import cn.spsilab.locatethings.bluetooth.BluetoothService;

/**
 * Created by changrq on 17-2-20.
 */

public class TagModuleOperateService {

    private final String TAG = TagModuleOperateService.class.toString();

    private final int MAC_BYTE_SIZE = 8;

    private final String OP_QEERY = "FT+BCT";
    private final String OP_BLINK_PRE = "FT+BLK";
    private final String OP_CALL_PRE = "FT+CAL=";

    private final int BLINK_SEND_INTERVAL = 1;
    private final int CALL_SEND_INTERVAL = 1;
    private final int QUERY_TIME = 12;

    private final BluetoothService mBluetoothSerive;
    private final Activity mBindActivity;

    private final AddTagHandler mAddTagHandler;

    private SendOpThread mSendOpThread;

    private boolean hasBroadCastReceiver = false;

    private ArrayList<Byte> mRecvDataBuf;


    private Handler mTimeOutHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage: time out!");

            Intent searchTagModuleTimeOut = new Intent();
            searchTagModuleTimeOut.setAction("ACTION_FINISH_SEARCH_TAG");

            if (mSendOpThread == null) {
                LocalBroadcastManager.getInstance(mBindActivity).sendBroadcast(searchTagModuleTimeOut);
            }
        }
    };


    private BroadcastReceiver mBluetoothDataRecvBroadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] recvBytes = intent.getByteArrayExtra(BluetoothConstants.EXTRA_RECV_BYTES);
            int recvBytesNum = intent.getIntExtra(BluetoothConstants.EXTRA_RECV_BYTES_NUM, 0);

            Log.d(TAG, "onReceive: recvNum " + recvBytesNum);

            for(int i = 0; i < recvBytesNum; i++) {
                mRecvDataBuf.add(recvBytes[i]);
                if (mRecvDataBuf.size() == MAC_BYTE_SIZE) {
                    int[] recvInInt = byteToInt();
                    String hexMacAddr = intToHex(recvInInt);

                    TagModule newTag = new TagModule();
                    newTag.setModuleId(0);

                    newTag.setModuleMAC(hexMacAddr);
                    Log.d(TAG, "onReceive: tagModuleService recv str: " + hexMacAddr);

                    mAddTagHandler.onAddTag(newTag);

                    mRecvDataBuf.clear();
                }
            }
        }
    };

    public interface AddTagHandler {
        void onAddTag(TagModule newTag);
    }

    private int[] byteToInt() {

        int[] dataInt = new int[MAC_BYTE_SIZE];
        for(int i = 0; i < MAC_BYTE_SIZE; i++) {
            int t = mRecvDataBuf.get(i) & 0xFF;
            dataInt[i] = t;
            Log.d(TAG, "byteToInt: convert " + dataInt[i]);
        }

        return dataInt;
    }

    private String intToHex(int[] dataInt) {
        StringBuilder sb = new StringBuilder();
        for(int i  = 0; i < MAC_BYTE_SIZE; i++) {
            //Integer.toHexString(dataInt[i])
            sb.append(String.format("%02X", dataInt[i]));
        }
        return sb.toString();
    }

    public TagModuleOperateService(BluetoothService service, DialogFragment from) {
        mRecvDataBuf = new ArrayList<>();
        mBindActivity = from.getActivity();
        mBluetoothSerive = service;

        if (from instanceof AddTagHandler) {
            mAddTagHandler = (AddTagHandler) from;
        } else {
            mAddTagHandler = null;
        }
    }
    private void bindBroadCastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothConstants.ACTION_BLUETOOTH_RECV_DATA);

        LocalBroadcastManager.getInstance(mBindActivity)
                .registerReceiver(mBluetoothDataRecvBroadReceiver, filter);

        hasBroadCastReceiver = true;
    }

    public void closeService() {
        if (hasBroadCastReceiver) {
            LocalBroadcastManager.getInstance(mBindActivity).unregisterReceiver(mBluetoothDataRecvBroadReceiver);
        }
    }

    public synchronized void clearRunningThead() {
        Log.d(TAG, "clearRunningThead: clear!");

        if (mSendOpThread != null) {
            mSendOpThread.sendStop();
            mSendOpThread = null;
        }
    }

    /**
     * send a msg through bluetooth serial port to querypLog.d(TAG, "queryAvailableModule:  all available tag module's mac address.
     */
    public void queryAvailableModule() {

        if (mAddTagHandler == null) {
            throw new RuntimeException("handler can't be null");
        }

        if (!hasBroadCastReceiver) {
            bindBroadCastReceiver();
        }


        mBluetoothSerive.write(OP_QEERY.getBytes());
        Log.d(TAG, "queryAvailableModule: will search");
        mTimeOutHandler.sendEmptyMessageDelayed(0, QUERY_TIME*1000);


    }

    /**
     * convert a hex string formated to a byte[]
     * @param hexMac
     */
    private byte[] convertHexToByte(String hexMac) {
        byte[] macInByte = new byte[MAC_BYTE_SIZE];
        int count = 0;
        for(int i = 0; i < MAC_BYTE_SIZE * 2; i+=2) {
             macInByte[count++] = (byte) Integer.parseInt(hexMac.substring(i, i+2), 16);
        }

        return macInByte;
    }

    /**
     * send msg through bluetooth serial port to blink a specify module,
     * make module blink.
     * @param tagModule
     */
    public void blinkModule(TagModule tagModule) {
        clearRunningThead();

        Log.d(TAG, "blinkModule: create a blink thread. "
                + "bink mac " + tagModule.getModuleMAC());

        mSendOpThread = new SendOpThread(BLINK_SEND_INTERVAL,
                OP_BLINK_PRE, convertHexToByte(tagModule.getModuleMAC()));

        mSendOpThread.start();
    }

    /**
     * send msg through bluetooth serial port to a specify module,
     * make module beep.
     * @param tagModule
     */
    public void callModule(TagModule tagModule) {
        clearRunningThead();

        mSendOpThread = new SendOpThread(CALL_SEND_INTERVAL, OP_CALL_PRE,
                convertHexToByte(tagModule.getModuleMAC()));

        mSendOpThread.start();
    }

    /**
     * reset module's networkNo and channelNo to default.
     * @param tagModule
     */
    public void resetModule(TagModule tagModule) {
    }

    private class SendOpThread extends Thread {
        private final int timeInterval;
        private final byte[] sendBytes;
        private boolean stopFlag = false;

        public SendOpThread(int interval, String strOp,byte[] mac) {
            timeInterval = interval * 1000;

            if (mac != null) {
                int count  = 0;
                sendBytes = new byte[strOp.length() + MAC_BYTE_SIZE];

                for(byte b: strOp.getBytes()) {
                    sendBytes[count++] = b;
                }

                for(int i = 0;i < MAC_BYTE_SIZE; i++) {
                    sendBytes[count++] = mac[i];
                }
            } else {
                sendBytes = strOp.getBytes();
            }
        }
        private void sendStop() {
            stopFlag = true;
        }

        @Override
        public void run() {
            while(true) {
                try {
                    mBluetoothSerive.write(sendBytes);
                    sleep(timeInterval);

                    if (stopFlag) {
                        break;
                    }

                } catch (Exception e) {
                    Log.e(TAG, "run: sendOp thread exception.", e);
                    break;
                }
            }
        }
    }
}
