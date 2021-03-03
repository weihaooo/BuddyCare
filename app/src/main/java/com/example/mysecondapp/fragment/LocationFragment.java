package com.example.mysecondapp.fragment;

import android.Manifest;
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

/**
 * Created by Zhirong on 19/3/2018.
 */

public class LocationFragment extends Fragment implements  DataClient.OnDataChangedListener {

    private TextView myLocation;
    private BeaconBroadcastReceiver beaconBroadcastReceiver;
    private BeaconManager beaconManager;

    private LocationFragment.OnFragmentInteractionListener mListener;
    private View view;
    private SharedPreferences sharedPref;

    private String email;
    private String uuid;

    public LocationFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LocationFragment newInstance(String param1, String param2) {
        LocationFragment fragment = new LocationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("test" ,"created");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_location, container, false);

        myLocation = view.findViewById(R.id.textView_MyLocation);

        //updateTextViewMyLocation("Getting your Location...");

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 800);
        }
        /*
        beaconManager = BeaconManager.getInstanceForApplication(getActivity());
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));

        beaconManager.bind(this);

        beaconBroadcastReceiver = new LocationFragment.BeaconBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("BEACON_VALUE");
        getActivity().registerReceiver(beaconBroadcastReceiver, intentFilter);
        */
        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /*
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
    public Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        getActivity().unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return getActivity().bindService(intent, serviceConnection, i);
    }

    /*@Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
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

        if (uuid != "") {
            //Send the details to the phone
            Intent intent = new Intent();
            intent.setAction("BEACON_VALUE");
            intent.putExtra("BEACON_INFO1", "Timestamp: " + locationDateTime + " UUID: " + uuid);
            this.getActivity().sendBroadcast(intent);

            //Get email of the user
            sharedPref = this.getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String defaultValue = getResources().getString(R.string.username);//"weihaooo@hotmail.com";
            String emailPref = sharedPref.getString(getString(R.string.username), defaultValue);//"weihaooo@hotmail.com";
            Log.d("", emailPref);
            Log.d("", uuid);
            //Save to private variables
            this.email = emailPref;
            this.uuid = uuid;

        }
    }*/

    /**
     * Represents an asynchronous location task
     *
     */
    public class LocationTask extends AsyncTask<Void, Void, Boolean> {

        private LocationEntity locationEntity;

        LocationTask(LocationEntity locationEntity) {
            this.locationEntity = locationEntity;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

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


    protected class BeaconBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals("BEACON_VALUE")) {
                String beaconInfo = intent.getStringExtra("BEACON_INFO1");
                updateTextViewMyLocation(beaconInfo);
            }
        }
    }


    public void updateTextViewMyLocation(String val) {
        myLocation.setText("");
        myLocation.append(val);
    }


    @Override
    public void onResume() {
        super.onResume();
        Wearable.getDataClient(this.getActivity()).addListener(this);

        Log.i("test" ,"resume");
    }



    @Override
    public void onPause() {
        super.onPause();
        Wearable.getDataClient(this.getActivity()).removeListener(this);
    }



    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer)
    {
        Log.i("test" ,"anything");
        for (DataEvent event : dataEventBuffer)
        {
            if (event.getType() == DataEvent.TYPE_CHANGED)
            {

                Log.i("test" ,"anything8888");
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/location") == 0)
                {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    String email_uuid = String.valueOf(dataMap.getString(getString(R.string.email_uuid)));
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
