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

    public User(String email, String token) {
        this.email = email;
        this.token = token;
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
}
