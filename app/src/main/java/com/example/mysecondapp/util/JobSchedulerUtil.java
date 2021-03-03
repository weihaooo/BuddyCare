package com.example.mysecondapp.util;


import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.example.mysecondapp.job.TestJobService;

public class JobSchedulerUtil
{
    public JobSchedulerUtil()
    {
    }



    public static void scheduleJob(Context context)
    {
        System.err.println("********** JobSchedulerUtil.scheduleJob");

        ComponentName serviceComponent = new ComponentName(context, TestJobService.class);

        JobInfo.Builder builder = new JobInfo.Builder(8080, serviceComponent);
        builder.setRequiresCharging(true); // device should be charging

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            builder.setMinimumLatency(3 * 1000); // wait at least 3 sec
        }
        else
        {
            builder.setPeriodic(3 * 1000); // job will repeat every 3 sec
        }

        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }



    public static void cancelJob(Context context)
    {
        System.err.println("********** JobSchedulerUtil.cancelJob");

        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.cancel(8080);
    }

}