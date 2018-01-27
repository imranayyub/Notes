package com.example.imran.notes;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.imran.notes.LoginActivity.email;
import static com.example.imran.notes.LoginActivity.login;
import static com.example.imran.notes.LoginActivity.pref;
import static com.example.imran.notes.LoginActivity.serverToken;
import static com.example.imran.notes.LoginActivity.userName;
import static com.example.imran.notes.LoginActivity.userPic;
import static com.example.imran.notes.MyAdapter.editNoteId;
import static com.example.imran.notes.TagAdapter.byTags;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    //defining DatabaseReference object to send and retrieve data.
    public static DatabaseReference databaseNote;

    public static FloatingActionButton fab;
    ImageView imageView;
    TextView names, emails;
    Button newNote;
    FragmentManager manager = getFragmentManager();    //Initializing Fragment Manager.
    AddNoteFragment Fragment = new AddNoteFragment();

    Retrofit retrofit;
    OkHttpClient defaultHttpClient;

    public static RecyclerView recyclerView, tagRecyclerView;
    ArrayList<NoteList> noteLists = new ArrayList<>();
    //    ArrayList<String> tagList = new ArrayList<>();
//    ArrayList<NoteList> byTag = new ArrayList<>();
    public static MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        newNote = (Button) findViewById(R.id.newNote);
//        newNote.setOnClickListener(this);

        //getting date in day date month yy format.
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        System.out.print(date);
        Log.i("date", date);

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
            NoteList noteList = new NoteList(email, "title", "note", "color", "tag");
            apiService.notes(noteList).enqueue(new Callback<List<NoteList>>() {
                //        apiService.savePost(username, password, phone).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<List<NoteList>> call, Response<List<NoteList>> response) {
                    if (response.isSuccessful()) {
                        Log.i("here:", "post submitted to API." + response.body().toString());
                        List<NoteList> noteList = response.body();
                        for (NoteList n : noteList) {
                            Log.i("note", n.getNote());
                            noteLists.add(n);
                        }

                        StaggeredGridLayoutManager staggeredGridLayoutManager;
                        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
                                StaggeredGridLayoutManager.VERTICAL);
                        recyclerView.setHasFixedSize(true);   //If the RecyclerView knows in advance that its size doesn't depend on the adapter content, then it will skip checking if its size should change every time an item is added or removed from the adapter.
                        recyclerView.setLayoutManager(staggeredGridLayoutManager);  //Displays recycler view in fragment.

                        adapter = new MyAdapter(HomeActivity.this, noteLists);
//                registerForContextMenu(recyclerView);
                        recyclerView.setAdapter(adapter);
//

                        Toast.makeText(getApplicationContext(), "Notes..!! ", Toast.LENGTH_SHORT).show();

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

    //preforms suitable action in case back button is pressed.
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            onStart();
//            super.onBackPressed();
        }
    }

////    @Override
////    public boolean onCreateOptionsMenu(Menu menu) {
//         Inflate the menu; this adds items to the action bar if it is present.
////        getMenuInflater().inflate(R.menu.home, menu);
////        return true;
////    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
////        if (id == R.id.action_settings) {
////            return true;
////        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.home: {
                getSupportActionBar().setTitle("My Notes");
                fab.setVisibility(View.VISIBLE);
                onStart();
                break;
            }

            case R.id.sharedNotes: {
                getSupportActionBar().setTitle("Shared Notes");

                break;
            }
            case R.id.logoutBn: {
                // logs user out
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
//            case R.id.newNote: {
//
//                showAddNoteFragment();
//                break;
//            }
            case R.id.fab: {
//            Snackbar.make(v, "Take a note", Snackbar.LENGTH_LONG);
//                    .setAction("Action", null).show();
                fab.setVisibility(View.INVISIBLE);
                showAddNoteFragment();
                break;

            }

        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        //fetches data from Firebase realtime database.
//        try {
//            databaseNote.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    noteList.clear();
//                    for (DataSnapshot notesnapshot : dataSnapshot.getChildren()) {
//                        NoteList noteLists = notesnapshot.getValue(NoteList.class);
//                        noteList.add(noteLists);
//                        if (!noteLists.getTag().equals(""))
//                            tagList.add(noteLists.getTag());
//                    }
//
//                    StaggeredGridLayoutManager staggeredGridLayoutManager;
//                    staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
//                            StaggeredGridLayoutManager.VERTICAL);
//                    recyclerView.setHasFixedSize(true);   //If the RecyclerView knows in advance that its size doesn't depend on the adapter content, then it will skip checking if its size should change every time an item is added or removed from the adapter.
//                    recyclerView.setLayoutManager(staggeredGridLayoutManager);  //Displays recycler view in fragment.
//
//
//                    adapter = new MyAdapter(HomeActivity.this, noteList);
////                registerForContextMenu(recyclerView);
//                    recyclerView.setAdapter(adapter);
//
//                    StaggeredGridLayoutManager tagStaggeredGridLayoutManager;
//                    tagStaggeredGridLayoutManager = new StaggeredGridLayoutManager(1,
//                            StaggeredGridLayoutManager.HORIZONTAL);
//                    tagRecyclerView.setHasFixedSize(true);   //If the RecyclerView knows in advance that its size doesn't depend on the adapter content, then it will skip checking if its size should change every time an item is added or removed from the adapter.
//                    tagRecyclerView.setLayoutManager(tagStaggeredGridLayoutManager);  //Displays recycler view in fragment.
//                    TagAdapter tagAdapter = new TagAdapter(HomeActivity.this, tagList);
//                    tagRecyclerView.setAdapter(tagAdapter);
//                    if (byTags != null) {
//                        showByTag(byTags);
//                    }
//
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }
//    }

    //shows AddnoteFragment.
    public void showAddNoteFragment() {
        getSupportActionBar().setTitle("Add Note");
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.mainxml, Fragment).commit();
        transaction.show(Fragment);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);


    }

//    public void priorityNotes(final String priority) {
//        databaseNote.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                noteList.clear();
//                int i = 0;
//                for (DataSnapshot notesnapshot : dataSnapshot.getChildren()) {
//                    NoteList noteLists = notesnapshot.getValue(NoteList.class);
//                    if (noteLists.getPriority().equals(priority)) {
//                        noteList.add(i, noteLists);
//                        i++;
//                    }
//                }
//                StaggeredGridLayoutManager staggeredGridLayoutManager;
//                staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
//                        StaggeredGridLayoutManager.VERTICAL);
//                recyclerView.setHasFixedSize(true);   //If the RecyclerView knows in advance that its size doesn't depend on the adapter content, then it will skip checking if its size should change every time an item is added or removed from the adapter.
//                recyclerView.setLayoutManager(staggeredGridLayoutManager);  //Displays recycler view in fragment.
//
//                MyAdapter adapter = new MyAdapter(HomeActivity.this, noteList);
////                registerForContextMenu(recyclerView);
//                recyclerView.setAdapter(adapter);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    //show dialog on logout.
    void logoutDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("Confirm Logout");

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want to Logout?");

        // Setting Icon to Dialog
//        alertDialog.setIcon(R.drawable.warning);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                editNoteId = null;
                login = false;
                //putting login value as false.
                pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
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

//    public void showByTag(String tag) {
//        for (NoteList n : noteList) {
//            if ((n.getTag()).equals(tag)) {
//                byTag.add(n);
//            }
//        }
//        StaggeredGridLayoutManager staggeredGridLayoutManager;
//        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
//                StaggeredGridLayoutManager.VERTICAL);
//        recyclerView.setHasFixedSize(true);   //If the RecyclerView knows in advance that its size doesn't depend on the adapter content, then it will skip checking if its size should change every time an item is added or removed from the adapter.
//        recyclerView.setLayoutManager(staggeredGridLayoutManager);  //Displays recycler view in fragment.
//
//
//        MyAdapter adapter = new MyAdapter(HomeActivity.this, byTag);
////                registerForContextMenu(recyclerView);
//
//        recyclerView.setAdapter(adapter);
////        adapter.notifyDataSetChanged();
//        byTags = null;
//    }
}
