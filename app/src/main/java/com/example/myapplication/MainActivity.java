package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    // Variables for pedometer.
    SensorManager sensorManager;
    Sensor stepCountSensor;
    TextView stepCountView;
    Button resetButton;

    // Variables for humidity sensor.
    Sensor humiditySensor;
    TextView humidityView;

    // Variables for temperature sensor.
    Sensor temperatureSensor;
    TextView temperatureView;

    int currentSteps = 0; // Initial step count.
    int humidity = 0; // Initial humidity measurement.
    int temperature = 0; // Initial temperature measurement.

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Pedometer.
        stepCountView = findViewById(R.id.stepCountView);
        resetButton = findViewById(R.id.resetButton);

        // Humidity.
        humidityView = findViewById(R.id.humidityView);

        // Temperature
        temperatureView = findViewById(R.id.temperatureView);

        // checking permission
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[] {Manifest.permission.ACTIVITY_RECOGNITION},0);
        }

        // TYPE_STEP_DETECTOR : it resets to 0 once the app closes
        // TYPE_STEP_COUNTER : keeps track even if the app closes, it does not reset.
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        // TYPE_AMBIENT_TEMPERATURE: constantly updates temperature.
        // TYPE_RELATIVE_HUMIDITY: constantly updates relative humidity levels.
        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        if (stepCountSensor == null){
            Toast.makeText(this, "No Step Sensor", Toast.LENGTH_SHORT).show();
        }
        if (humiditySensor == null){
            Toast.makeText(this, "No Humidity Sensor", Toast.LENGTH_SHORT).show();
        }
        if (temperatureSensor == null){
            Toast.makeText(this, "No Temperature Sensor", Toast.LENGTH_SHORT).show();
        }

        resetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Pedometer.
                currentSteps = 0;
                stepCountView.setText(String.valueOf(currentSteps));

                // Humidity.
                humidity = 0;
                humidityView.setText(String.valueOf(humidity));

                // Temperature
                temperature = 0;
                temperatureView.setText(String.valueOf(temperature));

            }
        });
    }

    public void onStart(){
        super.onStart();
        if(stepCountSensor != null){
            sensorManager.registerListener(this,stepCountSensor,SensorManager.SENSOR_DELAY_FASTEST);
        }
        if (humiditySensor != null){
            sensorManager.registerListener(this,humiditySensor,SensorManager.SENSOR_DELAY_FASTEST);
        }
        if (temperatureSensor != null){
            sensorManager.registerListener(this,temperatureSensor,SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){
            if(event.values[0] == 1.0f) {
                currentSteps++;
                stepCountView.setText(String.valueOf(currentSteps));
            }

            if(event.sensor.getType()==Sensor.TYPE_RELATIVE_HUMIDITY){
                humidityView.setText("Relative Humidity: " + event.values[0] + "%");
            }

            if(event.sensor.getType()==Sensor.TYPE_AMBIENT_TEMPERATURE){
                temperatureView.setText("Temperature: " + event.values[0] + " degrees");
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }
}