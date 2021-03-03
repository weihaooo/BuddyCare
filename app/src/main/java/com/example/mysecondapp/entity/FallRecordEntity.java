package com.example.mysecondapp.entity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by User on 4/18/2018.
 */

public class FallRecordEntity implements Serializable {
    private String time;
    private String degree;
    private String email;

    public FallRecordEntity(){

    }

    public FallRecordEntity(String time, String degree, String email){
        this.time = time;
        this.degree = degree;
        this.email = email;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "FallRecordEntity{" +
                "time=" + time +
                ", degree='" + degree + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
