package com.example.mysecondapp.service;


import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

public class HelloIntentService extends IntentService
{
    public HelloIntentService()
    {
        super("HelloIntentService");
    }



    @Override
    protected void onHandleIntent(Intent intent)
    {
        try
        {
            Thread.sleep(5000);
            System.err.println("********** HelloIntentService.onHandleIntent");
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Toast.makeText(this, "HelloIntentService service is starting", Toast.LENGTH_SHORT).show();

        return super.onStartCommand(intent, flags, startId);
    }
}
