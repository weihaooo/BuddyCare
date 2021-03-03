package com.example.mysecondapp;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.net.Uri;

import com.example.mysecondapp.R;

public class MainActivity extends WearableActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.text);

        // Enables Always-on
        setAmbientEnabled();


        Button btn_app_detection = findViewById(R.id.btn_app_detection);
        btn_app_detection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent appDetection = new Intent(view.getContext(), DetectApplication.class);
                startActivity(appDetection);
            }
        });

        Button btn_track_heart_rate = findViewById(R.id.btn_track_heart_rate);
        btn_track_heart_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent trackHeartRate = new Intent(view.getContext(), TrackHeartRate.class);
                startActivity(trackHeartRate);
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 800);
        }
        gravityScheduler();
    }

    private void gravityScheduler(){
        ComponentName componentName = new ComponentName(this, BuddyJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(12, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        JobScheduler jobScheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = jobScheduler.schedule(jobInfo);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            System.err.println("Job scheduled!");
        } else {
            System.err.println("Job not scheduled");
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
