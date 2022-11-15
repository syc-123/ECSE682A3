package com.example.a682a3;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class BLE extends Service {

    Intent intent;
    boolean serviceStopped;
    private final Handler handler = new Handler();
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;

    public static final String ACTION_BROADCAST = "com.example.a682a3.BROADCAST";
    public static final String TAG = "BluetoothLeService";

    public final static String ACTION_GATT_CONNECTED =
            "com.example.a682a3.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.a682a3.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.a682a3.ACTION_DATA_AVAILABLE";


    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTED = 2;

    private int connectionState;
    private String deviceAddress = "84:2e:14:31:b3:7b";


    public void initialize() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void connect(final String address) {
        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

        }
        bluetoothGatt = device.connectGatt(this, true, bluetoothGattCallback);
        // connect to the GATT server on the device
    }

    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                connectionState = STATE_CONNECTED;
                broadcast(ACTION_GATT_CONNECTED);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                connectionState = STATE_DISCONNECTED;
                broadcast(ACTION_GATT_DISCONNECTED);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcast(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcast(ACTION_DATA_AVAILABLE, characteristic);
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
    }

    public int onStartCommand(Intent intent, int flag, int startId){

        serviceStopped = false;

        handler.removeCallbacks(updateBroadcastData);
        handler.post(updateBroadcastData);


        return START_STICKY;
    }

    private Runnable updateBroadcastData = new Runnable() {
        public void run() {
            if (!serviceStopped) { // Only allow the repeating timer while service is running (once service is stopped the flag state will change and the code inside the conditional statement here will not execute).
                // Call the method that broadcasts the data to the Activity..
//                broadcast();
                // Call "handler.postDelayed" again, after a specified delay.
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mybinder;
//        return null;
    }

    public boolean onUnbind(Intent intent) {
        return false;
    }


    public void onDestroy(){
        super.onDestroy();
    }


    public void broadcast(){
        sendBroadcast(intent);
    }

}