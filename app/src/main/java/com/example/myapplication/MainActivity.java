package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    // Variables for pedometer.
    SensorManager sensorManager;
    Sensor stepCountSensor;
    TextView stepCountView;

    Button resetButton;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int myInt;

    EditText stepGoalView;
    Button btn01;

    TextView DistanceView;

    int currentSteps = 0; // Initial step count.
    double currentDistance = 0.0;
    String goal;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        myInt = pref.getInt("myInt", 0);
        stepGoalView = findViewById(R.id.stepGoalView);
        btn01 = findViewById(R.id.btn01);

        stepGoalView.setText(String.valueOf(myInt));

        btn01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myInt = Integer.parseInt(stepGoalView.getText().toString());
                editor.putInt("MyInt", myInt);
                editor.commit();
                goal = stepGoalView.getText().toString();
            }
        });

        // Pedometer.
        stepCountView = findViewById(R.id.stepCountView);
        resetButton = findViewById(R.id.resetButton);
        DistanceView = findViewById(R.id.DistanceView);


        // checking permission
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[] {Manifest.permission.ACTIVITY_RECOGNITION},0);
        }


        // TYPE_STEP_DETECTOR : it resets to 0 once the app closes
        // TYPE_STEP_COUNTER : keeps track even if the app closes, it does not reset.
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


        // TYPE_AMBIENT_TEMPERATURE: constantly updates temperature.
        // TYPE_RELATIVE_HUMIDITY: constantly updates relative humidity levels.
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) == null){
            Toast.makeText(this, "No Step Sensor", Toast.LENGTH_SHORT).show();
        }else{
            stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        }

        resetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Pedometer.
                currentSteps = 0;
                currentDistance = 0;
                stepGoalView.setText("0");
                stepCountView.setText(String.valueOf(currentSteps));
                DistanceView.setText(String.valueOf(currentDistance));

            }
        });
    }

    public void onStart(){
        super.onStart();
        if(stepCountSensor != null){
            sensorManager.registerListener(this,stepCountSensor,SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){
            if(event.values[0] == 1.0f) {
                currentSteps++;
                stepCountView.setText(currentSteps + " (" +getPercentage(goal,currentSteps)+"%"+")");
            }
            // Calculate the distance using the calculateDistance method
            currentDistance = calculateDistance(currentSteps);

            // Update the TextView text with the calculated distance
            DistanceView.setText(String.format("%.2f", currentDistance) + " mi");
        }
    }

    public static double calculateDistance(int stepCount) {
        return (stepCount * 2.5) / 5280;
    }
    public static int getPercentage(String goal, int current){
        int stepgoal = Integer.parseInt(goal);
        return (int) ((current / (float) stepgoal) * 100);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    @Override
    protected void onResume(){
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }
}