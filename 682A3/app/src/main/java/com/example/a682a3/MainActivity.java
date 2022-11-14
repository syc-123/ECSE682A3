package com.example.a682a3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;

import android.Manifest;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

public class MainActivity extends AppCompatActivity {

    BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
    BluetoothAdapter mBtAdapter = mBluetoothManager.getAdapter();
    BluetoothGatt mBluetoothGatt;

    private BLE.MyBinder mybinder;
    private BLE service;
    private Intent intent;


    private BluetoothLeScanner bluetoothLeScanner = mBtAdapter.getBluetoothLeScanner();
    private boolean scanning;
    private Handler handler = new Handler();

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = mybinder.getService();
            if (service != null) {
                if (!service.initialize()) {
                    finish();
                }
                mybinder = (BLE.MyBinder) binder;
                service.connect(deviceAddress);

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
        }
    };





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.BLUETOOTH_CONNECT },
                1);

        intent = new Intent(this, BLE.class);
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    private BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BLE.ACTION_GATT_CONNECTED.equals(action)) {
                connected = true;
                updateConnectionState(R.string.connected);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                connected = false;
                updateConnectionState(R.string.disconnected);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
        if (service != null) {
            final boolean result = service.connect(deviceAddress);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(gattUpdateReceiver);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLE.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BLE.ACTION_GATT_DISCONNECTED);
        return intentFilter;
    }




}