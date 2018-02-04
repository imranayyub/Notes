package com.example.imran.notes.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.ImageHeaderParser;
import com.example.imran.notes.model.NoteList;
import com.example.imran.notes.R;
import com.example.imran.notes.model.SharedNotes;
import com.example.imran.notes.activities.HomeActivity;
import com.example.imran.notes.interfaces.ApiInterface;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.imran.notes.activities.HomeActivity.idList;
import static com.example.imran.notes.activities.HomeActivity.isOffline;
import static com.example.imran.notes.activities.HomeActivity.isShared;
import static com.example.imran.notes.activities.HomeActivity.noteLists;
import static com.example.imran.notes.activities.HomeActivity.pinned;
import static com.example.imran.notes.activities.HomeActivity.pinnedNote;
import static com.example.imran.notes.activities.HomeActivity.pinnedNoteLayout;
import static com.example.imran.notes.activities.HomeActivity.pinnedTag;
import static com.example.imran.notes.activities.HomeActivity.pinnedTag1;
import static com.example.imran.notes.activities.HomeActivity.pinnedTag2;
import static com.example.imran.notes.activities.HomeActivity.pinnedTitle;
import static com.example.imran.notes.activities.HomeActivity.recyclerView;
import static com.example.imran.notes.activities.HomeActivity.tagList;
import static com.example.imran.notes.activities.HomeActivity.tagRecyclerView;
import static com.example.imran.notes.activities.LoginActivity.email;
import static com.example.imran.notes.activities.LoginActivity.serverToken;

/**
 * Created by Im on 21-11-2017.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyHolder> {
    //context used to inflate the layout.
    Context context;


    public static ArrayList<NoteList> noteList;
    public static String editNoteId, editNoteTitle, editNote, editNoteTag, editNoteTag1, editNoteTag2, editNoteColor, pinnedNotes, pinnedNoteTag, pinnedNoteTag1, pinnedNoteTag2, pinnedNoteTitle, pinnedNoteColor;

    //getting the context and Notelist with constructor
    public NoteAdapter(Context context, ArrayList<NoteList> noteList) {
        this.context = (Context) context;
        this.noteList = noteList;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cardview, null);
        return new MyHolder(view);
    }

    NoteList notelists;

    //sets data in recyclerView.
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        //getting the ride of the specified position
        String notes, title, tags, color;
        notelists = noteList.get(position);
        notes = notelists.getNote();
        title = notelists.getTitle();
        tags = notelists.getTag();
        color = notelists.getColor();
        holder.tag.setText(tags);
        holder.tag1.setText(notelists.getTag1());
        holder.tag2.setText(notelists.getTag2());
        holder.note.setText(notes);
        holder.title.setText(title);
        if (color == null) {
            holder.cardViewLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        } else {
            holder.cardViewLayout.setBackgroundColor(Color.parseColor(color));
        }
//        if(isOffline==1)
        if (notelists.getStatus() != null) {
            if (!notelists.getStatus().equals("synced")) {
                holder.sync.setBackgroundColor(Color.parseColor("#ff0000"));
            }
        }
    }


    //Function to get the size of List noteList.
    @Override
    public int getItemCount() {
        return noteList.size();
    }


    //MyHolder class describes an item View and space with the recyclerView(Finds item within cardView Layout).
    public class MyHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView note, title, tag, tag1, tag2;
        CardView cardViewLayout;
        ImageView sync;
        int p;
        String deleteNoteId, deleteTag, deleteTag1, deleteTag2;
        public Context context;

        public MyHolder(final View itemView) {
            super(itemView);
            note = (TextView) itemView.findViewById(R.id.adapterNote);
            tag = (TextView) itemView.findViewById(R.id.adapterTag);
            tag1 = (TextView) itemView.findViewById(R.id.adapterTag1);
            tag2 = (TextView) itemView.findViewById(R.id.adapterTag2);
            title = (TextView) itemView.findViewById(R.id.title);
            sync = (ImageView) itemView.findViewById(R.id.sync);
            cardViewLayout = (CardView) itemView.findViewById(R.id.cardviewlayout);
            context = itemView.getContext();
            //registers itemView for ContextMenu.
            itemView.setOnCreateContextMenuListener(this);

        }


        //Create a context menu and add options in it.
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Options:");
            MenuItem edit = menu.add(0, v.getId(), 0, "Edit");
            if (isShared == 0) {
                MenuItem share = menu.add(0, v.getId(), 0, "Share");
                share.setOnMenuItemClickListener(onEditMenu);
                MenuItem ptt = menu.add(0, v.getId(), 0, "Pin to Top");
                ptt.setOnMenuItemClickListener(onEditMenu);

            }
            if (notelists.getStatus() != null) {
                if (!notelists.getStatus().equals("synced")) {
                    MenuItem syncOption = menu.add(0, v.getId(), 0, "Sync");
                    syncOption.setOnMenuItemClickListener(onEditMenu);
                }
            }
                MenuItem remove = menu.add(0, v.getId(), 0, "Delete Note");//groupId, itemId, order, title
                MenuItem cancel = menu.add(0, v.getId(), 0, "Cancel");
                remove.setOnMenuItemClickListener(onEditMenu);
                edit.setOnMenuItemClickListener(onEditMenu);
                cancel.setOnMenuItemClickListener(onEditMenu);
            }


        //In case an option is selected from Context Menu.
        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle() == "Delete Note") {
                    p = getAdapterPosition();
                    NoteList note = noteList.get(p);
                    //check if note is sharednote or normal note and performs suitable function.
                    if (isShared == 1)
                        deleteSharedNote(email, note.getNote());
                    else
                        deleteNote(email, note.getNote());
                    deleteTag = note.getTag();
                    deleteTag1 = note.getTag1();
                    deleteTag2 = note.getTag2();


                } else if (item.getTitle() == "Sync") {

                } else if (item.getTitle() == "Share") {
                    p = getLayoutPosition();
                    final NoteList note = noteList.get(p);
                    //creating alert dialog with text input to take email id of recipient.
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Share Note");
                    // I'm using fragment here so I'm using getView() to provide ViewGroup
                    // but you can provide here any other instance of ViewGroup from your Fragment / Activity
                    View viewInflated = LayoutInflater.from(context).inflate(R.layout.sharenote_layout, (ViewGroup) itemView, false);
                    // Set up the input
                    final EditText input = (EditText) viewInflated.findViewById(R.id.editText1);
                    final EditText input1 = (EditText) viewInflated.findViewById(R.id.editText2);
                    final EditText input2 = (EditText) viewInflated.findViewById(R.id.editText3);
                    final EditText input3 = (EditText) viewInflated.findViewById(R.id.editText4);


                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    builder.setView(viewInflated);

                    // Set up the buttons
                    //in case ok is pressed.
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            ArrayList<String> reciever_email = new ArrayList<>();
                            reciever_email.add(0, input.getText().toString());
                            reciever_email.add(1, input1.getText().toString());
                            reciever_email.add(2, input2.getText().toString());
                            reciever_email.add(3, input3.getText().toString());
                            //shares note with the recipent.
                            for (int i = 0; i < 4; i++) {
                                if (reciever_email.get(i) != null && !reciever_email.get(i).equals(""))
                                    shareNote(email, reciever_email.get(i), note.getTitle(), note.getNote(), note.getColor(), note.getTag(), note.getTag1(), note.getTag2());
                            }
                        }
                    });
                    //else cancel the dialog without any action.
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();

                } else if (item.getTitle() == "Edit") {
                    p = getLayoutPosition();
                    Toast.makeText(itemView.getContext(), "Edit Note", Toast.LENGTH_SHORT).show();
                    NoteList note = noteList.get(p);
                    //check if the note is shared or normal.
                    editNoteId = idList.get(p);
                    editNote = note.getNote();
                    editNoteTag = note.getTag();
                    editNoteTag1 = note.getTag1();
                    editNoteTag2 = note.getTag2();
                    editNoteTitle = note.getTitle();
                    editNoteColor = note.getColor();
                    Intent intent = new Intent(context, HomeActivity.class);
                    context.startActivity(intent);

                } else if (item.getTitle() == "Pin to Top") {
                    p = getLayoutPosition();
                    NoteList note = noteList.get(p);
                    pinnedNotes = note.getNote();
                    pinnedNoteTag = note.getTag();
                    pinnedNoteTag1 = note.getTag1();
                    pinnedNoteTag2 = note.getTag2();
                    pinnedNoteTitle = note.getTitle();
                    if (note.getColor() != null)
                        pinnedNoteColor = note.getColor();
                    else
                        pinnedNoteColor = "#ffffff";

                    //storing pinned note data in sharedPreferences.
                    pinned = context.getSharedPreferences("pinned", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pinned.edit();
                    editor.putBoolean("isPinned", true);
                    editor.putString("pinnedEmail", email);
                    editor.putString("pinnedNote", pinnedNotes);
                    editor.putString("pinnedNoteTag", pinnedNoteTag);
                    editor.putString("pinnedNoteTag1", pinnedNoteTag1);
                    editor.putString("pinnedNoteTag2", pinnedNoteTag2);
                    editor.putString("pinnedNoteTitle", pinnedNoteTitle);
                    editor.putString("pinnedNoteColor", pinnedNoteColor);
                    editor.commit();
                    setPinnedNote(pinnedNoteTitle, pinnedNotes, pinnedNoteTag, pinnedNoteTag1, pinnedNoteTag2, pinnedNoteColor);
                    notifyDataSetChanged();
                } else if (item.getTitle() == "Cancel") {
                    Toast.makeText(itemView.getContext(), "Cancel", Toast.LENGTH_SHORT).show();
                } else {

                    return true;
                }
                return false;
            }

        };

        //deletes Note from server.
        public void deleteNote(String email, String note) {


//HttpCLient to Add Authorization Header.
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
                NoteList noteList = new NoteList(email, "", note, "", "", "", "", "");
                apiService.deleteNote(noteList).enqueue(new Callback<JsonObject>() {
                    //        apiService.savePost(username, password, phone).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            Log.i("here:", "post submitted to API." + response.body().toString());
                            //removing note from notelists .
                            noteLists.remove(p);
                            //notifies adapter that an item is removed.
                            notifyItemRemoved(p);
                            tagList.remove(deleteTag);
                            tagList.remove(deleteTag1);
                            tagList.remove(deleteTag2);

                            //deletes the tag related to the note being deleted.
                            notifyItemRangeChanged(p, getItemCount());
                            StaggeredGridLayoutManager tagStaggeredGridLayoutManager;
                            tagStaggeredGridLayoutManager = new StaggeredGridLayoutManager(1,
                                    StaggeredGridLayoutManager.HORIZONTAL);
                            tagRecyclerView.setHasFixedSize(true);   //If the RecyclerView knows in advance that its size doesn't depend on the adapter content, then it will skip checking if its size should change every time an item is added or removed from the adapter.
                            tagRecyclerView.setLayoutManager(tagStaggeredGridLayoutManager);  //Displays recycler view in fragment.
                            TagAdapter tagAdapter = new TagAdapter(context, tagList);
                            tagRecyclerView.setAdapter(tagAdapter);

//                            Toast.makeText(itemView.getContext(), "Removing Note" + deleteNoteId, Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(itemView.getContext(), "error" + deleteNoteId, Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(itemView.getContext(), "note found" + deleteNoteId, Toast.LENGTH_SHORT).show();
                        }

                    }

                    //in case of failure to connection to server.
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.e("here", "Unable to submit post to API.");
                        Toast.makeText(itemView.getContext(), "failed ", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        //deletes sharedNotes from server.
        public void deleteSharedNote(String email, String note) {
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
                SharedNotes sharedNotes = new SharedNotes("", email, "", note, "", "", "", "", "");
                apiService.deleteSharedNote(sharedNotes).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            Log.i("here:", "post submitted to API." + response.body().toString());
                            noteLists.remove(p);
                            notifyItemRemoved(p);
                            tagList.remove(deleteTag);
                            tagList.remove(deleteTag1);
                            tagList.remove(deleteTag2);
                            notifyItemRangeChanged(p, getItemCount());
                            StaggeredGridLayoutManager tagStaggeredGridLayoutManager;
                            tagStaggeredGridLayoutManager = new StaggeredGridLayoutManager(1,
                                    StaggeredGridLayoutManager.HORIZONTAL);
                            tagRecyclerView.setHasFixedSize(true);   //If the RecyclerView knows in advance that its size doesn't depend on the adapter content, then it will skip checking if its size should change every time an item is added or removed from the adapter.
                            tagRecyclerView.setLayoutManager(tagStaggeredGridLayoutManager);  //Displays recycler view in fragment.
                            TagAdapter tagAdapter = new TagAdapter(context, tagList);
                            tagRecyclerView.setAdapter(tagAdapter);

//                            Toast.makeText(itemView.getContext(), "Removing Note" + deleteNoteId, Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(itemView.getContext(), "error" + deleteNoteId, Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(itemView.getContext(), "Note not found" + deleteNoteId, Toast.LENGTH_SHORT).show();
                        }

                    }

                    //IN case of failure to connect to server.
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.e("here", "Unable to submit post to API.");
                        //System.out.print("throwable" + t);
                        Toast.makeText(itemView.getContext(), "failed ", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }


        }


        //shares note in the server database with particular user.
        public void shareNote(String sender_email, final String reciever_email, String title, String note, String color, String tag, String tag1, String tag2) {

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
                SharedNotes sharedNotes = new SharedNotes(sender_email, reciever_email, title, note, color, tag, tag1, tag2, "");
                apiService.shareNote(sharedNotes).enqueue(new Callback<JsonObject>() {
                    //IN case server responds.
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            Log.i("here:", "post submitted to API." + response.body().toString());
                            Toast.makeText(itemView.getContext(), "shared Note with : " + reciever_email, Toast.LENGTH_SHORT).show();

                        } else if (response.code() == 500) {
                            Toast.makeText(itemView.getContext(), "Some Error occured(Iternal server error)", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(itemView.getContext(), "Not Found", Toast.LENGTH_SHORT).show();
                        }

                    }

                    // In case of Failure.
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        t.printStackTrace();
                        Log.e("here", "Unable to submit post to API.");
                        Toast.makeText(itemView.getContext(), "Email not register : " + reciever_email, Toast.LENGTH_SHORT).show();

                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        //sets data to the top layout for pinned Note.
        void setPinnedNote(String title, String note, String tag, String tag1, String tag2, String color) {
            pinnedNote.setText(note);
            pinnedTag.setText(tag);
            pinnedTag1.setText(tag1);
            pinnedTag2.setText(tag2);
            pinnedTitle.setText(title);
            if (color != null)
                pinnedNoteLayout.setBackgroundColor(Color.parseColor(color));
            ViewGroup.LayoutParams params = pinnedNoteLayout.getLayoutParams();
            params.height = 200;
        }

    }

}
