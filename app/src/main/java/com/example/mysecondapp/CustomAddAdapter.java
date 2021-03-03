package com.example.mysecondapp;

import android.content.Context;
import android.content.Intent;
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

import com.example.mysecondapp.activity.LoginActivity;
import com.example.mysecondapp.activity.RegisterActivity;
import com.example.mysecondapp.entity.AddBuddyEntity;
import com.example.mysecondapp.entity.UserEntity;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class CustomAddAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<String> items = new ArrayList<String>();
    private ArrayList<ArrayList<String>> userDetails = new ArrayList<ArrayList<String>>();
    private String email;
    private Context context;


    public CustomAddAdapter(Context context, ArrayList<String> items,String ownEmail, ArrayList<ArrayList<String>> userDetails) {
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
            view = inflater.inflate(R.layout.custom_add_layout, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(items.get(position));

        //Handle buttons and add onClickListener
        Button addBtn = (Button)view.findViewById(R.id.add_btn);

        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                notifyDataSetChanged();
                AddBuddyTask addBuddyTask = new AddBuddyTask(email,userDetails.get(position).get(1), position);
                addBuddyTask.execute((Void) null);
            }
        });

        return view;
    }
    public class AddBuddyTask extends AsyncTask<Void, Void, Boolean> {

        private final String ownEmail;
        private final String buddyEmail;
        private final int position;

        AddBuddyTask(String ownEmail, String buddyEmail, int position) {
            this.ownEmail = ownEmail;
            this.buddyEmail = buddyEmail;
            this.position = position;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.i("test", String.valueOf(position) + ownEmail + buddyEmail);
            try {
                // Simulate network access.
                // Create URL
                URL webSvcEndpoint = new URL(context.getString(R.string.ip_address)+"WebServices-war/Resources/Account/addBuddy");

                // Create connection
                HttpURLConnection myConnection =
                        (HttpURLConnection) webSvcEndpoint.openConnection();

                AddBuddyEntity addBuddyEntity = new AddBuddyEntity(ownEmail,buddyEmail);
                myConnection.setRequestMethod("PUT");
                myConnection.setDoOutput(true);
                myConnection.setRequestProperty("Content-Type", "application/json");
                myConnection.setRequestProperty("Accept", "application/json");
                Gson gson = new Gson();
                OutputStreamWriter osw = new OutputStreamWriter(myConnection.getOutputStream());
                osw.write(gson.toJson(addBuddyEntity));
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
