package com.example.mysecondapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.mysecondapp.CustomAddAdapter;
import com.example.mysecondapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class BuddyProfileSearchFragment extends Fragment {

    private BuddyProfileSearchFragment.OnFragmentInteractionListener mListener;
    private View view;
    private CustomAddAdapter adapter;
    private ArrayList<String> buddies;
    private ListView lv;
    ArrayList<String> mAllData = new ArrayList<String>();
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
        view = inflater.inflate(R.layout.fragment_buddy_profile_search, container, false);
        sharedPref = this.getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.username);
        String email = sharedPref.getString(getString(R.string.username), defaultValue);
        doSearch(email);
        return view;
    }

    private void doSearch(final String email) {
        final EditText et = (EditText)view.findViewById(R.id.searchListBuddies);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SearchUserTask searchUserTask = new SearchUserTask(et.getText().toString(),email);
                searchUserTask.execute((Void) null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = et.getText().toString().toLowerCase(Locale.getDefault());
                //filter(text);
            }
        });
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

    public class SearchUserTask extends AsyncTask<Void, Void, Boolean> {

        private final String query;
        private final String email;

        SearchUserTask(String query,String email) {
            this.query = query;
            this.email = email;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
                Log.i("test", email);
            Log.i("test", query);
            try {
                // Simulate network access.
                // Create URL
                URL webSvcEndpoint = new URL(getString(R.string.ip_address)+"WebServices-war/Resources/Account/search/" + email + "/" + query);

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
                        String string = jsonReader.nextString();
                        if(!string.equalsIgnoreCase("")) {
                            String[] split = string.split(" ");
                            ArrayList<String> user = new ArrayList<>();
                            user.add(split[0]);
                            user.add(split[1]);
                            userDetails.add(user);
                        }
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
                adapter = new CustomAddAdapter(getActivity().getApplicationContext(), result, email, userDetails);
                list.setAdapter(adapter);

            } else {
            }
        }
    }
}
