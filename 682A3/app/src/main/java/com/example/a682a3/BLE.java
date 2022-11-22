package com.example.a682a3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.UUID;

public class BLE extends Service {

    Intent intent;
    boolean serviceStopped;
    private final Handler handler = new Handler();
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattService bluetoothGattService;
    private BluetoothGattCharacteristic Step_data;
    private BluetoothGattCharacteristic Reset;

    public static final String ACTION_BROADCAST = "com.example.a682a3.BROADCAST";

    private String deviceAddress = "84:2E:14:31:B3:7B";
    private  UUID step_service_UUID =
            UUID.fromString("8b85189a-69d4-11ed-a1eb-0242ac120002");
    private UUID step_char_UUID =
            UUID.fromString("d96279f2-69d6-11ed-a1eb-0242ac120002");
    private UUID step_reset_UUID =
            UUID.fromString("6cceaae6-6a89-11ed-a1eb-0242ac120002");

    private boolean connected = false;
    private int reset_flag = 0;


    public void initialize() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void connect(final String address) {
        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.d("debug", "Device not found.  Unable to connect.");
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

        }
        bluetoothGatt = device.connectGatt(this, true, bluetoothGattCallback);
        Log.d("debug", "Trying to create a new connection.");
    }

    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                bluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

            }
        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcast(characteristic);
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                bluetoothGattService = bluetoothGatt.getService(step_service_UUID);
                Log.d("debug", "discover service");
                Step_data = bluetoothGattService.getCharacteristic(step_char_UUID);
                Log.d("debug", "access characteristic");

                bluetoothGatt.readCharacteristic(Step_data);

                connected = true;

            } else {
                Log.d("debug", "not discover service");
            }
        }
    };

    class MyBinder extends Binder {
        public BLE getService(){
            return BLE.this;
        }
    }

    private MyBinder mybinder = new MyBinder();

    public BLE() {
    }

    public void onCreate(){
        super.onCreate();
        intent = new Intent(ACTION_BROADCAST);
        initialize();
        connect(deviceAddress);

        serviceStopped = false;
        Log.d("debug","on");

        handler.removeCallbacks(updateBroadcastData);
        handler.post(updateBroadcastData);

    }

    public int onStartCommand(Intent intent, int flag, int startId){
        return START_STICKY;
    }

    private Runnable updateBroadcastData = new Runnable() {
        public void run() {
            if (!serviceStopped) {
                if(connected) {
                    bluetoothGatt.readCharacteristic(Step_data);
                }
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mybinder;
    }

    public boolean onUnbind(Intent intent) {
        return false;
    }

    public void onDestroy(){
        super.onDestroy();
    }

    @SuppressLint("MissingPermission")
    public void broadcast(BluetoothGattCharacteristic characteristic){
        int step_num = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16,0);
        Log.d("debug", String.format("step: %d", step_num));
        intent.putExtra("stepcount",step_num);
        sendBroadcast(intent);
    }

}