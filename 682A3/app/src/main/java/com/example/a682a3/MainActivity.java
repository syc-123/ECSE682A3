package com.example.a682a3;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private BLE.MyBinder mybinder;
    private BLE service;
    private Intent intent;

    int countedStep;
    int oldStep = 0;

    boolean StepAchieved;
    boolean CAchieved;
    boolean DAchieved;

    int sgoal;
    int cgoal;
    int dgoal;

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

        StepAchieved = false;
        CAchieved = false;
        DAchieved = false;
    }

    ActivityResultLauncher<Intent> ResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.i("test","intent received");
                        Intent data = result.getData();
                        String message1 = data.getStringExtra("StepGoal");
                        String message2 = data.getStringExtra("CaloriesGoal");
                        String message3 = data.getStringExtra("DistanceGoal");
                        TextView textView1 = findViewById(R.id.ShowStepGoal);
                        TextView textView2 = findViewById(R.id.ShowCGoal);
                        TextView textView3 = findViewById(R.id.ShowDGoal);
                        textView1.setText(message1);
                        textView2.setText(message2);
                        textView3.setText(message3);
                    }
                }
            }
    );

    public void Set(View v) {
        Intent intent = new Intent(this, MainActivity2.class);
        ResultLauncher.launch(intent);
    }

    private BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateViews(intent);
        }
    };

    private void updateViews(Intent intent){
        countedStep = intent.getIntExtra("stepcount",0);

        Log.d("main", String.format("step in main: %d", countedStep));

        TextView CurrentSteps = findViewById(R.id.CurrentSteps);
        TextView CurrentC = findViewById(R.id.CurrentC);
        TextView CurrentD = findViewById(R.id.CurrentD);
        CurrentSteps.setText(String.valueOf(countedStep-oldStep));
        CurrentC.setText(String.valueOf(Integer.valueOf(countedStep-oldStep)*0.04));
        CurrentD.setText(String.format("%.2f", Integer.valueOf(countedStep-oldStep)*0.7));

        TextView textView1 = findViewById(R.id.ShowStepGoal);
        TextView textView2 = findViewById(R.id.ShowCGoal);
        TextView textView3 = findViewById(R.id.ShowDGoal);
        try{
            sgoal = Integer.valueOf(textView1.getText().toString());
        }catch(NumberFormatException e){
            sgoal = 0;
        }
        try{
            cgoal = Integer.valueOf(textView2.getText().toString());
        }catch(NumberFormatException e){
            cgoal = 0;
        }
        try{
            dgoal = Integer.valueOf(textView3.getText().toString());
        }catch(NumberFormatException e){
            dgoal = 0;
        }

        if(sgoal!=0 && Integer.valueOf(countedStep-oldStep) >= sgoal && !StepAchieved){
            Toast.makeText(getApplicationContext(), "Step Goal Achieved! Well done.", Toast.LENGTH_LONG).show();
            StepAchieved = true;
        }

        if(cgoal!=0 && Integer.valueOf(countedStep-oldStep)*0.04 >= cgoal && !CAchieved){
            Toast.makeText(getApplicationContext(), "Calories Goal Achieved! Well done.", Toast.LENGTH_LONG).show();
            CAchieved = true;
        }

        if(dgoal!=0 && Integer.valueOf(countedStep-oldStep)*0.7 >= dgoal && !DAchieved){
            Toast.makeText(getApplicationContext(), "Distance Goal Achieved! Well done.", Toast.LENGTH_LONG).show();
            DAchieved = true;
        }

    }


    public void Reset(View v){
        oldStep = countedStep;
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