package com.example.mysecondapp.entity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by FABIAN on 10-Apr-18.
 */

public class HeartRateEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private String timestamp;
    private String heartRate;
    private String email;


    public HeartRateEntity(){

    }

    public HeartRateEntity(String email,String heartRate,String timestamp){
        this.email = email;
        this.heartRate = heartRate;
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
