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
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private String nric;
    private String name;
    private String password;
    private String contact;
    private String gender;
    private String email;
    private String address;
    private String bloodType;
    private String dob;
    private String isDoctor;
    private String eContact;
    private String token;
    //CONSTRUCTORS
    public UserEntity(){

    }

    public UserEntity(String nric, String name, String password, String contact, String gender, String email, String address, String bloodType, String dob,  String isDoctor, String eContact, String token){
        this.nric = nric;
        this.name = name;
        this.password = password;
        this.contact = contact;
        this.gender = gender;
        this.email = email;
        this.address = address;
        this.bloodType = bloodType;
        this.dob = dob;
        this.isDoctor = isDoctor;
        this.eContact = eContact;
        this.token = token;
    }


    //GETTER & SETTERS
    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String isIsDoctor() {
        return isDoctor;
    }

    public void setIsDoctor(String isDoctor) {
        this.isDoctor = isDoctor;
    }

    public String geteContact() {
        return eContact;
    }

    public void seteContact(String eContact) {
        this.eContact = eContact;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nric != null ? nric.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserEntity)) {
            return false;
        }
        UserEntity other = (UserEntity) object;
        return !((this.nric == null && other.nric != null) || (this.nric != null && !this.nric.equals(other.nric)));
    }



}

