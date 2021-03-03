package com.example.mysecondapp.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mysecondapp.R;
import com.example.mysecondapp.activity.MainActivity;
import com.example.mysecondapp.entity.UpdateTokenEntity;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SharedPreferences sharedPref;

    URL url = null;
    ListView lvData;
    ArrayList<String> titles;
    ArrayList<String> newTitles = new ArrayList<String>();
    ArrayList<String> links;
    ArrayList<String> description;
    ArrayList<String> pubdate;
    ArrayList<String> images;
    View view;

    //private OnFragmentInteractionListener mListener;
    private OnLinkSelectedListener mCallback;

    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

        sharedPref = this.getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        String token = FirebaseInstanceId.getInstance().getToken();

        String defaultValue = getResources().getString(R.string.username);
        String email = sharedPref.getString(getString(R.string.username), defaultValue);
        UpdateTokenEntity ut = new UpdateTokenEntity(token, email);
        UpdateTokenTask updateTokenTask = new UpdateTokenTask(ut);
        updateTokenTask.execute((Void) null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        titles = new ArrayList<String>();
        description = new ArrayList<String>();
        pubdate = new ArrayList<String>();
        links = new ArrayList<String>();
        images = new ArrayList<String>();
        lvData = (ListView) view.findViewById(R.id.rssList);
        new ProcessInBackground().execute();

        return view;
    }

    /*// TODO: Rename method, update argument and hook method into UI event
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
    }*/

    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        }
        catch (IOException e)
        {
            return null;
        }
    }

    public class ProcessInBackground extends AsyncTask<Integer, Void, Exception> {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        Exception exception = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Loading RSS Feed...");
            progressDialog.show();
        }

        @Override
        protected Exception doInBackground(Integer... integers) {

            try {

                URL url = new URL("http://www.health.com/nutrition/feed");
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                // creates new instance of pull parser factory that allows XML retrieval
                factory.setNamespaceAware(false);
                // parser produced does not support XML namespaces
                XmlPullParser xpp = factory.newPullParser();
                //new instance of parser, extracts xml document data
                xpp.setInput(getInputStream(url), "UTF_8");
                //encoding is in UTF8
                boolean insideItem = false;
                int eventType = xpp.getEventType();
                //when we start reading, it returns the type of current event i.e. tag type

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = true;
                        } else if (xpp.getName().equalsIgnoreCase("title")) {
                            if (insideItem) {
                                titles.add(xpp.nextText());
                            }
                        } else if (xpp.getName().equalsIgnoreCase("link")) {
                            if (insideItem) {
                                links.add(xpp.nextText());
                            }
                        } else if (xpp.getName().equalsIgnoreCase("description")) {
                            if (insideItem) {
                                description.add(xpp.nextText());
                            }
                        } else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                            if (insideItem) {
                                pubdate.add(xpp.nextText());
                            }
                        }else if (xpp.getName().equalsIgnoreCase("image")) {
                            if (insideItem) {
                                images.add(xpp.nextText());
                            }
                        }
                    } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = false;
                    }

                    eventType = xpp.next();
                }

            } catch (MalformedURLException e) {
                exception = e;
            } catch (XmlPullParserException e) {
                exception = e;
            } catch (IOException e) {
                exception = e;
            }


            return exception;
        }

        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);
            //final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, titles);
            //adapter = new CustomRSSAdapter(getActivity().getApplicationContext(), titles, images, links);
            /*lvData.setAdapter(adapter);
            lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                        long arg3) {
                    String link = links.get(arg2);
                    //String link = adapter.getLinks(arg2);
                    mCallback.onLinkSelected(link);
                }

            });
            //connects to data to the list view*/
            for(int i=0; i < titles.size(); i++){
                String text = titles.get(i);
                text= replaceAll(text,"&quot;","\"");

                text= replaceAll(text,"&amp;","&");

                text= replaceAll(text,"&rsquo;","’");

                text= replaceAll(text,"&nbsp;","");

                text= replaceAll(text,"&mdash;","-");

                newTitles.add(text);
            }

            ListView l1=(ListView)view.findViewById(R.id.rssList);
            l1.setAdapter(new dataListAdapter(newTitles, images, links));
            l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                        long arg3) {
                    String link = links.get(arg2);
                    //String link = adapter.getLinks(arg2);
                    mCallback.onLinkSelected(link);
                }
            });
            progressDialog.dismiss();
        }
    }

    // Container Activity must implement this interface
    public interface OnLinkSelectedListener {
        void onLinkSelected(String link);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity;

        if (context instanceof Activity) {
            activity = (Activity) context;

            // This makes sure that the container activity has implemented
            // the callback interface. If not, it throws an exception
            try {
                mCallback = (OnLinkSelectedListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnLinkSelectedListener");
            }
        }
    }

    class dataListAdapter extends BaseAdapter {
        ArrayList<String> titles;
        ArrayList<String> links;
        ArrayList<String> images;

        dataListAdapter() {
            titles = null;
            links = null;
            images = null;
        }

        public dataListAdapter(ArrayList<String> rssTitles, ArrayList<String> rssLinks, ArrayList<String> rssImages) {
            this.titles = rssTitles;
            this.links = rssLinks;
            this.images = rssImages;
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return titles.size();
        }

        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;
            if (view == null) {
                //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LayoutInflater inflater = getLayoutInflater();
                view = inflater.inflate(R.layout.custom_rss, null);
            }
            TextView title;
            ImageView img;
            title = (TextView) view.findViewById(R.id.title);
            img=(ImageView)view.findViewById(R.id.img);
            title.setText(titles.get(position));

            return (view);
        }
    }

    public class UpdateTokenTask extends AsyncTask<Void, Void, Boolean> {

        private final UpdateTokenEntity ut;

        UpdateTokenTask(UpdateTokenEntity ut) {
            this.ut = ut;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                // Simulate network access.
                // Create URL
                URL webSvcEndpoint = new URL(getString(R.string.ip_address) + "WebServices-war/Resources/Account/updateToken");

                // Create connection
                HttpURLConnection myConnection =
                        (HttpURLConnection) webSvcEndpoint.openConnection();

                myConnection.setRequestMethod("PUT");
                myConnection.setDoOutput(true);
                myConnection.setRequestProperty("Content-Type", "application/json");
                myConnection.setRequestProperty("Accept", "application/json");
                Gson gson = new Gson();
                OutputStreamWriter osw = new OutputStreamWriter(myConnection.getOutputStream());
                osw.write(gson.toJson(ut));
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
    private String replaceAll(String source, String pattern, String replacement) {
        if (source == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        int index;
        int patIndex = 0;
        while ((index = source.indexOf(pattern, patIndex)) != -1) {
            sb.append(source.substring(patIndex, index));
            sb.append(replacement);
            patIndex = index + pattern.length();
        }
        sb.append(source.substring(patIndex));
        return sb.toString();
    }

}
