package com.example.mysecondapp.job;



import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mysecondapp.R;
import com.example.mysecondapp.util.JobSchedulerUtil;

import java.util.Date;


public class TestJobService extends JobService
{
   @Override
    public boolean onStartJob(JobParameters params)
    {
        System.err.println("********** TestJobService.onStartJob");

        new TestAsyncTask().execute();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            JobSchedulerUtil.scheduleJob(getApplicationContext()); // reschedule the job
        }

        return true;
    }



    @Override
    public boolean onStopJob(JobParameters params)
    {
        return true;
    }



    protected class TestAsyncTask extends AsyncTask<String, Void, String>
    {
        String label;



        @Override
        protected void onPreExecute()
        {
        }

        @Override
        protected String doInBackground(String... params)
        {
            return new Date().toString();
        }

        @Override
        protected void onPostExecute(String val)
        {
            System.err.println("********** TestAsyncTask.onPostExecute: " + val);
        }
    }
}
