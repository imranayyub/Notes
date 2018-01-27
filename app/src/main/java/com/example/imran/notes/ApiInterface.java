package com.example.imran.notes;

import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by imran on 27/1/18.
 */

public interface ApiInterface {

     String BASE_URL = "http://192.168.20.78:1337/";

    @POST("login")
    Call<User> loginUser(@Body User body);

    @POST("note")
    Call<List<NoteList>> notes(@Body NoteList body);
    @POST("note/add")
    Call<NoteList> addNote(@Body NoteList body);
}
