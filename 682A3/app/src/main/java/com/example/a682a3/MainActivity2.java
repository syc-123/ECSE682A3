package com.example.a682a3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    public void setgoal(View v){
        Intent back = new Intent(this, MainActivity.class);
        EditText edit1 = findViewById(R.id.StepGoalTypeIn);
        EditText edit2 = findViewById(R.id.CaloriesGoalTypeIn);
        EditText edit3 = findViewById(R.id.DistanceGoalTypeIn);
        String sendback1 = edit1.getText().toString();
        String sendback2 = edit2.getText().toString();
        String sendback3 = edit3.getText().toString();
        back.putExtra("StepGoal",sendback1);
        back.putExtra("CaloriesGoal",sendback2);
        back.putExtra("DistanceGoal",sendback3);
        setResult(Activity.RESULT_OK,back);
        finish();
    }

    public void resetgoal(View v){
        EditText edit1 = findViewById(R.id.StepGoalTypeIn);
        EditText edit2 = findViewById(R.id.CaloriesGoalTypeIn);
        EditText edit3 = findViewById(R.id.DistanceGoalTypeIn);
        edit1.setText(null);
        edit2.setText(null);
        edit3.setText(null);
    }

    protected void onDestroy(){
        super.onDestroy();
    }


}