package com.example.sangameswaran.womensafety;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.sangameswaran.womensafety.Adapters.BluetoothDeviceAdapter;
import com.example.sangameswaran.womensafety.Services.LocationService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements LocationListener {
    final int REQUEST_ENABLE_BT = 1;
    final int REQUEST_COARSE_LOCATION = 2;
    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> mPairedDevices;
    Set<BluetoothDevice> mDiscoveredDevices = new HashSet<>();
    RecyclerView pairedDevicesRv, availableDevices;
    RecyclerView.LayoutManager manager, manager1;
    RecyclerView.Adapter adapter, avAdapter;
    LocationService service;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCheck();
        pairedDevicesRv = (RecyclerView) findViewById(R.id.pairedDevices);
        availableDevices = (RecyclerView) findViewById(R.id.availableDevices);
        manager = new LinearLayoutManager(this);
        manager1 = new LinearLayoutManager(this);
        pairedDevicesRv.setLayoutManager(manager);
        availableDevices.setLayoutManager(manager1);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Device does not support b/t", Toast.LENGTH_SHORT).show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                mPairedDevices = getPairedDevices();
                adapter = new BluetoothDeviceAdapter(mBluetoothAdapter, mPairedDevices, this);
                pairedDevicesRv.setLayoutManager(manager);
                pairedDevicesRv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                if (mBluetoothAdapter.startDiscovery()) {
                    checkLocationPermission();
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mReceiver, filter);
                } else {
                    Toast.makeText(getApplicationContext(), "Errr", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            Toast.makeText(getApplicationContext(), "Bluetooth Enabled", Toast.LENGTH_LONG).show();
            mPairedDevices = getPairedDevices();
            adapter = new BluetoothDeviceAdapter(mBluetoothAdapter, mPairedDevices, this);
            pairedDevicesRv.setLayoutManager(manager);
            pairedDevicesRv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            if (mBluetoothAdapter.startDiscovery()) {
                checkLocationPermission();
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mReceiver, filter);
            } else {
                Toast.makeText(getApplicationContext(), "Errr", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "B/t Disabled.. Exiting App", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    Set<BluetoothDevice> getPairedDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d("TAG", "Device name + =" + deviceName + "\n Device H/W Addr +=" + deviceHardwareAddress);
            }
            return pairedDevices;
        } else {
            return null;
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                Toast.makeText(getApplicationContext(), "Discovered" + deviceName, Toast.LENGTH_LONG).show();
                mDiscoveredDevices.add(device);
                avAdapter = new BluetoothDeviceAdapter(mBluetoothAdapter, mDiscoveredDevices, MainActivity.this);
                availableDevices.setAdapter(avAdapter);
                avAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    protected void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
        }
    }

    public void locationCheck() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            Toast.makeText(getApplicationContext(),"Fuck yeah lat="+location.getLatitude(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(getApplicationContext(),"FUCK LAT:"+location.getLatitude(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {


    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
