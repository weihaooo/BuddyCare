package com.example.mysecondapp.job;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.example.mysecondapp.util.JobSchedulerUtil;



public class MyStartServiceReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        System.err.println("********** MyStartServiceReceiver.onReceive");

        if(intent.getAction().equals("com.example.mysecondapp.START_JOB_INTENT"))
        {
            JobSchedulerUtil.scheduleJob(context);
        }
        else if(intent.getAction().equals("com.example.mysecondapp.CANCEL_JOB_INTENT"))
        {
            JobSchedulerUtil.cancelJob(context);
        }

    }
}