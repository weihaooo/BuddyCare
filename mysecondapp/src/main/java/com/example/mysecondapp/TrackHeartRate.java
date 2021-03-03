package com.example.mysecondapp;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.wearable.activity.WearableActivity;
import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.PersistableBundle;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.mysecondapp.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by FABIAN on 05-Apr-18.
 */

public class TrackHeartRate extends WearableActivity {

    private static final String Sendheartkey = "com.example.mysecondapp.key.send.heart.rate";
    private SensorManager sensorManager;
    private Sensor heartRateSensor;
    private TextView txtview_show_heart_rate;
    private DataClient dataClient;

    //private TextView textViewHeartRateOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_heart_rate);

        txtview_show_heart_rate = (TextView) findViewById(R.id.txtview_show_heart_rate);
        dataClient = Wearable.getDataClient(this);

        // Enables Always-on
        setAmbientEnabled();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BODY_SENSORS}, 800);
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        if(heartRateSensor != null) {
            updateTextViewDataOutput("Heart Rate sensor okay\r\n");

            sensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    String val = "Heart Rate: ";
                    Date date = new Date();
                    String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date);
                    val += sensorEvent.values[0];
                    System.err.println("Sending HeartRate Data: " + sensorEvent.values[0] + " " + formattedDate);
                    PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/heart");
                    putDataMapReq.getDataMap().putString(Sendheartkey, Double.toString(sensorEvent.values[0]) + "," + formattedDate);
                    PutDataRequest putDataRequest = putDataMapReq.asPutDataRequest();
                    putDataRequest.setUrgent();
                    Task<DataItem> putDataTask = dataClient.putDataItem(putDataRequest);

                    updateTextViewDataOutput(val + "\r\n");
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    String acc = "";

                    switch (accuracy) {
                        case SensorManager.SENSOR_STATUS_UNRELIABLE:
                            acc = "Unreliable";
                            break;
                        case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                            acc = "Low";
                            break;
                        case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                            acc = "Medium";
                            break;
                        case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                            acc = "High";
                            break;
                        default:
                            acc = "Unknown";
                            break;
                    }

                    updateTextViewDataOutput(acc + "\r\n");
                }
            }, heartRateSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
        else {
            updateTextViewDataOutput("Heart Rate sensor is NULL\r\n");
        }
    }



    private void updateTextViewDataOutput(String val)
    {
        txtview_show_heart_rate.append(val + "\r\n");
    }
}












