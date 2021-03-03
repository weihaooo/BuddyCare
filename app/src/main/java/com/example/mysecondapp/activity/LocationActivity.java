package com.example.mysecondapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mysecondapp.R;
import com.example.mysecondapp.entity.LocationEntity;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;


public class LocationActivity extends AppCompatActivity implements  DataClient.OnDataChangedListener {

    private String email;
    private String uuid;
    private static final String emailUuid_key = "com.example.mysecondapp.emailUuid";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Location Activity", "onCreate()");
    }


    public class LocationTask extends AsyncTask<Void, Void, Boolean> {

        private LocationEntity locationEntity;

        LocationTask(LocationEntity locationEntity) {
            this.locationEntity = locationEntity;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d("Location Activity", "doInBackground");
            try {
                // Simulate network access.
                // Create URL
                URL webSvcEndpoint = new URL(getString(R.string.ip_address)+"WebServices-war/Resources/Location/createLocation");

                // Create connection
                HttpURLConnection myConnection =
                        (HttpURLConnection) webSvcEndpoint.openConnection();

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


    @Override
    protected void onResume() {
        super.onResume();
        Wearable.getDataClient(this).addListener(this);
        Log.d("test on resume","test on resume");
    }



    @Override
    protected void onPause() {
        super.onPause();
        Wearable.getDataClient(this).removeListener(this);
        Log.d("test on pause","test on pause");
    }



    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer)
    {
        Log.d("datachange", "Data changed!");
        for (DataEvent event : dataEventBuffer)
        {
            if (event.getType() == DataEvent.TYPE_CHANGED)
            {
                Log.d("typechange", "Type Changed!");
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/emailUuid") == 0)
                {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    String email_uuid = dataMap.getString(emailUuid_key);
                    Log.d("email_uuid", email_uuid);
                    email = email_uuid.split(",")[0];
                    uuid = email_uuid.split(",")[1];
                    LocationEntity locationEntity = new LocationEntity(email, uuid);
                    Log.d("", locationEntity.getEmail());
                    Log.d("", locationEntity.getUuid());
                    LocationTask locationTask = new LocationTask(locationEntity);
                    locationTask.execute((Void) null);
                }
            }
            else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

}
