package com.example.imran.notes.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import com.example.imran.notes.interfaces.ApiInterface;
import com.example.imran.notes.adapter.NoteAdapter;
import com.example.imran.notes.model.NoteList;
import com.example.imran.notes.R;
import com.example.imran.notes.activities.HomeActivity;
import com.example.imran.notes.model.SharedNotes;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.imran.notes.activities.HomeActivity.fab;
import static com.example.imran.notes.activities.HomeActivity.isShared;
import static com.example.imran.notes.activities.LoginActivity.email;
import static com.example.imran.notes.activities.LoginActivity.serverToken;
import static com.example.imran.notes.adapter.NoteAdapter.editNote;
import static com.example.imran.notes.adapter.NoteAdapter.editNoteColor;
import static com.example.imran.notes.adapter.NoteAdapter.editNoteId;
import static com.example.imran.notes.adapter.NoteAdapter.editNoteTag;
import static com.example.imran.notes.adapter.NoteAdapter.editNoteTitle;

/**
 * Created by imran on 13/1/18.
 */

public class AddNoteFragment extends Fragment implements View.OnClickListener {

    EditText note, noteTitle, noteTag;
    Button addNote, cancel;
    String notes, title, tag, color;
    static String changecolor;
    FloatingActionButton addColor;
    Button color1, color2, color3, color4, color5, color6, color7, defaultcolor;
    HorizontalScrollView colormenu;
    FrameLayout addNoteFrameLyout;
    int count = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.addnotefragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //finds element in xml.
        note = (EditText) getActivity().findViewById(R.id.note);
        noteTag = (EditText) getActivity().findViewById(R.id.noteTag);
        noteTitle = (EditText) getActivity().findViewById(R.id.noteTitle);
        addNote = (Button) getActivity().findViewById(R.id.addNote);
        cancel = (Button) getActivity().findViewById(R.id.cancel);
        addColor = (FloatingActionButton) getActivity().findViewById(R.id.addColor);
        addNoteFrameLyout = (FrameLayout) getActivity().findViewById(R.id.addNoteFragment);
        //setting floating Action button invisible.
        fab.setVisibility(View.INVISIBLE);
        //checks if note is to be edited.
        if (editNoteId != null) {
            note.setText(editNote);
            noteTitle.setText(editNoteTitle);
            noteTag.setText(editNoteTag);
            if (editNoteColor != null)
                addNoteFrameLyout.setBackgroundColor(Color.parseColor(editNoteColor));//87CEEB
        }
        //sets onClickListener on buttons.
        addNote.setOnClickListener(this);
        cancel.setOnClickListener(this);
        addColor.setOnClickListener(this);
        colormenu = (HorizontalScrollView) getActivity().findViewById(R.id.colormenu);
        color1 = (Button) getActivity().findViewById(R.id.color1);
        color2 = (Button) getActivity().findViewById(R.id.color2);
        color3 = (Button) getActivity().findViewById(R.id.color3);
        color4 = (Button) getActivity().findViewById(R.id.color4);
        color5 = (Button) getActivity().findViewById(R.id.color5);
        color6 = (Button) getActivity().findViewById(R.id.color6);
        color7 = (Button) getActivity().findViewById(R.id.color7);
        defaultcolor = (Button) getActivity().findViewById(R.id.defaultcolor);

        colormenu.setVisibility(View.GONE);
        color1.setOnClickListener(this);
        color2.setOnClickListener(this);
        color2.setOnClickListener(this);
        color3.setOnClickListener(this);
        color4.setOnClickListener(this);
        color5.setOnClickListener(this);
        color6.setOnClickListener(this);
        color7.setOnClickListener(this);
        defaultcolor.setOnClickListener(this);


    }

    //oncClick method to perform action according to the button being clicked.
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.addNote: {
                notes = note.getText().toString();
                title = noteTitle.getText().toString();
                tag = noteTag.getText().toString();
                if (tag == null)
                    tag = "";
                if (title == null)
                    title = "";
                if (notes.length() == 0)
                    Toast.makeText(getActivity(), "Note Empty", Toast.LENGTH_SHORT).show();
                else if (editNoteId != null) {
                    if (isShared == 1)
                        editSharedNote(email, title, notes, changecolor, tag, editNoteId);
                    else
                        editNote(email, title, notes, changecolor, tag, editNoteId);
//                    Intent intent;
//                    intent = new Intent(getActivity(), HomeActivity.class);
//                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "Note added successfully", Toast.LENGTH_SHORT).show();
                    Intent intent;
                    addNote(email, title, notes, changecolor, tag);
                    intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                }
                break;
            }
            //In case cancel button is pressed.
            case R.id.cancel: {
                editNoteId = null;
                Intent intent;
                intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
                break;

            }
            //to add color to note.
            case R.id.addColor: {

                if (count == 0) {
                    count++;
                    colormenu.setVisibility(View.VISIBLE);
                } else {
                    colormenu.setVisibility(View.GONE);
                    count--;
                }
                break;
            }
            case R.id.defaultcolor: {
                color = "#ffffff";
                changecolor = color;
                addNoteFrameLyout.setBackgroundColor(Color.parseColor("#ffffff"));//87CEEB
                break;
            }
            case R.id.color1: {
                color = "#893F45";
                changecolor = color;
                addNoteFrameLyout.setBackgroundColor(Color.parseColor(color));
                break;
            }
            case R.id.color2: {
                color = "#ffac99";
                changecolor = color;
                addNoteFrameLyout.setBackgroundColor(Color.parseColor(color));
                break;
            }
            case R.id.color3: {
                color = "#87A96B";
                changecolor = color;
                addNoteFrameLyout.setBackgroundColor(Color.parseColor(color));
                break;
            }
            case R.id.color4: {
                color = "#3D2B1F";
                changecolor = color;
                addNoteFrameLyout.setBackgroundColor(Color.parseColor(color));
                break;
            }
            case R.id.color5: {
                color = "#004225";
                changecolor = color;
                addNoteFrameLyout.setBackgroundColor(Color.parseColor(color));
                break;
            }
            case R.id.color6: {
                color = "#800020";
                changecolor = color;
                addNoteFrameLyout.setBackgroundColor(Color.parseColor(color));
                break;
            }
            case R.id.color7: {
                color = "#614051";
                changecolor = color;
                addNoteFrameLyout.setBackgroundColor(Color.parseColor(color));
                break;
            }

        }
    }

    //edits Note and save it on server.
    private void editNote(String email, String title, String note, String color, String tag, String editNoteId) {

        //HttpCLient to Add Authorization Header.
        OkHttpClient defaultHttpClient = new OkHttpClient.Builder()
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public okhttp3.Response intercept(Chain chain) throws IOException {
                                Request request = chain.request().newBuilder()
                                        .addHeader("authorization", "bearer " + serverToken).build();
                                return chain.proceed(request);
                            }
                        }).build();
        //Retrofit to retrieve JSON data from server.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiInterface.BASE_URL)
                .client(defaultHttpClient)
                .addConverterFactory(GsonConverterFactory.create())     //Using GSON to Convert JSON into POJO.
                .build();

        ApiInterface apiService = retrofit.create(ApiInterface.class);
        try {
            NoteList noteList = new NoteList(email, title, note, color, tag, editNoteId);
            apiService.editNote(noteList).enqueue(new Callback<JsonObject>() {
                //In case server responds.
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        Log.i("here:", "post submitted to API." + response.body().toString());
                        NoteAdapter.editNoteId = null;
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                        Toast.makeText(getActivity(), "Note edit successfully..!! ", Toast.LENGTH_SHORT).show();

                    } else if (response.code() == 500) {
                        Toast.makeText(getActivity(), "Some Error occured(Iternal ", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 404) {
                        Toast.makeText(getActivity(), "wrong..", Toast.LENGTH_SHORT).show();
                    }

                }

                //In case of failure to connect to server.
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    t.printStackTrace();
                    Log.e("here", "Unable to submit post to API.");
                    Toast.makeText(getActivity(), "failed ", Toast.LENGTH_SHORT).show();

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //edits SharedNote and save it on the server.
    private void editSharedNote(String email, String title, String note, String color, String tag, String editNoteId) {

        //HttpCLient to Add Authorization Header.
        OkHttpClient defaultHttpClient = new OkHttpClient.Builder()
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public okhttp3.Response intercept(Chain chain) throws IOException {
                                Request request = chain.request().newBuilder()
                                        .addHeader("authorization", "bearer " + serverToken).build();
                                return chain.proceed(request);
                            }
                        }).build();
        //Retrofit to retrieve JSON data from server.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiInterface.BASE_URL)
                .client(defaultHttpClient)
                .addConverterFactory(GsonConverterFactory.create())     //Using GSON to Convert JSON into POJO.
                .build();

        ApiInterface apiService = retrofit.create(ApiInterface.class);
        try {
            SharedNotes sharedNotes = new SharedNotes("", email, title, note, color, tag, "", editNoteId);
            apiService.editSharedNote(sharedNotes).enqueue(new Callback<JsonObject>() {
                //In case server responds.
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        Log.i("here:", "post submitted to API." + response.body().toString());
                        NoteAdapter.editNoteId = null;
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                        Toast.makeText(getActivity(), "Note edit successfully..!! ", Toast.LENGTH_SHORT).show();

                    } else if (response.code() == 500) {
                        Toast.makeText(getActivity(), "Some Error occured(Iternal ", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 404) {
                        Toast.makeText(getActivity(), "wrong..", Toast.LENGTH_SHORT).show();
                    }

                }

                //incase of failure to connect to server.
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    t.printStackTrace();
                    Log.e("here", "Unable to submit post to API.");
                    Toast.makeText(getActivity(), "failed ", Toast.LENGTH_SHORT).show();

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    //adds note in the server database.
    public void addNote(String email, String title, String note, String color, String tag) {


//HttpCLient to Add Authorization Header.
        OkHttpClient defaultHttpClient = new OkHttpClient.Builder()
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public okhttp3.Response intercept(Chain chain) throws IOException {
                                Request request = chain.request().newBuilder()
                                        .addHeader("authorization", "bearer " + serverToken).build();
                                return chain.proceed(request);
                            }
                        }).build();
        //Retrofit to retrieve JSON data from server.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiInterface.BASE_URL)
                .client(defaultHttpClient)
                .addConverterFactory(GsonConverterFactory.create())     //Using GSON to Convert JSON into POJO.
                .build();

        ApiInterface apiService = retrofit.create(ApiInterface.class);
        try {
            NoteList noteList = new NoteList(email, title, note, color, tag, "");
            apiService.addNote(noteList).enqueue(new Callback<NoteList>() {
                @Override
                public void onResponse(Call<NoteList> call, Response<NoteList> response) {
                    if (response.isSuccessful()) {
                        Log.i("here:", "post submitted to API." + response.body().toString());
                        NoteList noteList = response.body();
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                        Toast.makeText(getActivity(), "Notes..!! ", Toast.LENGTH_SHORT).show();

                    } else if (response.code() == 500) {
                        Toast.makeText(getActivity(), "Some Error occured(Iternal ", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 404) {
                        Toast.makeText(getActivity(), "wrong..", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<NoteList> call, Throwable t) {
                    t.printStackTrace();
                    Log.e("here", "Unable to submit post to API.");
                    Toast.makeText(getActivity(), "failed ", Toast.LENGTH_SHORT).show();

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}