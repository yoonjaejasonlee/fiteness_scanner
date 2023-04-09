package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

<<<<<<< Updated upstream
    // Variables for temperature sensor.
    Sensor temperatureSensor;
    TextView temperatureView;
=======
    // Variables used for creating notifications.
    String textTitle;
    String textContent;
    String largeTextContent;
>>>>>>> Stashed changes

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
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
<<<<<<< Updated upstream
=======

    // NOTIFICATION HANDLING CODE: Not yet functional. If you need to test something without this code, feel free to comment it out quick.
    // /* --------------------------

    // Creates a channel for notifications.
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES .0) { // Ensures notif. channel is only created on API Ver. 26+. Using notification channels below this ver. causes an error.
            CharSequence name = getString(R.string.channel_name);
            String description = getSring(R.string.channel_description);
            int priority = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, priority);
            channel.setDescription(description);

            // Register the channel with the system.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Sets the notification's 'tap action'/intent, or what occurs when the notification is tapped.
    Intent intent = new Intent(this, AlertDetails.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

    // Sets the content and channel of a notification.
    NotificationManagerCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
        //.setSmallIcon(R.drawable.notification_icon) // Code for a potential icon for the notification.
        .setContentTitle(textTitle) // Title of the Notification.
        .setContentText(textContent) // Content of the Notification. Short version that spans one line.
        .setStyle(new NotificationCompat.BigTextStyle() // Notifications that span more than a single line.
            .bigText(largeTextContent)
        )

        .setPriority(NotificationCompat.PRIORITY_DEFAULT); // How 'intrusive' the notification is. Default value.
        .setContentIntent(pendingIntent); // Fires the notification's 'tap action'. See the respective code block above for further details.
        .setAutoCancel(true); // Automatically dismisses notification after a bit.

    // Shows the notification to the user.
    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
    notificationManager.notify(notificationId, builder.build());

    // End of notification code.
    // ------------------------------------- */

    public static double calculateDistance(int stepCount) {
        return (stepCount * 2.5) / 5280;
    }

>>>>>>> Stashed changes
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }
}