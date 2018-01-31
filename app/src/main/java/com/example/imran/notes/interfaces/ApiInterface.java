package com.example.imran.notes.interfaces;

import com.example.imran.notes.model.NoteList;
import com.example.imran.notes.model.SharedNotes;
import com.example.imran.notes.model.User;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by imran on 27/1/18.
 */

public interface ApiInterface {

    //Base Url.
     String BASE_URL = "http://192.168.20.86:1337/";

     //checks if user is already registered otherwise logs in .
    @POST("login")
    Call<User> loginUser(@Body User body);

    @POST("user/updatetoken")
    Call<User> updateFcmToken(@Body User body);


    //fetches all the notes from server database.
    @POST("note")
    Call<List<NoteList>> notes(@Body NoteList body);

    //save a note on the server.
    @POST("note/add")
    Call<NoteList> addNote(@Body NoteList body);

    //deletes note from server.
    @POST("note/delete")
    Call<JsonObject> deleteNote(@Body NoteList body);

    //shares note to the registered user.
    @POST("note/share")
    Call<JsonObject> shareNote(@Body SharedNotes body);

    //fetches SharedNotes.
    @POST("note/shared")
    Call<List<NoteList>> sharedNote(@Body SharedNotes body);

    //gets id ofthe note to be edited.
    @POST("note/getedit")
    Call<NoteList> editNoteId(@Body NoteList body);

    //update the edits done to the note.
    @POST("note/edit")
    Call<JsonObject> editNote(@Body NoteList body);

    //gets id of the Shared note to be edited.
    @POST("note/shared/getedit")
    Call<NoteList> editSharedNoteId(@Body SharedNotes body);

    //update the edits done to the Shared note.
    @POST("note/shared/edit")
    Call<JsonObject> editSharedNote(@Body SharedNotes body);

    //deletes Sharednote from server.
    @POST("note/shared/delete")
    Call<JsonObject> deleteSharedNote(@Body SharedNotes body);

}
