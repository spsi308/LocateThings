package cn.spsilab.locatethings.bluetooth;

import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.spsilab.locatethings.LocateThings;
import cn.spsilab.locatethings.R;

/**
 * Created by changrq on 17-2-20.
 */

public class SelectStationDialog extends DialogFragment implements DeviceRecyclerAdapter.DeviceSelectHandler {
    private final String TAG = SelectStationDialog.class.toString();

    private BluetoothAdapter mDefaultBluetoothAdapter;
    private BluetoothService mBlueService;

    private RecyclerView mRecyclerView;

    private TextView mStationStatusTextView;
    private TextView mSearchingTextView;

    private ProgressBar mSearchingProgressBar;
    private Button mRetryButton;

    private  DeviceRecyclerAdapter mRecyclerViewAdapter;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == BluetoothConstants.SUCCESSFUL_CONNECT) {
                Log.d(TAG, "handleMessage: successful make connection.");
                // set application: connnected.
                ((LocateThings) getActivity().getApplication()).setBluetoothStatus(
                        BluetoothConstants.STATUS_CONNECTED_TO_RELAY_STATION);

                // close select dialog.
                //SelectStationDialog.this.dismiss();
                getFragmentManager().popBackStack();
            }
        }
    };


    private BroadcastReceiver enableBluetoothBroadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

                Log.d(TAG, "onReceive: receive ->"+ state);
                if (state == BluetoothAdapter.STATE_ON) {
                    mDefaultBluetoothAdapter.startDiscovery();
                    mSearchingTextView.setText("寻找设备中.");
                }
            }
        }
    };

    private BroadcastReceiver discoveryDeviceBroadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice newFoundDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "onReceive: find device " + newFoundDevice.getName() + "\n"
                        + newFoundDevice.getAddress());

                // if the device was the first founded device hidden searching progress bar.
                if (mSearchingProgressBar.getVisibility() == View.VISIBLE) {
                    mSearchingProgressBar.setVisibility(View.INVISIBLE);
                    mSearchingTextView.setVisibility(View.INVISIBLE);
                }

                // add device.
                mRecyclerViewAdapter.addDevice(newFoundDevice);
            }

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action) &&
                    mSearchingProgressBar.getVisibility() == View.VISIBLE) {
                mSearchingProgressBar.setVisibility(View.INVISIBLE);
                mSearchingTextView.setText("未能找到设备.");
                mRetryButton.setVisibility(View.VISIBLE);
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDefaultBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBlueService = new BluetoothService(getActivity(), mDefaultBluetoothAdapter, mHandler);
        ((LocateThings) getActivity().getApplication()).setBluetoothService(mBlueService);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(discoveryDeviceBroadReceiver, filter);

        filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(enableBluetoothBroadReceiver, filter);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(discoveryDeviceBroadReceiver);
        getActivity().unregisterReceiver(enableBluetoothBroadReceiver);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_station_select, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_station_select_device_list);
        mStationStatusTextView = (TextView) view.findViewById(R.id.text_station_select_status);
        mSearchingTextView = (TextView) view.findViewById(R.id.text_station_select_search_device);
        mSearchingProgressBar = (ProgressBar) view.findViewById(R.id.progressbar_station_select_searing);
        mRetryButton = (Button) view.findViewById(R.id.btn_station_select_retry);

        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchingTextView.setText("寻找设备中.");
                //mDefaultBluetoothAdapter.cancelDiscovery();
                mDefaultBluetoothAdapter.startDiscovery();
                mSearchingProgressBar.setVisibility(View.VISIBLE);
                mRetryButton.setVisibility(View.INVISIBLE);
            }
        });

        // if bluetooth disable, start to enable bluetooth.
        if (!mDefaultBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBluetooth);
            mSearchingTextView.setText("开启蓝牙中.");
        } else {
            mDefaultBluetoothAdapter.startDiscovery();
        }

        // set list invisible.
        mSearchingProgressBar.setVisibility(View.VISIBLE);
        mSearchingTextView.setVisibility(View.VISIBLE);

        // set recyclerView's layoutManager and adapter.
        mRecyclerViewAdapter = new DeviceRecyclerAdapter(this);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
         //= new LinearLayoutManager(container.getContext(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onDeviceSelect(BluetoothDevice device) {
        mBlueService.connct(device);
    }
}
