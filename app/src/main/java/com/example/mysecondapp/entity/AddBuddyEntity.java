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
public class AddBuddyEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    public String ownEmail;
    public String buddyEmail;


    public AddBuddyEntity(String ownEmail, String buddyEmail) {
        this.ownEmail = ownEmail;
        this.buddyEmail = buddyEmail;
    }

    public AddBuddyEntity() {
    }

    public String getOwnEmail() {
        return ownEmail;
    }

    public void setOwnEmail(String ownEmail) {
        this.ownEmail = ownEmail;
    }

    public String getBuddyEmail() {
        return buddyEmail;
    }

    public void setBuddyEmail(String buddyEmail) {
        this.buddyEmail = buddyEmail;
    }
}

