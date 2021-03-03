package com.example.mysecondapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.MenuItem;

import com.example.mysecondapp.R;
import com.example.mysecondapp.entity.FallRecordEntity;
import com.example.mysecondapp.entity.HeartRateEntity;
import com.example.mysecondapp.entity.LocationEntity;
import com.example.mysecondapp.entity.UserEntity;

import com.example.mysecondapp.fragment.BuddyProfileIndividualFragment;
import com.example.mysecondapp.fragment.HeartRateDatabaseFragment;
import com.example.mysecondapp.fragment.HeartRateFragment;
import com.example.mysecondapp.fragment.HomeFragment;
import com.example.mysecondapp.fragment.LocationFragment;
import com.example.mysecondapp.fragment.BuddyProfileFragment;
import com.example.mysecondapp.fragment.BuddyRequestFragment;
import com.example.mysecondapp.fragment.BuddyNotificationsFragment;
import com.example.mysecondapp.fragment.BuddyProfileSearchFragment;
import com.example.mysecondapp.fragment.ProfileFragment;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                    HomeFragment.OnLinkSelectedListener,
                    ProfileFragment.OnFragmentInteractionListener,
                    LocationFragment.OnFragmentInteractionListener,
                    HeartRateFragment.OnFragmentInteractionListener,
                    BuddyRequestFragment.OnFragmentInteractionListener,
                    BuddyProfileFragment.OnFragmentInteractionListener,
                    BuddyNotificationsFragment.OnFragmentInteractionListener,
                    HeartRateDatabaseFragment.OnFragmentInteractionListener,
                    BuddyProfileSearchFragment.OnFragmentInteractionListener,
                    BuddyProfileIndividualFragment.OnFragmentInteractionListener,
                    DataClient.OnDataChangedListener {
    private FragmentManager fragmentManager;
    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;
    private BuddyProfileFragment buddyProfileFragment;
    private BuddyRequestFragment buddyRequestFragment;
    private BuddyNotificationsFragment buddyNotificationsFragment;
    private HeartRateDatabaseFragment heartRateDatabaseFragment;
    private HeartRateFragment heartRateFragment;
    private SharedPreferences sharedPref;
    private UserEntity userEntity;
    private String email;

    private static final String Sendheartkey = "com.example.mysecondapp.key.send.heart.rate";
    private static final String gravity_key = "com.example.mysecondapp.key.gravity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
        homeFragment = new HomeFragment();
        profileFragment = new ProfileFragment();
        heartRateFragment = new HeartRateFragment();
        buddyProfileFragment = new BuddyProfileFragment();
        buddyRequestFragment = new BuddyRequestFragment();
        buddyNotificationsFragment = new BuddyNotificationsFragment();
        heartRateDatabaseFragment = new HeartRateDatabaseFragment();
        heartRateFragment = new HeartRateFragment();
        sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.username);
        email = sharedPref.getString(getString(R.string.username), defaultValue);
        //email = sharedPref.getString(getString(R.string.is_log_in), defaultValue1);

        switchHome();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStackImmediate();
        } else {
            super.onBackPressed();
        }
    }



    /*@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            switchHome();
        } else if (id == R.id.nav_logout) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.is_log_in), "false");
            editor.putString(getString(R.string.username), "");
            editor.apply();
            exit();
        } else if (id == R.id.nav_profile) {
            switchProfile();
        } else if (id == R.id.nav_heartRate) {
            switchHeartRate();
        } else if (id == R.id.nav_buddyProfile) {
            switchBuddyProfile();
        }  else if (id == R.id.nav_buddyRequest) {
            switchBuddyRequest();
        }
        /*else if (id == R.id.nav_buddyNotifications)
        {
            switchBuddyNotifications();
        }*/
        else if (id == R.id.nav_heartRateDatabase) {
            switchHeartRateDatabase();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void switchHome() {
        fragmentManager.beginTransaction().replace(R.id.frameLayout, homeFragment).commit();
    }

    private void switchProfile() {
        fragmentManager.beginTransaction().replace(R.id.frameLayout, profileFragment).commit();
    }


    private void switchHeartRate() {
        fragmentManager.beginTransaction().replace(R.id.frameLayout, heartRateFragment).commit();
    }


    private void switchBuddyProfile() {
        fragmentManager.beginTransaction().replace(R.id.frameLayout, buddyProfileFragment).commit();
    }

    private void switchBuddyRequest() {
        fragmentManager.beginTransaction().replace(R.id.frameLayout, buddyRequestFragment).commit();
    }

    private void switchBuddyNotifications() {
        fragmentManager.beginTransaction().replace(R.id.frameLayout, buddyNotificationsFragment).commit();
    }

    private void switchHeartRateDatabase() {
        fragmentManager.beginTransaction().replace(R.id.frameLayout, heartRateDatabaseFragment).commit();
    }

    private void exit() {
        finish();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        Wearable.getDataClient(this).addListener(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        Wearable.getDataClient(this).removeListener(this);
    }


    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();

                //LOCATION
                if (item.getUri().getPath().compareTo("/location") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    String uuid = dataMap.getString(getString(R.string.email_uuid));
                    LocationEntity locationEntity = new LocationEntity(email, uuid);
                    LocationTask locationTask = new LocationTask(locationEntity);
                    locationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

                //HEART RATE
                if (item.getUri().getPath().compareTo("/heart") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    String dataReceived = dataMap.getString(Sendheartkey);
                    String heartRate = dataReceived.split(",")[0];
                    String timestamp = dataReceived.split(",")[1];

                    HeartRateEntity hre = new HeartRateEntity(email, heartRate,timestamp);
                    SendHeartRateTask sendHeartRateTask = new SendHeartRateTask(hre);
                    sendHeartRateTask.execute((Void) null);

                }

                //GRAVITY-FALL
                if (item.getUri().getPath().compareTo("/gravity") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    String dataReceived = dataMap.getString(gravity_key);
                    String timestamp = dataReceived.split(",")[0];
                    String degreeFall = dataReceived.split(",")[1];


                    FallRecordEntity fallRecordEntity = new FallRecordEntity(timestamp, degreeFall, sharedPref.getString(getString(R.string.username), ""));
                    GravityFallTask gravityFallTask = new GravityFallTask(fallRecordEntity);
                    gravityFallTask.execute((Void) null);
                    System.err.println("Data sent from watch: " + dataReceived);

                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
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
                // Simulate network access.
                // Create URL
                URL webSvcEndpoint = new URL(getString(R.string.ip_address) + "WebServices-war/Resources/Location/createLocation");

                // Create connection
                HttpURLConnection myConnection =
                        (HttpURLConnection) webSvcEndpoint.openConnection();

                myConnection.setRequestMethod("PUT");
                myConnection.setDoOutput(true);
                myConnection.setRequestProperty("Content-Type", "application/json");
                myConnection.setRequestProperty("Accept", "application/json");
                OutputStreamWriter osw = new OutputStreamWriter(myConnection.getOutputStream());
                Gson gson = new Gson();
                osw.write(gson.toJson(locationEntity));
                osw.flush();
                osw.close();

                int test = myConnection.getResponseCode();

                if (myConnection.getResponseCode() == 200) {
                    // Success
                    // Further processing here
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader =
                            new InputStreamReader(responseBody, "UTF-8");
                    JsonReader jsonReader = new JsonReader(responseBodyReader);
                    jsonReader.beginObject(); // Start processing the JSON object
                    while (jsonReader.hasNext()) { // Loop through all keys
                        String key = jsonReader.nextName(); // Fetch the next key
                        if (key.equals("newLocation")) { // Check if desired key
                            // Fetch the value as a String
                            jsonReader.close();
                            myConnection.disconnect();
                            return true;

                        }
                        jsonReader.skipValue();
                    }
                    jsonReader.endObject();
                    jsonReader.close();
                    responseBodyReader.close();
                    responseBody.close();
                    myConnection.disconnect();
                    return false;

                } else {

                    // Error handling code goes here
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }
    }

    public class GravityFallTask extends AsyncTask<Void, Void, Boolean> {

        private FallRecordEntity fallRecordEntity;

        GravityFallTask(FallRecordEntity fallRecordEntity) {
            this.fallRecordEntity = fallRecordEntity;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // Simulate network access.
                // Create URL
                URL webSvcEndpoint = new URL(getString(R.string.ip_address) + "WebServices-war/Resources/FallRecord/createFallRecord");

                // Create connection
                HttpURLConnection myConnection =
                        (HttpURLConnection) webSvcEndpoint.openConnection();

                myConnection.setRequestMethod("PUT");
                myConnection.setDoOutput(true);
                myConnection.setRequestProperty("Content-Type", "application/json");
                myConnection.setRequestProperty("Accept", "application/json");
                OutputStreamWriter osw = new OutputStreamWriter(myConnection.getOutputStream());
                Gson gson = new Gson();
                osw.write(gson.toJson(fallRecordEntity));
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
                    Log.d("", "Jsonreader : " + jsonReader.toString());
                    jsonReader.beginObject(); // Start processing the JSON object
                    while (jsonReader.hasNext()) { // Loop through all keys
                        String key = jsonReader.nextName(); // Fetch the next key
                        Log.d("testvalue", "Key : " + key);
                        if (key.equals("true")) { // Check if desired key
                            // Fetch the value as a String
                            Log.d("testvalue", "Location : " + key);
                            jsonReader.close();
                            myConnection.disconnect();
                            return true;

                        }
                        jsonReader.skipValue();
                    }
                    jsonReader.endObject();
                    jsonReader.close();
                    responseBodyReader.close();
                    responseBody.close();
                    myConnection.disconnect();
                    return false;

                } else {

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public class SendHeartRateTask extends AsyncTask<Void, Void, Boolean> {

        private HeartRateEntity hre;

        SendHeartRateTask(HeartRateEntity hre) {
            this.hre = hre;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // Simulate network access.
                // Create URL
                URL webSvcEndpoint = new URL(getString(R.string.ip_address)+"WebServices-war/Resources/HeartRate/send");

                // Create connection
                HttpURLConnection myConnection =
                        (HttpURLConnection) webSvcEndpoint.openConnection();

                myConnection.setRequestMethod("PUT");
                myConnection.setDoOutput(true);
                myConnection.setRequestProperty("Content-Type", "application/json");
                myConnection.setRequestProperty("Accept", "application/json");
                OutputStreamWriter osw = new OutputStreamWriter(myConnection.getOutputStream());
                Gson gson = new Gson();
                osw.write(gson.toJson(hre));
                osw.flush();
                osw.close();

                if (myConnection.getResponseCode() == 200) {
                    // Success
                    // Further processing here
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader =
                            new InputStreamReader(responseBody, "UTF-8");
                    JsonReader jsonReader = new JsonReader(responseBodyReader);

                    jsonReader.beginObject(); // Start processing the JSON object
                    while (jsonReader.hasNext()) { // Loop through all keys
                        String key = jsonReader.nextName(); // Fetch the next key
                        if (key.equals("response")) { // Check if desired key
                            // Fetch the value as a String
                            boolean value = jsonReader.nextBoolean();

                            if (value) {
                                jsonReader.close();
                                myConnection.disconnect();
                                return true;
                            } else {
                                jsonReader.close();
                                myConnection.disconnect();
                                return false;
                            }
                        } else {
                            jsonReader.skipValue(); // Skip values of other keys
                        }
                    }
                } else {
                    // Error handling code goes here
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public void onLinkSelected(String link) {

        String url = link.trim();
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}
