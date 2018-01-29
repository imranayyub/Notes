package com.example.imran.notes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.renderscript.Script;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.cast.LaunchOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;

import org.json.JSONObject;

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

import static com.example.imran.notes.HomeActivity.fab;
import static com.example.imran.notes.HomeActivity.noteLists;
import static com.example.imran.notes.HomeActivity.tagList;
import static com.example.imran.notes.HomeActivity.tagRecyclerView;
import static com.example.imran.notes.LoginActivity.email;
import static com.example.imran.notes.LoginActivity.serverToken;
import static com.example.imran.notes.MyFirebaseInstanceIDService.recent_token;

/**
 * Created by Im on 21-11-2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {
    //this context we will use to inflate the layout
    Context context;

    //we are storing all the rides in a list
    public static ArrayList<NoteList> noteList;
    public static String editNoteId, editNoteTitle, editNote, editNoteTag, editNoteColor;

    //getting the context and ride list with constructor
    public MyAdapter(Context context, ArrayList<NoteList> noteList) {
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

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        //getting the ride of the specified position
        String notes, title, tags, color;
        NoteList note = noteList.get(position);
        notes = note.getNote();
        title = note.getTitle();
        tags = note.getTag();
        color = note.getColor();
        holder.tag.setText(tags);
        holder.note.setText(notes);
        holder.title.setText(title);
        if (color == null) {
            holder.cardViewLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        } else {
            holder.cardViewLayout.setBackgroundColor(Color.parseColor(color));
        }
    }


//    // Custom method to get a random number between a range
//    protected int getRandomIntInRange(int max, int min){
//        return mRandom.nextInt((max-min)+min)+min;
//    }

    //Function to get the size of List noteList.
    @Override
    public int getItemCount() {
        return noteList.size();
    }


    //MyHolder class describes an item View and space with the recyclerView(Finds item within cardView Layout).
    public class MyHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView note, title, tag;
        CardView cardViewLayout;
        int p;
        String deleteNoteId, deleteTag;
        public Context context;

        public MyHolder(final View itemView) {
            super(itemView);
            note = (TextView) itemView.findViewById(R.id.adapterNote);
            tag = (TextView) itemView.findViewById(R.id.adapterTag);
            title = (TextView) itemView.findViewById(R.id.title);
            cardViewLayout = (CardView) itemView.findViewById(R.id.cardviewlayout);
            context = itemView.getContext();

            //in case any Note is clicked.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    p = getLayoutPosition();
//
//                    Toast.makeText(itemView.getContext(), "Edit Note", Toast.LENGTH_SHORT).show();
//                    NoteList note = noteList.get(p);
//                    editNoteId = note.getId();
//                    editNote = note.getNote();
//                    editNoteTitle = note.getTitle();
//                    editNotePrioirty = note.getPriority();
//                    Intent intent = new Intent(context, HomeActivity.class);
//                    context.startActivity(intent);
////                    AddNoteFragment.editNote(editNoteId,editNoteTitle,editNote);
////                    HomeActivity.showAddNoteFragment();

                }
            });
            itemView.setOnCreateContextMenuListener(this);

        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//            super.onCreateContextMenu(menu, v, menuInfo);
            menu.setHeaderTitle("Options:");
            MenuItem edit = menu.add(0, v.getId(), 0, "Edit");
            MenuItem share = menu.add(0, v.getId(), 0, "Share");
            MenuItem ptt = menu.add(0, v.getId(), 0, "Pin to Top");
            MenuItem remove = menu.add(0, v.getId(), 0, "Delete Note");//groupId, itemId, order, title
            MenuItem cancel = menu.add(0, v.getId(), 0, "Cancel");
            remove.setOnMenuItemClickListener(onEditMenu);
            edit.setOnMenuItemClickListener(onEditMenu);
            share.setOnMenuItemClickListener(onEditMenu);
            ptt.setOnMenuItemClickListener(onEditMenu);
            cancel.setOnMenuItemClickListener(onEditMenu);
        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle() == "Delete Note") {
                    p = getAdapterPosition();
                    NoteList note = noteList.get(p);
                    deleteNote(email, note.getNote());
                    deleteTag = note.getTag();
                    //                    Intent intent = new Intent(context, HomeActivity.class);
//                    context.startActivity(intent);
//                    Toast.makeText(itemView.getContext(), "Removing Note" + deleteNoteId, Toast.LENGTH_SHORT).show();

                } else if (item.getTitle() == "Share") {
                    p = getLayoutPosition();
                    final NoteList note = noteList.get(p);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Share Note");
                    // I'm using fragment here so I'm using getView() to provide ViewGroup
                    // but you can provide here any other instance of ViewGroup from your Fragment / Activity
                    View viewInflated = LayoutInflater.from(context).inflate(R.layout.share_alert, (ViewGroup) itemView, false);
                    // Set up the input
                    final EditText input = (EditText) viewInflated.findViewById(R.id.input);
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    builder.setView(viewInflated);

                    // Set up the buttons
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            String reciever_email = input.getText().toString();
//String firebaseToken=

                            shareNote(email, reciever_email, note.getTitle(), note.getNote(), note.getColor(), note.getTag(), FirebaseInstanceId.getInstance().getToken());
                        }
                    });
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
                    editNoteid(email, note.getNote());
                    editNote = note.getNote();
                    editNoteTag = note.getTag();
                    editNoteTitle = note.getTitle();
                    editNoteColor = note.getColor();
                } else if (item.getTitle() == "Pin to Top") {

                } else if (item.getTitle() == "Cancel") {
                    Toast.makeText(itemView.getContext(), "Cancel", Toast.LENGTH_SHORT).show();
                } else {

                    return true;
                }
                return false;
            }

        };

        public void deleteNote(String email, String note) {


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
                NoteList noteList = new NoteList(email, "", note, "", "", "");
                apiService.deleteNote(noteList).enqueue(new Callback<JsonObject>() {
                    //        apiService.savePost(username, password, phone).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            Log.i("here:", "post submitted to API." + response.body().toString());
//                            Intent intent = new Intent(context, HomeActivity.class);
                            noteLists.remove(p);
                            notifyItemRemoved(p);
                            tagList.remove(deleteTag);
                            notifyItemRangeChanged(p, getItemCount());
                            StaggeredGridLayoutManager tagStaggeredGridLayoutManager;
                            tagStaggeredGridLayoutManager = new StaggeredGridLayoutManager(1,
                                    StaggeredGridLayoutManager.HORIZONTAL);
                            tagRecyclerView.setHasFixedSize(true);   //If the RecyclerView knows in advance that its size doesn't depend on the adapter content, then it will skip checking if its size should change every time an item is added or removed from the adapter.
                            tagRecyclerView.setLayoutManager(tagStaggeredGridLayoutManager);  //Displays recycler view in fragment.
                            TagAdapter tagAdapter = new TagAdapter(context, tagList);
                            tagRecyclerView.setAdapter(tagAdapter);

                            Toast.makeText(itemView.getContext(), "Removing Note" + deleteNoteId, Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(itemView.getContext(), "error" + deleteNoteId, Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(itemView.getContext(), "note found" + deleteNoteId, Toast.LENGTH_SHORT).show();
                        }

                    }

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

        //shares note in the server database.
        public void shareNote(String sender_email, String reciever_email, String title, String note, String color, String tag,String fcmToken) {

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
                SharedNotes sharedNotes = new SharedNotes(sender_email, reciever_email, title, note, color, tag,fcmToken);
                apiService.shareNote(sharedNotes).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            Log.i("here:", "post submitted to API." + response.body().toString());
                            Toast.makeText(itemView.getContext(), "shared Note..!! ", Toast.LENGTH_SHORT).show();

                        } else if (response.code() == 500) {
                            Toast.makeText(itemView.getContext(), "Some Error occured(Iternal ", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(itemView.getContext(), "wrong..", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        t.printStackTrace();
                        Log.e("here", "Unable to submit post to API.");
                        Toast.makeText(itemView.getContext(), "failed to share note", Toast.LENGTH_SHORT).show();

                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        public void editNoteid(String email, String note) {


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
                NoteList noteList = new NoteList(email, "", note, "", "", "");
                apiService.editNoteId(noteList).enqueue(new Callback<NoteList>() {
                    //        apiService.savePost(username, password, phone).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<NoteList> call, Response<NoteList> response) {
                        if (response.isSuccessful()) {
                            Log.i("here:", "post submitted to API." + response.body().toString());
                            NoteList noteList = response.body();
                            editNoteId = noteList.getId();
                            Intent intent = new Intent(context, HomeActivity.class);
                            context.startActivity(intent);
//                           HomeActivity homeActivity=new HomeActivity();
//                           homeActivity.showNotesAndTags();

                            Toast.makeText(itemView.getContext(), "Removing Note" + deleteNoteId, Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(itemView.getContext(), "error" + deleteNoteId, Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(itemView.getContext(), "note found" + deleteNoteId, Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<NoteList> call, Throwable t) {
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


    }


}
