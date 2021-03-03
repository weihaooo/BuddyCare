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
public class UpdateTokenEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    public String token;
    public String email;


    public UpdateTokenEntity(String token, String email) {
        this.token = token;
        this.email = email;
    }

    public UpdateTokenEntity() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

