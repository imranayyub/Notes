package com.example.imran.notes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Im on 21-11-2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {
    //this context we will use to inflate the layout
    Context context;

    //we are storing all the rides in a list
    private static ArrayList<NoteList> noteList;
    public static String editNoteId, editNoteTitle, editNote,editNotePrioirty;
    public static String priorityNoteId,priorityNote,priorityNoteTitle,priorityNotePriority;

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
        String notes, title;
        NoteList note = noteList.get(position);
        notes = note.getNote();
        title = note.getTitle();
        if(note.getPriority().equals("Important"))
         holder.cardViewLayout.setBackgroundColor(Color.parseColor("#FF4500"));
        if(note.getPriority().equals("Urgent"))
            holder.cardViewLayout.setBackgroundColor(Color.parseColor("#ff0000"));
        if(note.getPriority().equals("default"))
            holder.cardViewLayout.setBackgroundColor(Color.parseColor("#2196b3"));

        holder.note.setText("Note :" + notes);
        holder.title.setText("Title :" + title);

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
    public static class MyHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView note, title;
        CardView cardViewLayout;
        int p;
        String deleteNoteId;
        public Context context;

        public MyHolder(final View itemView) {
            super(itemView);
            note = (TextView) itemView.findViewById(R.id.adapterNote);
            title = (TextView) itemView.findViewById(R.id.title);
            cardViewLayout=(CardView)itemView.findViewById(R.id.cardviewlayout);
            context = itemView.getContext();

            //in case any Note is clicked.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    p = getLayoutPosition();
                    Toast.makeText(itemView.getContext(), "Edit Note", Toast.LENGTH_SHORT).show();
                    NoteList note = noteList.get(p);
                    editNoteId = note.getId();
                    editNote = note.getNote();
                    editNoteTitle = note.getTitle();
                    editNotePrioirty=note.getPriority();
                    Intent intent = new Intent(context, HomeActivity.class);
                    context.startActivity(intent);
//                    AddNoteFragment.editNote(editNoteId,editNoteTitle,editNote);
//                    HomeActivity.showAddNoteFragment();

                }
            });
            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//            super.onCreateContextMenu(menu, v, menuInfo);
            menu.setHeaderTitle("Options:");
            MenuItem remove = menu.add(0, v.getId(), 0, "Remove Note");//groupId, itemId, order, title
            MenuItem normal = menu.add(0, v.getId(), 0, "Mark as Normal");
            MenuItem vImp = menu.add(0, v.getId(), 0, "Mark as Important");
            MenuItem imp = menu.add(0, v.getId(), 0, "Mark as Urgent");
            MenuItem cancel = menu.add(0, v.getId(), 0, "Cancel");
            remove.setOnMenuItemClickListener(onEditMenu);
            normal.setOnMenuItemClickListener(onEditMenu);
            vImp.setOnMenuItemClickListener(onEditMenu);
            imp.setOnMenuItemClickListener(onEditMenu);
            cancel.setOnMenuItemClickListener(onEditMenu);
        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle() == "Remove Note") {


//                    p=itemView.getLayoutPosition;
                    p = getAdapterPosition();
                    NoteList note = noteList.get(p);
                    deleteNoteId = note.getId();
                    AddNoteFragment.removeNote(deleteNoteId);
                    Intent intent = new Intent(context, HomeActivity.class);
                    context.startActivity(intent);
                    Toast.makeText(itemView.getContext(), "Removing Note" + deleteNoteId, Toast.LENGTH_SHORT).show();

                } else if (item.getTitle() == "Mark as Normal") {
                    p = getAdapterPosition();
                    NoteList note = noteList.get(p);
                    priorityNoteId = note.getId();
                    priorityNote = note.getNote();
                    priorityNoteTitle = note.getTitle();
                    note.setPriority("Normal");
                    priorityNotePriority="Normal";
                    AddNoteFragment.editNote(priorityNoteId,priorityNoteTitle,priorityNote,priorityNotePriority);
                    Intent intent = new Intent(context, HomeActivity.class);
                    context.startActivity(intent);
                    Toast.makeText(itemView.getContext(), "Normal Note", Toast.LENGTH_SHORT).show();
                }
                else if (item.getTitle() == "Mark as Important") {
                    p = getAdapterPosition();
                    NoteList note = noteList.get(p);
                    priorityNoteId = note.getId();
                    priorityNote = note.getNote();
                    priorityNoteTitle = note.getTitle();
                    note.setPriority("Important");
                    priorityNotePriority="Important";
                    AddNoteFragment.editNote(priorityNoteId,priorityNoteTitle,priorityNote,priorityNotePriority);
                    Intent intent = new Intent(context, HomeActivity.class);
                    context.startActivity(intent);
                    Toast.makeText(itemView.getContext(), "Important Note", Toast.LENGTH_SHORT).show();
                }
                else if (item.getTitle() == "Mark as Urgent") {
                    p = getAdapterPosition();
                    NoteList note = noteList.get(p);
                    priorityNoteId = note.getId();
                    priorityNote = note.getNote();
                    priorityNoteTitle = note.getTitle();
                    note.setPriority("Important");
                    priorityNotePriority="Urgent";
                    AddNoteFragment.editNote(priorityNoteId,priorityNoteTitle,priorityNote,priorityNotePriority);
                    Intent intent = new Intent(context, HomeActivity.class);
                    context.startActivity(intent);
//
                    Toast.makeText(itemView.getContext(), "Urgent Note", Toast.LENGTH_SHORT).show();
                } else if (item.getTitle() == "Cancel") {
                    Toast.makeText(itemView.getContext(), "Cancel", Toast.LENGTH_SHORT).show();
                } else {

                    return true;
                }
                return false;
            }

        };

    }

}
