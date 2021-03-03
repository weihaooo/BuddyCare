package com.example.mysecondapp.entity;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.Serializable;

/**
 *
 * @author User
 */
public class LocationEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private String email;
    private String uuid;

    //CONSTRUCTORS
    public LocationEntity(){

    }

    public LocationEntity(String email, String uuid){
        this.email = email;
        this.uuid = uuid;
    }


    //GETTER & SETTERS

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationEntity that = (LocationEntity) o;

        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        return uuid != null ? uuid.equals(that.uuid) : that.uuid == null;
    }

    @Override
    public int hashCode() {
        int result = email != null ? email.hashCode() : 0;
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        return result;
    }
}

