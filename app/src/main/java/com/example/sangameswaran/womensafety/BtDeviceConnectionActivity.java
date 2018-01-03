package com.example.sangameswaran.womensafety;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.media.audiofx.LoudnessEnhancer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Created by Sangameswaran on 02-01-2018.
 */

public class BtDeviceConnectionActivity extends AppCompatActivity {

    ProgressBar connectionProgress;
    TextView tvConnectionStatus;
    Button startServiceBtn;
    BluetoothSocket mSocket;
    BluetoothDevice mDevice;
    InputStream mInput;
    byte[] mBuffer;
    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bt_device_connection_activity);
        connectionProgress=(ProgressBar)findViewById(R.id.connectionProgress);
        mBuffer=new byte[2046];
        tvConnectionStatus=(TextView)findViewById(R.id.tvConnectionStatus);
        getValuesViaIntent();
        startServiceBtn=(Button) findViewById(R.id.startServiceBtn);
        try {
            mSocket=mDevice.createRfcommSocketToServiceRecord(MY_UUID);
            Log.d("Tag","Socket Created without Error");
            mSocket.connect();
            Log.d("Tag","Connected to server");
            connectionProgress.setVisibility(View.GONE);
            tvConnectionStatus.setText("Connected to "+mDevice.getName());
            tvConnectionStatus.setVisibility(View.VISIBLE);
            startServiceBtn.setVisibility(View.VISIBLE);
            /*while (true){
                mInput=mSocket.getInputStream();
                mInput.read(mBuffer);
                String s=new String(mBuffer);
                Toast.makeText(getApplicationContext(),"Recieved :"+s,Toast.LENGTH_LONG).show();
            }*/
            final Handler keepGettingIps=new Handler();
            keepGettingIps.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        mInput=mSocket.getInputStream();
                        mInput.read(mBuffer);
                        String s=new String(mBuffer);
                        Toast.makeText(getApplicationContext(),"Recieved :"+s,Toast.LENGTH_LONG).show();
                        keepGettingIps.postDelayed(this,2000);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                   // Toast.makeText(getApplicationContext(),"Looping in Handler", Toast.LENGTH_SHORT).show();
                }
            },2000);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    public void getValuesViaIntent(){
        if(getIntent().getExtras()!=null){
            mDevice=getIntent().getExtras().getParcelable("mDevice");
            if(mDevice==null){
                Toast.makeText(getApplicationContext(),"Something went wrong!!",Toast.LENGTH_LONG).show();
                finish();
            }
        }else{
            Toast.makeText(getApplicationContext(),"Something went wrong!!",Toast.LENGTH_LONG).show();
        }
    }
}
