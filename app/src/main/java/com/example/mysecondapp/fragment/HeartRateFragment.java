package com.example.mysecondapp.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;


import com.example.mysecondapp.R;
import com.example.mysecondapp.entity.HeartRateEntity;
import com.example.mysecondapp.entity.UserEntity;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Zhirong on 19/3/2018.
 */

public class HeartRateFragment extends Fragment implements View.OnClickListener {


    private static final String Sendheartkey = "com.example.mysecondapp.key.send.heart.rate";
    private HeartRateFragment.OnFragmentInteractionListener mListener;
    private View view;
    private SharedPreferences sharedPref;
    public String email;
    private ArrayList<ArrayList<String>> heartRateDetails = new ArrayList<>();
    public List<Double> heartRateList = new ArrayList<Double>();
    public GraphView graph;
    public EditText et_start_date;
    public EditText et_end_date;
    public Button btn_send_request;
    private DatePickerDialog datePickerStartDialog;
    private DatePickerDialog datePickerEndDialog;
    private SimpleDateFormat dateFormatter;
    public boolean isAllHeartRate = true;
    public Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = this.getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.username);
        email = sharedPref.getString(getString(R.string.username), defaultValue);
        RetrieveHeartRateTask retrieveTask = new RetrieveHeartRateTask(email, "01-01-2017 00:00:00", "31-12-2019 23:59:59");
        isAllHeartRate = false;
        retrieveTask.execute((Void) null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_heart_rate, container, false);

        graph = (GraphView) view.findViewById(R.id.heart_rate_graph);
        btn_send_request = (Button) view.findViewById(R.id.btn_send_request);
        et_start_date = (EditText) view.findViewById(R.id.et_start_date);
        et_end_date = (EditText) view.findViewById(R.id.et_end_date);


        btn_send_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrieveHeartRateTask retrieveTask = new RetrieveHeartRateTask(email, et_start_date.getText().toString(), et_end_date.getText().toString());
                graph.removeAllSeries();
                isAllHeartRate = true;
                retrieveTask.execute((Void) null);
            }
        });


        //date picker
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        findViewsById();
        setDateTimeField();

        return view;
    }

    public class RetrieveHeartRateTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mStartDate;
        private final String mEndDate;

        RetrieveHeartRateTask(String email, String startDate, String endDate) {
            this.mEmail = email;
            this.mStartDate = startDate;
            this.mEndDate = endDate;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                // Simulate network access.
                // Create URL
                URL webSvcEndpoint = new URL(getString(R.string.ip_address) + "WebServices-war/Resources/HeartRate/retrieve/" + mEmail + "/" + mStartDate + "/" + mEndDate);

                // Create connection
                HttpURLConnection myConnection = (HttpURLConnection) webSvcEndpoint.openConnection();

                System.err.println("Response Code is: " + myConnection.getResponseCode());
                if (myConnection.getResponseCode() == 200) {
                    // Success
                    // Further processing here
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader =
                            new InputStreamReader(responseBody, "UTF-8");
                    JsonReader jsonReader = new JsonReader(responseBodyReader);
                    heartRateDetails = new ArrayList<ArrayList<String>>();
                    jsonReader.beginObject(); // Start processing the JSON object
                    String key = jsonReader.nextName();
                    jsonReader.beginArray();

                    while (jsonReader.hasNext()) { // Loop through all keys
                        String[] split = jsonReader.nextString().split(" ");
                        ArrayList<String> list = new ArrayList<>();
                        list.add(split[0]);
                        list.add(split[1] + " " + split[2]);
                        heartRateDetails.add(list);
                    }
                    myConnection.disconnect();
                    return true;

                } else {
                    System.err.print("Did not get response 200");
                    //Log.i("testvalue", String.valueOf(test));
                    // Error handling code goes here
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("Exception" + e.toString());
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {

                DataPoint[] dataPoints = new DataPoint[heartRateDetails.size()];

                ArrayList<String> result = new ArrayList<String>();
                for (int i = 0; i < heartRateDetails.size(); i++) {
                    result.add(heartRateDetails.get(i).get(0) + " " + heartRateDetails.get(i).get(1));
                    String s = heartRateDetails.get(i).get(1);
                    try {

                        Date date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(s);
                        dataPoints[i] = new DataPoint(date, Double.parseDouble(heartRateDetails.get(i).get(0)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

                //print to see the results
                for (int i = 0; i < result.size(); i++) {
                    System.err.println(result.get(i));
                }

                LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
                series.setTitle("Heart Rate Graph");
                series.setDrawDataPoints(true);
                if (isAllHeartRate) {
                    graph.getGridLabelRenderer().setHorizontalAxisTitle("From: " + mStartDate + " to " + mEndDate);
                } else {
                    graph.getGridLabelRenderer().setHorizontalAxisTitle("Complete Heart Rate Chart");
                }
                graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
                graph.getViewport().setScrollable(true);
                //graph.getViewport().setScalable(true);
                graph.getLegendRenderer().setVisible(true);
                graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                //series.setBackgroundColor(Color.BLACK);
                graph.addSeries(series);
                /*series.setBackgroundColor(Color.argb(255, 255, 60, 60));
                series.setDrawDataPoints(true);
                graph.addSeries(series);
                graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
                graph.getGridLabelRenderer().setHorizontalAxisTitle("From " + et_end_date.getText() + " to" + et_start_date);
                //graph.getGridLabelRenderer().setHumanRounding(false);
                graph.getLegendRenderer().setVisible(true);
                graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);*/

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

    //D.O.B section
    private void findViewsById() {
        et_start_date = (EditText) view.findViewById(R.id.et_start_date);
        et_start_date.setInputType(InputType.TYPE_NULL);
        et_end_date = (EditText) view.findViewById(R.id.et_end_date);
        et_end_date.setInputType(InputType.TYPE_NULL);
    }

    private void setDateTimeField() {
        et_start_date.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        datePickerStartDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                et_start_date.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        et_end_date.setOnClickListener(this);

        Calendar newCalendar2 = Calendar.getInstance();
        datePickerEndDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                et_end_date.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar2.get(Calendar.YEAR), newCalendar2.get(Calendar.MONTH), newCalendar2.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(View view) {
        if (view == et_start_date) {
            datePickerStartDialog.show();
        }
        if (view == et_end_date) {
            datePickerEndDialog.show();
        }
    }
}
