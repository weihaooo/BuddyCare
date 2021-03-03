package com.example.mysecondapp;

import android.Manifest;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.JsonReader;
import android.util.Log;
import android.app.Activity;

import com.example.mysecondapp.entity.LocationEntity;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;


/**
 * Created by User on 4/17/2018.
 */

public class BuddyJobService extends android.app.job.JobService implements BeaconConsumer, RangeNotifier {

    private SensorManager sensorManager;
    private Sensor aSensor;
    //private Sensor sigSensor;
    //private TriggerEventListener mTriggerEventListener;
    private float[] gravity = new float[3];
    private float accOne = 0;
    private float accTwo = 0;
    private BeaconManager beaconManager;
    private SharedPreferences sharedPref;
    private DataClient dataClient;

    private static final String emailUuid_key = "com.example.mysecondapp.key.location";
    private static final String gravity_key = "com.example.mysecondapp.key.gravity";


    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //sigSensor = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
        //mTriggerEventListener = new TriggerListener();
        //sensorManager.requestTriggerSensor(mTriggerEventListener, sigSensor);

        for (Sensor s:sensorManager.getSensorList(Sensor.TYPE_ALL)) {
            System.err.println("Print all Sensors");
            System.err.println(s.getName());
        }

        dataClient = Wearable.getDataClient(this);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        beaconManager.bind(this);

        System.err.println("BuddyJobService : onCreate()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        System.err.println("BuddyJobService : onDestroy()");
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        System.out.println("BuddyJobService : onStartJob()");

        if(aSensor !=null){
            getGravityInfo();
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        System.out.println("BuddyJobService : onStopJob()");
        return false;
    }

    protected void getGravityInfo(){
        sensorManager.registerListener(new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                System.err.println("onSensorChanged");
                if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
                    return;

                //gravity[0] = event.values[0];
                //gravity[1] = event.values[1];
                gravity[2] = event.values[2];

                //System.err.println("X : "+gravity[0]);
                //System.err.println("Y : "+gravity[1]);
                System.err.println("Z : "+gravity[2]);

                processFloats();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                System.err.println("onAccuracyChanged");
            }


        }, aSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void processFloats(){
        if(accOne==0){
            accOne = gravity[2];
        }else{
            accTwo = gravity[2];
            float diff = getDiffofX(accOne, accTwo);

            /*
            PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/location");
            putDataMapReq.getDataMap().putString(emailUuid_key, userName+","+uuid);
            PutDataRequest putDataRequest = putDataMapReq.asPutDataRequest();
            putDataRequest.setUrgent();
            Task<DataItem> putDataTask = dataClient.putDataItem(putDataRequest);
             */

            System.err.println("**** SEND DATA TO MOBILE ****");


            if (diff>29.4){
                PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/gravity");
                String str = new Timestamp(System.currentTimeMillis()).toString()+",high"; //
                putDataMapReq.getDataMap().putString(gravity_key, str);
                System.err.println(str);
                System.err.println("Send High");
                PutDataRequest putDataRequest = putDataMapReq.asPutDataRequest();
                putDataRequest.setUrgent();
                Task<DataItem> putDataTask = dataClient.putDataItem(putDataRequest);
            }
            else if(diff>19.6){
                PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/gravity");
                String str = new Timestamp(System.currentTimeMillis()).toString()+",mid";
                putDataMapReq.getDataMap().putString(gravity_key, str);
                System.err.println(str);
                System.err.println("Send Mid");
                PutDataRequest putDataRequest = putDataMapReq.asPutDataRequest();
                putDataRequest.setUrgent();
                Task<DataItem> putDataTask = dataClient.putDataItem(putDataRequest);
            }
            else if(diff>9.8){
                PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/gravity");
                String str = new Timestamp(System.currentTimeMillis()).toString()+",low";
                putDataMapReq.getDataMap().putString(gravity_key, str);
                System.err.println(str);
                System.err.println("Send Low");
                PutDataRequest putDataRequest = putDataMapReq.asPutDataRequest();
                putDataRequest.setUrgent();
                Task<DataItem> putDataTask = dataClient.putDataItem(putDataRequest);
            }



            System.err.println("Send Finish");
            accOne=0;
            accTwo=0;
        }
    }

    private float getDiffofX(float x1, float x2){
        if(x1>x2)
            return x1-x2;
        else
            return x2-x1;
    }

    @Override
    public void onBeaconServiceConnect() {
        System.err.println("********** BeaconConsumer.onBeaconServiceConnect()");
        final ArrayList<Identifier> identifiers = new ArrayList<>();
        identifiers.add(null);

        Region region = new Region("AllBeaconsRegion", identifiers);

        try {
            beaconManager.startRangingBeaconsInRegion(region);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        beaconManager.addRangeNotifier(this);
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        System.err.println("beacon in range!");

        //Maximum distance that beacon can detect
        double nearestDistance = 7.0;
        //Identifier
        String uuid = "";


        for (Beacon beacon : beacons) {
            if (beacon.getDistance() < nearestDistance) {
                String identifier = "";

                if (beacon.getBeaconTypeCode() == 0) {
                    identifier = beacon.getId1() + ":" + beacon.getId2();
                } else if (beacon.getBeaconTypeCode() == 16) {
                    identifier = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());
                }
                nearestDistance = beacon.getDistance();
                uuid= identifier;
            }
        }

        // Find today date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String locationDateTime = dateFormat.format(new Date());

        if (uuid != ""){
            //Send the details to the phone
            Intent intent = new Intent();
            intent.setAction("BEACON_VALUE");
            intent.putExtra("BEACON_INFO1", "Timestamp: " + locationDateTime + " UUID: " + uuid);
            this.sendBroadcast(intent);

            //Get email of the user
            sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            //Save to Database

            PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/location");
            putDataMapReq.getDataMap().putString(emailUuid_key, uuid);
            PutDataRequest putDataRequest = putDataMapReq.asPutDataRequest();
            putDataRequest.setUrgent();
            Task<DataItem> putDataTask = dataClient.putDataItem(putDataRequest);


            /*LocationTask locationTask = new LocationTask(locationEntity);
           // locationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,(Void) null);
            locationTask.execute((Void) null);*/
        }

    }

    public class LocationTask extends AsyncTask<Void, Void, Boolean> {

        private LocationEntity locationEntity;

        LocationTask(LocationEntity locationEntity) {
            this.locationEntity = locationEntity;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                System.err.println("5");
                // Simulate network access.
                // Create URL
                URL webSvcEndpoint = new URL(getString(R.string.ip_address)+"WebServices-war/Resources/Location/createLocation");

                // Create connection
                HttpURLConnection myConnection =
                        (HttpURLConnection) webSvcEndpoint.openConnection();
                System.err.println("6");
                /*Log.d("","Username : "+userName);
                Log.d("","uuid : "+uuid);*/
                myConnection.setRequestMethod("PUT");
                myConnection.setDoOutput(true);
                myConnection.setRequestProperty("Content-Type", "application/json");
                myConnection.setRequestProperty("Accept", "application/json");
                OutputStreamWriter osw = new OutputStreamWriter(myConnection.getOutputStream());
                Log.d("testvalue", "Before osw.write()");
                Log.d("", "Email: "+locationEntity.getEmail());
                Log.d("", "Uuid: "+locationEntity.getUuid());
                Log.d("testvalue", "After osw.write()");
                Gson gson = new Gson();
                osw.write(gson.toJson(locationEntity));
                osw.flush();
                osw.close();
                System.err.println("7");
                int test = myConnection.getResponseCode();

                if (myConnection.getResponseCode() == 200) {
                    Log.d("testvalue", "Connection Pass!");

                    // Success
                    // Further processing here
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader =
                            new InputStreamReader(responseBody, "UTF-8");
                    JsonReader jsonReader = new JsonReader(responseBodyReader);
                    Log.d("", "Jsonreader : "+jsonReader.toString());
                    jsonReader.beginObject(); // Start processing the JSON object
                    while (jsonReader.hasNext()) { // Loop through all keys
                        String key = jsonReader.nextName(); // Fetch the next key
                        Log.d("testvalue", "Key : "+key);
                        if (key.equals("newLocation")) { // Check if desired key
                            // Fetch the value as a String
                            Log.d("testvalue", "Location : "+key);
                            jsonReader.close();
                            myConnection.disconnect();
                            return true;

                        }
                        jsonReader.skipValue();
                    }
                    jsonReader.endObject();
                    jsonReader.close();
                    myConnection.disconnect();
                    return false;

                } else {
                    Log.i("testvalue", "Fail error "+String.valueOf(test));
                    // Error handling code goes here
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("testvalue", e.getMessage());
            }

            return false;
        }
    }

}
