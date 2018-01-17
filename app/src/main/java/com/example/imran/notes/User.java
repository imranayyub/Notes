package com.example.imran.notes;

/**
 * Created by imran on 16/1/18.
 */

public class User {
String uId,username,password;

    public User(String uId, String username, String password) {
        this.uId = uId;
        this.username = username;
        this.password = password;
    }

    public User()
    {

    }
    public String getuId() {
        return uId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
