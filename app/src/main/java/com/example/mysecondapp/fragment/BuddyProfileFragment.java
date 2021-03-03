package com.example.mysecondapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.view.View.OnClickListener;
import com.example.mysecondapp.R;
import com.example.mysecondapp.entity.UserEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
/**
 * Created by Zhirong on 20/3/2018.
 */

public class BuddyProfileFragment extends Fragment {
    //Display buddy's profile

    private BuddyProfileFragment.OnFragmentInteractionListener mListener;
    private View view;
    private ArrayList<ArrayList<String>> userDetails = new ArrayList<>();
    private SharedPreferences sharedPref;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_buddy_profile, container, false);
        Button searchBuddy = (Button) view.findViewById(R.id.searchBuddy);

        searchBuddy.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Fragment buddyProfileSearchFragment = new BuddyProfileSearchFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frameLayout, buddyProfileSearchFragment).addToBackStack(null).commit();
                /*Fragment buddyProfileSearchFragment = new BuddyProfileSearchFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, buddyProfileSearchFragment);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
            }
        });

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

        sharedPref = this.getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.username);
        String email = sharedPref.getString(getString(R.string.username), defaultValue);
        GetBuddyTask getBuddyTask = new GetBuddyTask(email);
        getBuddyTask.execute((Void) null);

    }

    @Override
    public void onResume(){
        super.onResume();

        sharedPref = this.getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.username);
        String email = sharedPref.getString(getString(R.string.username), defaultValue);
        GetBuddyTask getBuddyTask = new GetBuddyTask(email);
        getBuddyTask.execute((Void) null);

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class GetBuddyTask extends AsyncTask<Void, Void, Boolean> {

        private final String email;

        GetBuddyTask(String email) {
            this.email = email;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                // Simulate network access.
                // Create URL
                URL webSvcEndpoint = new URL(getString(R.string.ip_address)+"WebServices-war/Resources/Account/getBuddy/" + email);

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
                    userDetails = new ArrayList<ArrayList<String>>();
                    jsonReader.beginObject(); // Start processing the JSON object
                    String key = jsonReader.nextName();
                    jsonReader.beginArray();
                    while (jsonReader.hasNext()) { // Loop through all keys
                        String[] split = jsonReader.nextString().split(" ");
                        ArrayList<String> user = new ArrayList<>();
                        user.add(split[0]);
                        user.add(split[1]);
                        userDetails.add(user);
                    }
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
                ArrayList<String> result = new ArrayList<String>();
                for(int i = 0 ; i<userDetails.size(); i++){
                    result.add(userDetails.get(i).get(0)+"("+userDetails.get(i).get(1)+")");
                }
                //display the list of buddies as clickable itme
                final ListView list = (ListView)view.findViewById(R.id.buddyListView);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, result);
                list.setAdapter(adapter);

                list.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id) {
                        // TODO Auto-generated method stub
                        String item = (String) list.getItemAtPosition(position);
                        Fragment buddyProfileIndividualFragment = new BuddyProfileIndividualFragment();
                        Bundle args = new Bundle();
                        args.putString("email", userDetails.get(position).get(1));
                        buddyProfileIndividualFragment.setArguments(args);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.frameLayout, buddyProfileIndividualFragment).addToBackStack(null).commit();
                /*Fragment buddyProfileSearchFragment = new BuddyProfileSearchFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, buddyProfileSearchFragment);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
                    }

                });
            } else {
            }
        }
    }
}
