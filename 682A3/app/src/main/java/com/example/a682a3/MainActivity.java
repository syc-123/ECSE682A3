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
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

//    BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//    BluetoothAdapter mBtAdapter = mBluetoothManager.getAdapter();
//    BluetoothGatt mBluetoothGatt;

    private BLE.MyBinder mybinder;
    private BLE service;
    private Intent intent;

    boolean isServiceStopped;
    boolean StepAchieved;
    boolean CAchieved;
    boolean DAchieved;

    int sgoal;
    int cgoal;
    int dgoal;

    String countedStep;

//    private BluetoothLeScanner bluetoothLeScanner = mBtAdapter.getBluetoothLeScanner();
//    private boolean scanning;
    private Handler handler = new Handler();

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mybinder = (BLE.MyBinder) binder;
            service = mybinder.getService();
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
        registerReceiver(gattUpdateReceiver, new IntentFilter(BLE.ACTION_BROADCAST));
    }

    public void Start(View v){

    }

    public void Stop(View v){
        unregisterReceiver(gattUpdateReceiver);
    }


    private BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateViews(intent);
        }
    };

    private void updateViews(Intent intent){
        countedStep = intent.getStringExtra("stepcount");

        Log.d("main", String.format("step in main: %s", countedStep));

        TextView CurrentSteps = findViewById(R.id.CurrentSteps);
        TextView CurrentC = findViewById(R.id.CurrentC);
        TextView CurrentD = findViewById(R.id.CurrentD);
        CurrentSteps.setText(String.valueOf(countedStep));
        CurrentC.setText(String.valueOf(Integer.valueOf(countedStep)*0.04));
        CurrentD.setText(String.format("%.2f", Integer.valueOf(countedStep)*0.7));


    }


    public void Reset(View v){
        TextView textView1 = findViewById(R.id.ShowStepGoal);
        TextView textView2 = findViewById(R.id.ShowCGoal);
        TextView textView3 = findViewById(R.id.ShowDGoal);
        TextView CurrentSteps = findViewById(R.id.CurrentSteps);
        TextView CurrentC = findViewById(R.id.CurrentC);
        TextView CurrentD = findViewById(R.id.CurrentD);
        textView1.setText("0");
        textView2.setText("0");
        textView3.setText("0");
        CurrentSteps.setText("0");
        CurrentC.setText("0");
        CurrentD.setText("0");
        StepAchieved = false;
        CAchieved = false;
        DAchieved = false;
    }

    protected void onDestroy(){
        super.onDestroy();
        unbindService(conn);
    }

}