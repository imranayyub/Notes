package com.example.imran.notes;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ContextMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
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


import java.util.ArrayList;
import java.util.List;

import static com.example.imran.notes.LoginActivity.email;
import static com.example.imran.notes.LoginActivity.userId;
import static com.example.imran.notes.LoginActivity.userName;
import static com.example.imran.notes.LoginActivity.userPic;
import static com.example.imran.notes.MyAdapter.editNoteId;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    //defining DatabaseReference object to send and retrieve data.
    public static DatabaseReference databaseNote;

    ImageView imageView;
    TextView names, emails;
    Button newNote;
    FragmentManager manager = getFragmentManager();    //Initializing Fragment Manager.
    AddNoteFragment Fragment = new AddNoteFragment();

    RecyclerView recyclerView;
    ArrayList<NoteList> noteList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        newNote = (Button) findViewById(R.id.newNote);
        newNote.setOnClickListener(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
        if (userPic.equals("Nopic"))
            imageView.setBackgroundResource(R.drawable.noic1);
        else {
            Glide.with(getApplicationContext()).load(userPic)
                    .thumbnail(0.5f)
                    .crossFade()
                    .transform(new CircleTransform(HomeActivity.this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }

        //For staggered layout.
        //Initializing databaseReference object.
        databaseNote = FirebaseDatabase.getInstance().getReference("NoteList").child(userName);
        if (editNoteId != null) {
            showAddNoteFragment();
        }

        //finds recyclerView in the xml.
        recyclerView = (RecyclerView) this.findViewById(R.id.recyclerView);

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
            case R.id.home : {
                getSupportActionBar().setTitle("Home");
                onStart();
                break;
            }

            case R.id.importantBn: {
                getSupportActionBar().setTitle("Important Notes");
                prioirtyNotes("Important");
                break;
            }
            case R.id.urgentBn :{
                getSupportActionBar().setTitle("Urgent Notes");
                prioirtyNotes("Urgent");
          break;
            }

            case R.id.normalBn : {
                getSupportActionBar().setTitle("Normal Notes");
                prioirtyNotes("default");
                break;
            }
            case R.id.logoutBn: {
                // logs user out
                editNoteId=null;
                LoginActivity loginActivity = new LoginActivity();
                loginActivity.signOut();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                //Starting LoginActivity.
                startActivity(intent);
                //finish();
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
            case R.id.newNote: {

                showAddNoteFragment();
                break;
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        //fetches data from Firebase realtime database.
        try {
            databaseNote.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    noteList.clear();
                    for (DataSnapshot notesnapshot : dataSnapshot.getChildren()) {
                        NoteList noteLists = notesnapshot.getValue(NoteList.class);
                        noteList.add(noteLists);
                    }

                    StaggeredGridLayoutManager staggeredGridLayoutManager;
                    staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
                            StaggeredGridLayoutManager.VERTICAL);
                    recyclerView.setHasFixedSize(true);   //If the RecyclerView knows in advance that its size doesn't depend on the adapter content, then it will skip checking if its size should change every time an item is added or removed from the adapter.
                    recyclerView.setLayoutManager(staggeredGridLayoutManager);  //Displays recycler view in fragment.


                    MyAdapter adapter = new MyAdapter(HomeActivity.this, noteList);
//                registerForContextMenu(recyclerView);
                    recyclerView.setAdapter(adapter);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    //shows AddnoteFragment.
    public void showAddNoteFragment() {
        getSupportActionBar().setTitle("Add Note");
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.mainxml, Fragment).commit();
        transaction.show(Fragment);

    }
    public void prioirtyNotes(final String priority)
    {
        databaseNote.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                noteList.clear();
                int i = 0;
                for (DataSnapshot notesnapshot : dataSnapshot.getChildren()) {
                    NoteList noteLists = notesnapshot.getValue(NoteList.class);
                    if (noteLists.getPriority().equals(priority)) {
                        noteList.add(i,noteLists);
                        i++;
                    }
                }
                StaggeredGridLayoutManager staggeredGridLayoutManager;
                staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
                        StaggeredGridLayoutManager.VERTICAL);
                recyclerView.setHasFixedSize(true);   //If the RecyclerView knows in advance that its size doesn't depend on the adapter content, then it will skip checking if its size should change every time an item is added or removed from the adapter.
                recyclerView.setLayoutManager(staggeredGridLayoutManager);  //Displays recycler view in fragment.

                MyAdapter adapter = new MyAdapter(HomeActivity.this, noteList);
//                registerForContextMenu(recyclerView);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
