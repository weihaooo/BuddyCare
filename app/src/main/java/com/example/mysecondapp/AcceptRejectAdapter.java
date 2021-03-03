package com.example.mysecondapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.mysecondapp.entity.AddBuddyEntity;
import com.example.mysecondapp.entity.AppBuddyEntity;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class AcceptRejectAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<String> items = new ArrayList<String>();
    private ArrayList<ArrayList<String>> userDetails = new ArrayList<ArrayList<String>>();
    private String email;
    private Context context;


    public AcceptRejectAdapter(Context context, ArrayList<String> items,String ownEmail, ArrayList<ArrayList<String>> userDetails) {
        //super(context, items);
        this.items = items;
        this.context = context;
        this.email = ownEmail;
        this.userDetails = userDetails;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int pos) {
        return items.get(pos);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_accept_reject_layout, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(items.get(position));

        //Handle buttons and add onClickListeners
        Button rejectBtn = (Button)view.findViewById(R.id.reject_btn);
        Button addBtn = (Button)view.findViewById(R.id.add_btn);

        rejectBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something

                AppBuddyTask rejBuddyTask = new AppBuddyTask(email,userDetails.get(position).get(1), position, false);
                rejBuddyTask.execute((Void) null);
                items.remove(position); //or some other task
                notifyDataSetChanged();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                AppBuddyTask appBuddyTask = new AppBuddyTask(email,userDetails.get(position).get(1), position, true);
                appBuddyTask.execute((Void) null);
                notifyDataSetChanged();
            }
        });

        return view;
    }
    public class AppBuddyTask extends AsyncTask<Void, Void, Boolean> {

        private final String ownEmail;
        private final String buddyEmail;
        private final int position;
        private final boolean approve;

        AppBuddyTask(String ownEmail, String buddyEmail, int position, boolean approve) {
            this.ownEmail = ownEmail;
            this.buddyEmail = buddyEmail;
            this.position = position;
            this.approve = approve;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                // Simulate network access.
                // Create URL
                URL webSvcEndpoint = new URL(context.getString(R.string.ip_address) +"WebServices-war/Resources/Account/approveBuddy");

                // Create connection
                HttpURLConnection myConnection =
                        (HttpURLConnection) webSvcEndpoint.openConnection();

                AppBuddyEntity appBuddyEntity = new AppBuddyEntity(ownEmail,buddyEmail, approve);
                myConnection.setRequestMethod("PUT");
                myConnection.setDoOutput(true);
                myConnection.setRequestProperty("Content-Type", "application/json");
                myConnection.setRequestProperty("Accept", "application/json");
                Gson gson = new Gson();
                OutputStreamWriter osw = new OutputStreamWriter(myConnection.getOutputStream());
                osw.write(gson.toJson(appBuddyEntity));
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

                            if(value){
                                jsonReader.close();
                                myConnection.disconnect();
                                return true;
                            } else{
                                jsonReader.close();
                                myConnection.disconnect();
                                return false;
                            }
                        } else {
                            jsonReader.skipValue(); // Skip values of other keys
                        }
                    }

                    jsonReader.close();
                    myConnection.disconnect();

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
                items.remove(position);
                userDetails.remove(position);
                notifyDataSetChanged();
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            //mAuthTask = null;
            //showProgress(false);
        }
    }

}
