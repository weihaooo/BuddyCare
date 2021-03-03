package com.example.mysecondapp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mysecondapp.R;
import com.example.mysecondapp.entity.UserEntity;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BuddyProfileIndividualFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BuddyProfileIndividualFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuddyProfileIndividualFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "email";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private UserEntity buddy;

    private TextView emailView;
    private TextView nricView;
    private TextView nameView;
    private TextView genderView;
    private TextView dobView;
    private TextView contactView;
    private TextView eContactView;
    private TextView addressView;
    private TextView bloodTypeView;
    private TextView titleView;

    private TextView myBuddyLocation;
    private TextView myBuddyHeartRate;
    private View view;
    private SharedPreferences sharedPref;
    private GetBuddyLocation mBuddyLocationTask = null;
    private GetBuddyHr mBuddyHrTask = null;
    private String locationNameString = "";
    private String nameofPatient = "";
    private double hr;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BuddyProfileIndividualFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BuddyProfileIndividualFragment newInstance(String param1,String param2) {
        BuddyProfileIndividualFragment fragment = new BuddyProfileIndividualFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_buddy_profile_individual, container, false);
        myBuddyLocation = view.findViewById(R.id.textView_MyBuddyLocation);
        myBuddyHeartRate = view.findViewById(R.id.myBuddyHeartRate);
        TimerTask task = new TimerTask(){
            @Override
            public void run(){

                //Get Buddy(ies) location from database and display here
                //(1) Get the username of the user
                sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                String defaultValue = getResources().getString(R.string.username);
                String username =sharedPref.getString(getString(R.string.username), defaultValue);

                mBuddyLocationTask = new GetBuddyLocation(username);
                mBuddyLocationTask.execute((Void) null);

                mBuddyHrTask = new GetBuddyHr(username);
                mBuddyHrTask.execute((Void) null);
            }

        };

        new Timer().scheduleAtFixedRate(task,0,1000);
        /*
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipelayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh,R.color.refresh1,R.color.refresh2);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);

                    }
                },3000);
            }
        });*/
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
    @Override
    public void onStart(){
        super.onStart();
        emailView = ((TextView)this.getActivity().findViewById(R.id.emailVal));
        nricView =((TextView)this.getActivity().findViewById(R.id.nricVal));
        nameView =((TextView)this.getActivity().findViewById(R.id.nameVal));
        genderView =((TextView)this.getActivity().findViewById(R.id.genderVal));
        dobView =((TextView)this.getActivity().findViewById(R.id.dobVal));
        contactView =((TextView)this.getActivity().findViewById(R.id.contactVal));
        eContactView =((TextView)this.getActivity().findViewById(R.id.eContactVal));
        addressView =((TextView)this.getActivity().findViewById(R.id.resAddressVal));
        bloodTypeView =((TextView)this.getActivity().findViewById(R.id.bloodTypeVal));
        titleView =((TextView)this.getActivity().findViewById(R.id.budProfileTitle));


    }
    @Override
    public void onResume(){
        super.onResume();

        GetBuddyProfileTask getBuddyProfileTask = new GetBuddyProfileTask(mParam1);
        getBuddyProfileTask.execute((Void) null);

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    protected class BeaconBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            if (action.equals("BEACON_VALUE"))
            {
                String beaconInfo = intent.getStringExtra("BEACON_INFO1");
                updateTextViewMyBuddyLocation(beaconInfo);
            }
        }
    }



    private void updateTextViewMyBuddyLocation(String val)
    {
        myBuddyLocation.setText(val);
    }
    private void updateTextViewMyBuddyHr(double val)
    {
        Log.i("test1", String.valueOf(val));
        myBuddyHeartRate.setText("Current Heart Rate: " + String.valueOf(val));
    }

    public class GetBuddyLocation extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;

        GetBuddyLocation(String email){
            mEmail = email;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // Simulate network access.
                // Create URL
                URL webSvcEndpoint = new URL(getString(R.string.ip_address)+"WebServices-war/Resources/Location/myPatientLocation/"+mEmail);

                // Create connection
                HttpURLConnection myConnection =
                        (HttpURLConnection) webSvcEndpoint.openConnection();


                //myConnection.setRequestMethod("POST");
                if (myConnection.getResponseCode() == 200) {
                    // Success
                    // Further processing here
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader =
                            new InputStreamReader(responseBody, "UTF-8");
                    JsonReader jsonReader = new JsonReader(responseBodyReader);
                    jsonReader.beginObject(); // Start processing the JSON object
                    while (jsonReader.hasNext()) { // Loop through all keys
                        String key = jsonReader.nextName();
                        if (key.equals("patientLocations")) {
                            jsonReader.beginArray();

                            while(jsonReader.hasNext()){
                                jsonReader.beginObject();
                                while (jsonReader.hasNext()) {
                                    String beaconUser = jsonReader.nextName();

                                    if (beaconUser.equals("beacon")) {
                                        jsonReader.beginObject();
                                        while(jsonReader.hasNext()){
                                            String locationName = jsonReader.nextName();
                                            if (locationName.equals("locationName")) {
                                                locationNameString = jsonReader.nextString();
                                            }else jsonReader.skipValue();
                                        }
                                        jsonReader.endObject();
                                    }else if(beaconUser.equals("userEntity")){
                                        jsonReader.beginObject();
                                        while(jsonReader.hasNext()){
                                            String name = jsonReader.nextName();
                                            if(name.equals("name")){
                                                nameofPatient = jsonReader.nextString();

                                            }else{
                                                jsonReader.skipValue();
                                            }
                                        }
                                        jsonReader.endObject();

                                    }else {
                                        jsonReader.skipValue();
                                    }
                                }
                                jsonReader.endObject();
                            }
                            jsonReader.endArray();

                        }
                    }
                    //If locationName and nameOfPatient are retrieved successfully, return TRUE
                    if(!(locationNameString.isEmpty()||locationNameString=="")&&!(nameofPatient.isEmpty()||nameofPatient==""))
                        return true;

                    jsonReader.close();
                    myConnection.disconnect();
                    return false;
                } else {
                    myConnection.disconnect();
                    return false;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }
        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                updateTextViewMyBuddyLocation(locationNameString);
            } else {
            }
        }
    }
    public class GetBuddyHr extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;

        GetBuddyHr(String email){
            mEmail = email;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // Simulate network access.
                // Create URL
                URL webSvcEndpoint = new URL(getString(R.string.ip_address)+"WebServices-war/Resources/HeartRate/myPatientHr/"+mParam1);

                // Create connection
                HttpURLConnection myConnection =
                        (HttpURLConnection) webSvcEndpoint.openConnection();

                if (myConnection.getResponseCode() == 200) {
                    // Success
                    // Further processing here
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader =
                            new InputStreamReader(responseBody, "UTF-8");
                    JsonReader jsonReader = new JsonReader(responseBodyReader);
                    jsonReader.beginObject(); // Start processing the JSON object
                    jsonReader.nextName();
                    while (jsonReader.hasNext()) { // Loop through all keys
                        hr = jsonReader.nextDouble();
                    }

                    jsonReader.close();
                    myConnection.disconnect();
                    return true;
                } else {
                    myConnection.disconnect();
                    return false;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }
        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                updateTextViewMyBuddyHr(hr);
            } else {
            }
        }
    }

    public class GetBuddyProfileTask extends AsyncTask<Void, Void, Boolean> {

            private final String email;

            GetBuddyProfileTask(String email) {
                this.email = email;
            }

            @Override
            protected Boolean doInBackground(Void... params) {

                try {
                    // Simulate network access.
                    // Create URL
                    URL webSvcEndpoint = new URL(getString(R.string.ip_address)+"WebServices-war/Resources/Account/buddyProfile/" + email);

                    // Create connection
                    HttpURLConnection myConnection =
                            (HttpURLConnection) webSvcEndpoint.openConnection();

                    if (myConnection.getResponseCode() == 200) {
                        // Success
                        // Further processing here
                        InputStream responseBody = myConnection.getInputStream();
                        InputStreamReader responseBodyReader =
                                new InputStreamReader(responseBody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);
                        Gson gson = new Gson();
                        buddy = gson.fromJson(responseBodyReader,UserEntity.class);

                        myConnection.disconnect();
                        return true;

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
            @Override
            protected void onPostExecute(final Boolean success) {

                if (success) {
                    emailView.setText(buddy.getEmail());
                    nricView.setText(buddy.getNric());
                    nameView.setText(buddy.getName());
                    genderView.setText(buddy.getGender());
                    dobView.setText(buddy.getDob());
                    contactView.setText(buddy.getContact());
                    eContactView.setText(buddy.geteContact());
                    addressView.setText(buddy.getAddress());
                    bloodTypeView.setText(buddy.getBloodType());
                } else {
                }
            }
        }
}
