package com.example.imran.notes;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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


import java.util.ArrayList;
import java.util.List;

import static com.example.imran.notes.LoginActivity.email;
import static com.example.imran.notes.LoginActivity.userName;
import static com.example.imran.notes.LoginActivity.userPic;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    ImageView imageView;
    TextView names, emails;
    Button newNote;
    FragmentManager manager = getFragmentManager();    //Initializing Fragment Manager.
    AddNoteFragment Fragment = new AddNoteFragment();

    RecyclerView recyclerView;
    static ArrayList<String> noteList = new ArrayList<>();
    static ArrayList<String> titleList = new ArrayList<>();
    public static int i = 0;

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
        Glide.with(getApplicationContext()).load(userPic)
                .thumbnail(0.5f)
                .crossFade()
                .transform(new CircleTransform(HomeActivity.this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);


        Bundle bundle;
        bundle = getIntent().getExtras();
        recyclerView = (RecyclerView) this.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);   //If the RecyclerView knows in advance that its size doesn't depend on the adapter content, then it will skip checking if its size should change every time an item is added or removed from the adapter.
        recyclerView.setLayoutManager(new LinearLayoutManager(this));  //Displays recycler view in fragment.
        try {
            if (bundle.getInt("add") == 1) {
                noteList.add(i, bundle.getString("Addnote"));
                titleList.add(i, bundle.getString("Title"));
                i++;
                //Setting data in recycler view.
                MyAdapter adapter = new MyAdapter(this, noteList,titleList);
//                registerForContextMenu(recyclerView);
                recyclerView.setAdapter(adapter);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.logoutBn) {
            // logs user out
            LoginActivity loginActivity = new LoginActivity();
            loginActivity.signOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            //Starting LoginActivity.
            startActivity(intent);
            //finish();

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

                getSupportActionBar().setTitle("Add Note");
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.mainxml, Fragment).commit();
                transaction.show(Fragment);
                break;
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

//    public void data(String note) {
//
////       note = bundle.getString("Addnote");
//        noteList.add(i, note);
//        i++;
//        //Setting data in recycler view.
//        MyAdapter adapter = new MyAdapter(HomeActivity.this, noteList);
//        recyclerView.setAdapter(adapter);
//
//    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        menu.setHeaderTitle("Options:");
//        menu.add(0, v.getId(), 0, "Remove Note");//groupId, itemId, order, title
//        menu.add(0, v.getId(), 0, "Mark as Very Important");
//        menu.add(0, v.getId(), 0, "Mark as Important");
//        menu.add(0, v.getId(), 0, "Cancel");
//    }
//
//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        if (item.getTitle() =="Remove Note"){
//            Toast.makeText(getApplicationContext(),"Removing Note",Toast.LENGTH_SHORT).show();
//        }
//        else   if (item.getTitle() =="Mark as Very Important"){
//            Toast.makeText(getApplicationContext(),"Very Important Note",Toast.LENGTH_SHORT).show();
//        }
//        else   if (item.getTitle() =="Mark as Important"){
//            Toast.makeText(getApplicationContext(),"Important Note",Toast.LENGTH_SHORT).show();
//        }
//        else   if (item.getTitle() =="Cancel"){
//            Toast.makeText(getApplicationContext(),"Cancel",Toast.LENGTH_SHORT).show();
//        }
//        else {
//            return false;
//        }
//        return true;
//    }


}
