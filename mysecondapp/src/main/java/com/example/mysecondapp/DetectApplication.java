package com.example.mysecondapp;

/**
 * Created by FABIAN on 03-Apr-18.
 */

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.phone.PhoneDeviceType;
import android.widget.TextView;

import com.example.mysecondapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DetectApplication extends WearableActivity {

    TextView txtview_show_connected_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detect_application);

        txtview_show_connected_msg = findViewById(R.id.txtview_show_connected_msg);

        // Enables Always-on
        setAmbientEnabled();

        String phoneDeviceTypeName = "";
        int phoneDeviceType = PhoneDeviceType.getPhoneDeviceType(this);

        switch (phoneDeviceType) {
            case PhoneDeviceType.DEVICE_TYPE_ANDROID:
                phoneDeviceTypeName = "Android";
                break;
            case PhoneDeviceType.DEVICE_TYPE_IOS:
                phoneDeviceTypeName = "iOS";
                break;
            case PhoneDeviceType.DEVICE_TYPE_ERROR_UNKNOWN:
                phoneDeviceTypeName = "Unknown";
                break;
        }

        System.err.println("!!!!!!!!!!CHECKING PHONE DEVICE TYPE NAME HERE!!!!!!!: " + phoneDeviceTypeName);

        if (phoneDeviceTypeName.equals("Android") || phoneDeviceTypeName.equals("iOS")) {
            System.err.println("*******************HERE1");
            Task<Map<String, CapabilityInfo>> capabilitiesTask = Wearable.getCapabilityClient(this).getAllCapabilities(CapabilityClient.FILTER_REACHABLE);
            capabilitiesTask.addOnSuccessListener(new OnSuccessListener<Map<String, CapabilityInfo>>() {
                @Override
                public void onSuccess(Map<String, CapabilityInfo> capabilityInfoMap) {
                    System.err.println("*******************HERE2");

                    Set<Node> nodes = new HashSet<>();

                    if (capabilityInfoMap.isEmpty()) {
                        System.err.println("*******************HERE3");
                        return;
                    }

                    for (CapabilityInfo capabilityInfo : capabilityInfoMap.values()) {
                        if (capabilityInfo != null) {
                            System.err.println("*******************HERE4");
                            updateTextViewDataOutput("CapabilityInfo: " + capabilityInfo.getName());

                            for (Node node : capabilityInfo.getNodes()) {
                                updateTextViewDataOutput("\tNode: " + node.getDisplayName());
                            }
                        }
                    }
                }
            });
        } else {
            updateTextViewDataOutput("Not connected to any phone" + "\n\n Please make sure that you are paired with a mobile phone");
        }
    }


    private void updateTextViewDataOutput(String s) {
        if (s.contains("CapabilityInfo") || s.contains("Node")) {
            txtview_show_connected_msg.append("Successfully Connected!" + " \n\nYou can begin tracking your heart rate or enable fall detection");
        } else {
            txtview_show_connected_msg.append("Hello");
        }
    }

}
