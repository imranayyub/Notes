package com.example.imran.notes.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by imran on 16/1/18.
 */
//Model for the userDetails.
public class User {

    @SerializedName("email")
    @Expose
    String email;

    @SerializedName("token")
    @Expose
    String token;

    @SerializedName("fcm_token")
    @Expose
    String fcm_token;

    public User(String email, String token,String fcm_token) {
        this.email = email;
        this.token = token;
        this.fcm_token=fcm_token;
    }

    public User() {

    }

    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFcm_token() {
        return fcm_token;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }
}
