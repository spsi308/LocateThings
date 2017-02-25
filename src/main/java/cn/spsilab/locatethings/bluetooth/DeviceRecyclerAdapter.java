package cn.spsilab.locatethings.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import cn.spsilab.locatethings.R;

/**
 * Created by changrq on 17-2-20.
 */

public class DeviceRecyclerAdapter extends RecyclerView.Adapter<DeviceRecyclerAdapter.DeviceViewHolder> {

    private ArrayList<BluetoothDevice> mDeviceArrayList;
    private DeviceSelectHandler mSelectHandler;

    public DeviceRecyclerAdapter(DeviceSelectHandler handler) {

        // set call back handler.
        if (handler instanceof DeviceSelectHandler) {
            mSelectHandler = (DeviceSelectHandler) handler;
        } else {
            throw new RuntimeException(handler.getClass().toString()
                    + "should implement DeviceSelectHandler.");
        }

        // instantiate arrayList.
        mDeviceArrayList = new ArrayList<>();
    }

    public interface DeviceSelectHandler {
        void onDeviceSelect(BluetoothDevice device);
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder {

        private TextView mDeviceInfoTextView;

        public DeviceViewHolder(View itemView) {
            super(itemView);

            // bind view component.
            mDeviceInfoTextView = (TextView) itemView.findViewById(R.id.text_bluetooth_device);
            Button mSelectButton = (Button) itemView.findViewById(R.id.btn_bluetooth_select_deviece);

            mSelectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectHandler.onDeviceSelect(mDeviceArrayList.get(getAdapterPosition()));
                }
            });
        }
        public void setDeviceInfo(String name, String addr) {
            mDeviceInfoTextView.setText(name + "\n" + addr);
        }
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_item_bluetooth_device, parent, false);

        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        BluetoothDevice device = mDeviceArrayList.get(position);
        holder.setDeviceInfo(device.getName(), device.getAddress());
    }

    @Override
    public int getItemCount() {
        return mDeviceArrayList == null ? 0: mDeviceArrayList.size();
    }

    public void addDevice(BluetoothDevice device) {
        mDeviceArrayList.add(0, device);

        notifyItemInserted(0);
    }
}
