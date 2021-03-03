package com.example.mysecondapp.fragment;

import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.TextView;

import com.example.mysecondapp.R;
import com.example.mysecondapp.activity.LoginActivity;
import com.example.mysecondapp.activity.MainActivity;
import com.example.mysecondapp.activity.RegisterActivity;
import com.example.mysecondapp.entity.UserEntity;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Zhirong on 19/3/2018.
 */

public class ProfileFragment extends Fragment {
    //Display user's ProfileFragment

    private ProfileFragment.OnFragmentInteractionListener mListener;
    private View view;
    private SharedPreferences sharedPref;
    private UserEntity userEntity;
    private TextView emailView;
    private TextView nricView;
    private TextView nameView;
    private TextView genderView;
    private TextView dobView;
    private TextView contactView;
    private TextView eContactView;
    private TextView addressView;
    private TextView bloodTypeView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        return view;
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


    }

    @Override
    public void onResume(){
        super.onResume();
        sharedPref = this.getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.username);
        String email = sharedPref.getString(getString(R.string.username), defaultValue);
        GetProfileTask getProfileTask = new GetProfileTask(email);
        getProfileTask.execute((Void) null);
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

    /**
     * Represents an asynchronous registration task used to authenticate
     * the user.
     */
    public class GetProfileTask extends AsyncTask<Void, Void, Boolean> {

        private final String email;

        GetProfileTask(String email) {
            this.email = email;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                // Simulate network access.
                // Create URL
                URL webSvcEndpoint = new URL(getString(R.string.ip_address)+"WebServices-war/Resources/Account/profile/" + email);

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
                    userEntity = gson.fromJson(responseBodyReader,UserEntity.class);
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
                emailView.setText(userEntity.getEmail());
                nricView.setText(userEntity.getNric());
                nameView.setText(userEntity.getName());
                genderView.setText(userEntity.getGender());
                dobView.setText(userEntity.getDob());
                contactView.setText(userEntity.getContact());
                eContactView.setText(userEntity.geteContact());
                addressView.setText(userEntity.getAddress());
                bloodTypeView.setText(userEntity.getBloodType());
            } else {
            }
        }
    }
}
