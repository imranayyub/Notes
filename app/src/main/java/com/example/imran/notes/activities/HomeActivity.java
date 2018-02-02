package com.example.imran.notes.activities;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.imran.notes.fragments.AddNoteFragment;
import com.example.imran.notes.interfaces.ApiInterface;
import com.example.imran.notes.adapter.NoteAdapter;
import com.example.imran.notes.model.NoteList;
import com.example.imran.notes.R;
import com.example.imran.notes.model.SharedNotes;
import com.example.imran.notes.adapter.TagAdapter;
import com.google.firebase.database.DatabaseReference;


import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.imran.notes.activities.LoginActivity.email;
import static com.example.imran.notes.activities.LoginActivity.login;
import static com.example.imran.notes.activities.LoginActivity.loginPref;
import static com.example.imran.notes.activities.LoginActivity.serverToken;
import static com.example.imran.notes.activities.LoginActivity.userName;
import static com.example.imran.notes.activities.LoginActivity.userPic;
import static com.example.imran.notes.adapter.NoteAdapter.editNoteId;
import static com.example.imran.notes.adapter.NoteAdapter.pinnedNoteTag;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    public static SharedPreferences pinned;
    public static FloatingActionButton fab;
    ImageView imageView;
    TextView names, emails;
    FragmentManager manager = getFragmentManager();    //Initializing Fragment Manager.
    AddNoteFragment Fragment = new AddNoteFragment();

    Retrofit retrofit;
    OkHttpClient defaultHttpClient;

    //defining Variables.
    public static RecyclerView recyclerView, tagRecyclerView;
    public static ArrayList<NoteList> noteLists = new ArrayList<>();
    public static ArrayList<String> tagList = new ArrayList<>();
    public static NoteAdapter adapter;
    public static int isShared = 0;

    public static TextView pinnedNote, pinnedTitle, pinnedTag;
    public static CardView pinnedNoteLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //finding xml elements.
        pinnedNote = (TextView) findViewById(R.id.pinnedNote);
        pinnedTitle = (TextView) findViewById(R.id.pinnedtitle);
        pinnedTag = (TextView) findViewById(R.id.pinnedTag);
        pinnedNoteLayout = (CardView) findViewById(R.id.pinnedNoteLayout);

        //registers pinnedlayout for context menu.
        registerForContextMenu(pinnedNoteLayout);

        //checks if any not if already pinned to top or not
        try {
            pinned = getApplicationContext().getSharedPreferences("pinned", 0);
            if (pinned.getBoolean("isPinned", false) == true) {
                if (pinned.getString("pinnedEmail", "dfdsf").equals(email))
                    setPinnedNote(pinned.getString("pinnedNoteTitle", "ads"), pinned.getString("pinnedNote", "as"), pinned.getString("pinnedNoteTag", "asd"), pinned.getString("pinnedNoteColor", "asd"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        //setting data in Navigation Bar Header.
        imageView = (ImageView) header.findViewById(R.id.imageView);
        emails = (TextView) header.findViewById(R.id.emails);
        names = (TextView) header.findViewById(R.id.names);
        names.setText(userName);
        emails.setText(email);
        if (userPic.equals("Nopic")) {
//            imageView.setBackgroundResource(R.drawable.noic1);
        } else {
            Glide.with(getApplicationContext()).load(userPic)
                    .thumbnail(0.5f)
                    .crossFade()
                    .transform(new CircleTransform(HomeActivity.this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }


        //finds recyclerView in the xml.
        recyclerView = (RecyclerView) this.findViewById(R.id.recyclerView);
        tagRecyclerView = (RecyclerView) this.findViewById(R.id.tagRecyclerView);

        //takes to edit fragment id editNoteId is not null.
        if (editNoteId != null) {
            showAddNoteFragment();
        }

        //checks if Sharednotes tab is open.
        if(isShared==1)
        {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) pinnedNoteLayout.getLayoutParams();
            lp.height = 0;
            pinnedNoteLayout.setLayoutParams(lp);
            getSupportActionBar().setTitle("Shared Notes");
            fab.setVisibility(View.INVISIBLE);
            isShared = 1;
            showSharedNotes(email);
        }
        //render all the notes and tags.
        else
        showNotesAndTags();


    }

    //preforms suitable action in case back button is pressed.
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (Fragment != null) {
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.remove(Fragment);
                transaction.commit();
            }
            try {
                pinned = getApplicationContext().getSharedPreferences("pinned", 0);
                if (pinned.getBoolean("isPinned", false) == true) {
                    if (pinned.getString("pinnedEmail", "dfdsf").equals(email))
                        setPinnedNote(pinned.getString("pinnedNoteTitle", "ads"), pinned.getString("pinnedNote", "as"), pinned.getString("pinnedNoteTag", "asd"), pinned.getString("pinnedNoteColor", "asd"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            getSupportActionBar().setTitle("My Notes");
            fab.setVisibility(View.VISIBLE);
            isShared = 0;
            showNotesAndTags();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            //takes to home screen(My Notes in this case)
            case R.id.home: {
                try {
                    pinned = getApplicationContext().getSharedPreferences("pinned", 0);
                    if (pinned.getBoolean("isPinned", false) == true) {
                        if (pinned.getString("pinnedEmail", "dfdsf").equals(email))
                            setPinnedNote(pinned.getString("pinnedNoteTitle", "ads"), pinned.getString("pinnedNote", "as"), pinned.getString("pinnedNoteTag", "asd"), pinned.getString("pinnedNoteColor", "asd"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getSupportActionBar().setTitle("My Notes");
                fab.setVisibility(View.VISIBLE);
                isShared = 0;
                showNotesAndTags();
                break;
            }
            //shows notes being Shared with the user.
            case R.id.sharedNotes: {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) pinnedNoteLayout.getLayoutParams();
                lp.height = 0;
                pinnedNoteLayout.setLayoutParams(lp);
                getSupportActionBar().setTitle("Shared Notes");
                fab.setVisibility(View.INVISIBLE);
                isShared = 1;
                showSharedNotes(email);
                break;
            }
            // logs user out
            case R.id.logoutBn: {
                isShared = 0;
                logoutDialog();
                break;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //oncClick method to perform action according to the button being clicked.
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab: {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) pinnedNoteLayout.getLayoutParams();
                lp.height = 0;
                pinnedNoteLayout.setLayoutParams(lp);
                fab.setVisibility(View.INVISIBLE);
                showAddNoteFragment();
                break;

            }

        }
    }


    //shows AddnoteFragment.
    public void showAddNoteFragment() {
        ViewGroup.LayoutParams params = pinnedNoteLayout.getLayoutParams();
        params.height = 0;
        pinnedNoteLayout.setLayoutParams(params);
        getSupportActionBar().setTitle("Add Note");
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.mainxml, Fragment).commit();
        transaction.show(Fragment);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }


    //show dialog on logout.
    void logoutDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("Confirm Logout");

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want to Logout?");


        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                editNoteId = null;
                login = false;
                //putting login value as false.
                loginPref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                SharedPreferences.Editor editor = loginPref.edit();
                editor.putBoolean("login", login);
                editor.commit();

                LoginActivity loginActivity = new LoginActivity();
                loginActivity.signOut();

                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                //Starting LoginActivity.
                startActivity(intent);
//                finish();

            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();

    }


    public void showNotesAndTags() {
        //HttpCLient to Add Authorization Header.
        defaultHttpClient = new OkHttpClient.Builder()
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
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiInterface.BASE_URL)
                .client(defaultHttpClient)
                .addConverterFactory(GsonConverterFactory.create())     //Using GSON to Convert JSON into POJO.
                .build();

        ApiInterface apiService = retrofit.create(ApiInterface.class);
        try {
            NoteList noteList = new NoteList(email, "title", "note", "color", "tag", "");
            apiService.notes(noteList).enqueue(new Callback<List<NoteList>>() {
                //        apiService.savePost(username, password, phone).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<List<NoteList>> call, Response<List<NoteList>> response) {
                    if (response.isSuccessful()) {
                        Log.i("here:", "post submitted to API." + response.body().toString());
                        List<NoteList> noteList = response.body();
                        noteLists.clear();
                        tagList.clear();
                        for (NoteList n : noteList) {
                            Log.i("note", n.getNote());
                            noteLists.add(n);
                            if (!n.getTag().equals(""))
                                tagList.add(n.getTag());
                        }

                        //Initializing StaggeredGrideLayoutManager.
                        StaggeredGridLayoutManager staggeredGridLayoutManager;
                        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
                                StaggeredGridLayoutManager.VERTICAL);
                        recyclerView.setHasFixedSize(true);   //If the RecyclerView knows in advance that its size doesn't depend on the adapter content, then it will skip checking if its size should change every time an item is added or removed from the adapter.
                        recyclerView.setLayoutManager(staggeredGridLayoutManager);  //Setting StaggeredGridLayoutManager.
                        adapter = new NoteAdapter(HomeActivity.this, noteLists);
                        //setting Adapter On recyclerView.
                        recyclerView.setAdapter(adapter);

                        //setting adapter for Tag RecyclerView.
                        StaggeredGridLayoutManager tagStaggeredGridLayoutManager;
                        tagStaggeredGridLayoutManager = new StaggeredGridLayoutManager(1,
                                StaggeredGridLayoutManager.HORIZONTAL);
                        tagRecyclerView.setHasFixedSize(true);   //If the RecyclerView knows in advance that its size doesn't depend on the adapter content, then it will skip checking if its size should change every time an item is added or removed from the adapter.
                        tagRecyclerView.setLayoutManager(tagStaggeredGridLayoutManager);  //Displays recycler view in fragment.
                        TagAdapter tagAdapter = new TagAdapter(HomeActivity.this, tagList);
                        tagRecyclerView.setAdapter(tagAdapter);

                    } else if (response.code() == 500) {
                        Toast.makeText(getApplicationContext(), "Some Error occured(Iternal ", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 404) {
                        Toast.makeText(getApplicationContext(), "wrong..", Toast.LENGTH_SHORT).show();
                    }

                }

                //In case of failure to connect to server.
                @Override
                public void onFailure(Call<List<NoteList>> call, Throwable t) {
                    t.printStackTrace();
                    Log.e("here", "Unable to submit post to API.");
                    Toast.makeText(getApplicationContext(), "failed ", Toast.LENGTH_SHORT).show();

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //shows notes shared with User.
    public void showSharedNotes(String email) {

        //HttpCLient to Add Authorization Header.
        defaultHttpClient = new OkHttpClient.Builder()
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
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiInterface.BASE_URL)
                .client(defaultHttpClient)
                .addConverterFactory(GsonConverterFactory.create())     //Using GSON to Convert JSON into POJO.
                .build();

        ApiInterface apiService = retrofit.create(ApiInterface.class);
        try {
            SharedNotes sharedNotes = new SharedNotes("", email, "title", "note", "", "", "");
            apiService.sharedNote(sharedNotes).enqueue(new Callback<List<NoteList>>() {
                @Override
                public void onResponse(Call<List<NoteList>> call, Response<List<NoteList>> response) {
                    if (response.isSuccessful()) {
                        Log.i("here:", "post submitted to API." + response.body().toString());
                        List<NoteList> noteList = response.body();
                        tagList.clear();
                        noteLists.clear();
                        for (NoteList n : noteList) {
                            Log.i("note", n.getNote());
                            noteLists.add(n);
                            if (!n.getTag().equals(""))
                                tagList.add(n.getTag());
                        }

                        StaggeredGridLayoutManager staggeredGridLayoutManager;
                        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
                                StaggeredGridLayoutManager.VERTICAL);
                        recyclerView.setHasFixedSize(true);   //If the RecyclerView knows in advance that its size doesn't depend on the adapter content, then it will skip checking if its size should change every time an item is added or removed from the adapter.
                        recyclerView.setLayoutManager(staggeredGridLayoutManager);  //Displays recycler view in fragment.

                        adapter = new NoteAdapter(HomeActivity.this, noteLists);
                        recyclerView.setAdapter(adapter);

                        StaggeredGridLayoutManager tagStaggeredGridLayoutManager;
                        tagStaggeredGridLayoutManager = new StaggeredGridLayoutManager(1,
                                StaggeredGridLayoutManager.HORIZONTAL);
                        tagRecyclerView.setHasFixedSize(true);   //If the RecyclerView knows in advance that its size doesn't depend on the adapter content, then it will skip checking if its size should change every time an item is added or removed from the adapter.
                        tagRecyclerView.setLayoutManager(tagStaggeredGridLayoutManager);  //Displays recycler view in fragment.
                        TagAdapter tagAdapter = new TagAdapter(HomeActivity.this, tagList);
                        tagRecyclerView.setAdapter(tagAdapter);


                    } else if (response.code() == 500) {
                        Toast.makeText(getApplicationContext(), "Some Error occured(Iternal ", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 404) {
                        Toast.makeText(getApplicationContext(), "wrong..", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<List<NoteList>> call, Throwable t) {
                    t.printStackTrace();
                    Log.e("here", "Unable to submit post to API.");
                    Toast.makeText(getApplicationContext(), "failed ", Toast.LENGTH_SHORT).show();

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //sets data of pinned note to top layout.
    void setPinnedNote(String title, String note, String tag, String color) {
        pinnedNote.setText(note);
        pinnedTag.setText(tag);
        pinnedTitle.setText(title);
        if (color != null)
            pinnedNoteLayout.setBackgroundColor(Color.parseColor(color));
        ViewGroup.LayoutParams params = pinnedNoteLayout.getLayoutParams();
        params.height = 200;
        pinnedNoteLayout.setLayoutParams(params);
    }

    //creates contextmenu and add options.
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Options :");
        menu.add(0, v.getId(), 0, "Unpin from Top");
    }

    //In case any option is selected from Context menu.
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //unpins the note from top.
        if (item.getTitle() == "Unpin from Top") {
            pinned = this.getSharedPreferences("pinned", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pinned.edit();
            editor.putBoolean("isPinned", false);
            editor.commit();
            ViewGroup.LayoutParams params = pinnedNoteLayout.getLayoutParams();
            params.height = 0;
            pinnedNoteLayout.setLayoutParams(params);
        }
        return true;

    }
}
