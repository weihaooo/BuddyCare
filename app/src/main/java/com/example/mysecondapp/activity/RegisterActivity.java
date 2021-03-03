package com.example.mysecondapp.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.JsonReader;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.text.InputType;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView;
import android.text.Html;

import com.example.mysecondapp.R;
import com.example.mysecondapp.entity.UserEntity;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;

public class RegisterActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, OnClickListener{

    private EditText dob;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    private Button bloodType;
    private SharedPreferences sharedPref;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setBackgroundDrawableResource(R.drawable.background);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        findViewsById();
        sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);


        token = FirebaseInstanceId.getInstance().getToken();

        setDateTimeField();

        Button submitReg = (Button) findViewById(R.id.submit);
        Button cancelReg = (Button) findViewById(R.id.cancel);
        bloodType = (Button) findViewById(R.id.eBloodType);

       bloodType.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(RegisterActivity.this, bloodType);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.bloodtype_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        TextView textView = (TextView) findViewById(R.id.displayBloodType);
                        String output = "<b>" + item.getTitle() + " is selected" +  "</b>";
                        textView.setText(Html.fromHtml(output));
                        return true;
                    }
                });
                popup.show();
            }
        });
        submitReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ((EditText)findViewById(R.id.eEmail)).getText().toString();
                String nric = ((EditText)findViewById(R.id.eNric)).getText().toString();
                String name = ((EditText)findViewById(R.id.eName)).getText().toString();
                String password = ((EditText)findViewById(R.id.ePassword)).getText().toString();
                String contact = ((EditText)findViewById(R.id.eContactNum)).getText().toString();
                RadioGroup radioGroup = ((RadioGroup)findViewById(R.id.radioGender));
                String gender = "";
                if(radioGroup.getCheckedRadioButtonId()!=-1){
                    int id= radioGroup.getCheckedRadioButtonId();
                    View radioButton = radioGroup.findViewById(id);
                    int radioId = radioGroup.indexOfChild(radioButton);
                    RadioButton btn = (RadioButton) radioGroup.getChildAt(radioId);
                    gender = (String) btn.getText();
                }
                String address = ((EditText)findViewById(R.id.eAddress)).getText().toString();
                String bloodtype = ((Button)findViewById(R.id.eBloodType)).getText().toString();
                String dob = ((EditText)findViewById(R.id.eDob)).getText().toString();
                String isDoctor = "";
                RadioGroup radioDoc = ((RadioGroup)findViewById(R.id.radioDoc));
                if(radioDoc.getCheckedRadioButtonId()!=-1){
                    int id= radioDoc.getCheckedRadioButtonId();
                    View radioButton = radioDoc.findViewById(id);
                    int radioId = radioDoc.indexOfChild(radioButton);
                    RadioButton btn = (RadioButton) radioDoc.getChildAt(radioId);
                    isDoctor = (String) btn.getText();
                }
                String eContact = ((EditText)findViewById(R.id.eEmergencyContactNum)).getText().toString();

                UserEntity user = new UserEntity(nric, name, password, contact,gender,email,address,bloodtype,dob,isDoctor,eContact, token);

                RegisterTask registerTask = new RegisterTask(user);
                registerTask.execute((Void) null);
            }
        });
        cancelReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    //D.O.B section
    private void findViewsById() {
        dob = (EditText) findViewById(R.id.eDob);
        dob.setInputType(InputType.TYPE_NULL);
    }

    private void setDateTimeField() {
        dob.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dob.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(View view) {
        if(view == dob) {
            datePickerDialog.show();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }


    /**
     * Represents an asynchronous registration task used to authenticate
     * the user.
     */
    public class RegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final UserEntity user;

        RegisterTask(UserEntity user) {
            this.user = user;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                // Simulate network access.
                // Create URL
                URL webSvcEndpoint = new URL(getString(R.string.ip_address)+"WebServices-war/Resources/Account/register");

                // Create connection
                HttpURLConnection myConnection =
                        (HttpURLConnection) webSvcEndpoint.openConnection();


                myConnection.setRequestMethod("PUT");
                myConnection.setDoOutput(true);
                myConnection.setRequestProperty("Content-Type", "application/json");
                myConnection.setRequestProperty("Accept", "application/json");
                Gson gson = new Gson();
                OutputStreamWriter osw = new OutputStreamWriter(myConnection.getOutputStream());
                osw.write(gson.toJson(user));
                osw.flush();
                osw.close();

                int test = myConnection.getResponseCode();

                if (myConnection.getResponseCode() == 200) {
                    Log.i("testvalue", "here5");
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
                            String value = jsonReader.nextString();

                            if(value.equalsIgnoreCase("1")){
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
                    Log.i("testvalue", String.valueOf(test));
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
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
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
