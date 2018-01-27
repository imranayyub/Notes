package com.example.imran.notes;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.imran.notes.HomeActivity.databaseNote;
import static com.example.imran.notes.HomeActivity.fab;
import static com.example.imran.notes.LoginActivity.email;
import static com.example.imran.notes.LoginActivity.serverToken;
import static com.example.imran.notes.LoginActivity.userName;
import static com.example.imran.notes.MyAdapter.editNote;
import static com.example.imran.notes.MyAdapter.editNoteId;
import static com.example.imran.notes.MyAdapter.editNoteTitle;

/**
 * Created by imran on 13/1/18.
 */

public class AddNoteFragment extends Fragment implements View.OnClickListener {

    EditText note, noteTitle, noteTag;
    Button addNote, cancel;
    String notes, title, tag,  color;
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
        note = (EditText) getActivity().findViewById(R.id.note);
        noteTag = (EditText) getActivity().findViewById(R.id.noteTag);
        noteTitle = (EditText) getActivity().findViewById(R.id.noteTitle);
        addNote = (Button) getActivity().findViewById(R.id.addNote);
        cancel = (Button) getActivity().findViewById(R.id.cancel);
        addColor = (FloatingActionButton) getActivity().findViewById(R.id.addColor);

        addNoteFrameLyout = (FrameLayout) getActivity().findViewById(R.id.addNoteFragment);
        fab.setVisibility(View.INVISIBLE);
        if (editNoteId != null) {
            note.setText(editNote);
            noteTitle.setText(editNoteTitle);
        }
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
                if (notes.length() == 0)
                    Toast.makeText(getActivity(), "Note Empty", Toast.LENGTH_SHORT).show();
                else if (editNoteId != null) {
//                    editNote(editNoteId, title, notes, tag);
                    Intent intent;
                    intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
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
            case R.id.cancel: {
                editNoteId = null;
                Intent intent;
//                fab.setVisibility(View.VISIBLE);
                intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
                break;

            }
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
//                Snackbar snackbar = Snackbar.make(addNoteFrameLyout, "It will set " +
//                        "your card view with default color", Snackbar.LENGTH_LONG);
//                snackbar.show();
//                color = null;
//                changecolor = color;
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


    //adds note in the Firebase realtime database.
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
            NoteList noteList = new NoteList(email, title, note, color, tag);
            apiService.addNote(noteList).enqueue(new Callback<NoteList>() {
                //        apiService.savePost(username, password, phone).enqueue(new Callback<User>() {
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

//    public static void editNote(String editId, String editNoteTitle, String editNote, String priority) {
//        editNoteId = null;
////        DatabaseReference editreference;
//        NoteList noteList = new NoteList(editId, editNoteTitle, editNote, priority);
////        editreference = FirebaseDatabase.getInstance().getReference("NoteList").child(editId);
//        databaseNote.child(editId).setValue(noteList);
////        Toast.makeText(getActivity(), "Updated Realtime Database!!!", Toast.LENGTH_SHORT).show();
//
//    }
//
//    public static void removeNote(String deleteNoteId) {
//        databaseNote.child(deleteNoteId).removeValue();
//
//    }
}
//onnotfiydatachanged.