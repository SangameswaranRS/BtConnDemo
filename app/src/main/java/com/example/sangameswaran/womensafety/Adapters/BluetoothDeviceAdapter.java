package com.example.sangameswaran.womensafety.Adapters;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sangameswaran.womensafety.BtDeviceConnectionActivity;
import com.example.sangameswaran.womensafety.MainActivity;
import com.example.sangameswaran.womensafety.R;
import com.example.sangameswaran.womensafety.Services.ConnectionService;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Sangameswaran on 02-01-2018.
 */

public class BluetoothDeviceAdapter  extends RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolder>{

    BluetoothAdapter bluetoothAdapter;
    Set<BluetoothDevice> bluetoothDevices;
    List<BluetoothDevice> btDeviceList;
    Context context;
    BluetoothSocket mSocket;
    BluetoothDevice mDevice;
    InputStream mInput;
    byte[] mBuffer;
    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public BluetoothDeviceAdapter(BluetoothAdapter bluetoothAdapter, Set<BluetoothDevice> bluetoothDevices, Context context) {
        this.bluetoothAdapter = bluetoothAdapter;
        this.bluetoothDevices = bluetoothDevices;
        this.context = context;
        btDeviceList=new ArrayList<>(bluetoothDevices);
        mBuffer=new byte[12445];
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_devices_card_layout,parent,false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final BluetoothDevice device=btDeviceList.get(position);
        holder.btName.setText(device.getName());
        holder.btMac.setText(device.getAddress());
        holder.CardContainerLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDevice=btDeviceList.get(position);
                bluetoothAdapter.cancelDiscovery();
                Intent startBtDeviceConnectionActivity=new Intent(context, ConnectionService.class);
                startBtDeviceConnectionActivity.putExtra("mDevice",mDevice);
                context.startService(startBtDeviceConnectionActivity);
                Toast.makeText(context,"Service Started",Toast.LENGTH_LONG).show();
                Log.d("TAG","Discovery stopped");
                try{
                    /*mSocket=mDevice.createRfcommSocketToServiceRecord(MY_UUID);
                    Toast.makeText(context,"Socket created",Toast.LENGTH_LONG).show();*/
                    /*try {
                        mSocket.connect();
                    }catch (Exception er){
                        Toast.makeText(context,"Connect Exception : " +er,Toast.LENGTH_LONG).show();
                    }*/
                    /*try{
                        mInput=mSocket.getInputStream();
                        mInput.read(mBuffer);
                        String s=new String(mBuffer);
                        Toast.makeText(context,"Oruvazhia Kedachirichi : "+s,Toast.LENGTH_LONG).show();
                    }catch (Exception et){
                        Toast.makeText(context,"Problem in getting Input Stream",Toast.LENGTH_LONG).show();
                    }*/
                }catch (Exception e){
                    Toast.makeText(context,"Socket Exception : " +e,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return btDeviceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView btName,btMac;
        LinearLayout CardContainerLL;
        public ViewHolder(View itemView) {
            super(itemView);
            btName=(TextView) itemView.findViewById(R.id.btName);
            btMac=(TextView) itemView.findViewById(R.id.btMac);
            CardContainerLL=(LinearLayout)itemView.findViewById(R.id.CardContainerLL);
        }
    }
}
