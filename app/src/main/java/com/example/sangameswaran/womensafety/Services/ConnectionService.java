package com.example.sangameswaran.womensafety.Services;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.Executor;

/**
 * Created by Sangameswaran on 02-01-2018.
 */

public class ConnectionService extends Service implements LocationListener{
    BluetoothSocket mSocket;
    BluetoothDevice mDevice;
    InputStream mInput;
    byte[] mBuffer;
    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    int readBytes=0;
    private FusedLocationProviderClient mFusedLocationClient;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getExtras()!=null){
            mBuffer=new byte[100];
            mDevice=intent.getExtras().getParcelable("mDevice");
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            if(mDevice==null){
                Toast.makeText(this,"Something went wrong..Try again",Toast.LENGTH_LONG).show();
            }else {
                try {
                    mSocket=mDevice.createRfcommSocketToServiceRecord(MY_UUID);
                    Log.d("Tag","Socket Created without Error");
                    mSocket.connect();
                    Log.d("Tag","Connected to server");
                    Toast.makeText(this,"ALL FINE",Toast.LENGTH_LONG).show();
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
                                readBytes=mInput.read(mBuffer);
                                Log.d("SERVICE","Bytes read : "+readBytes);
                                String s=new String(mBuffer);
                                Toast.makeText(getApplicationContext(),"Recieved :"+s,Toast.LENGTH_LONG).show();
                                if (ActivityCompat.checkSelfPermission(ConnectionService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ConnectionService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                mFusedLocationClient.getLastLocation()
                                        .addOnSuccessListener(new Executor() {
                                            @Override
                                            public void execute(@NonNull Runnable runnable) {
                                                runnable.run();
                                            }
                                        }, new OnSuccessListener<Location>() {
                                            @Override
                                            public void onSuccess(Location location) {
                                                // Got last known location. In some rare situations this can be null.
                                                if (location != null) {
                                                    // Logic to handle location object
                                                    Toast.makeText(getApplicationContext(), "Fuck yeah lat=" + location.getLatitude(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                keepGettingIps.postDelayed(this,2000);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                stopSelf();
                            }
                        }
                    },2000);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    stopSelf();
                }
            }
        }else{
            Toast.makeText(this,"Something went wrong..Try again",Toast.LENGTH_LONG).show();
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
